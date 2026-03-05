package tech.davidmartinezmuelas.gastrolink.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.davidmartinezmuelas.gastrolink.data.LocalJsonRepository
import tech.davidmartinezmuelas.gastrolink.data.OrderRepository
import tech.davidmartinezmuelas.gastrolink.data.RepositoryResult
import tech.davidmartinezmuelas.gastrolink.data.SettingsRepository
import tech.davidmartinezmuelas.gastrolink.data.ai.AiRecommendationServiceImpl
import tech.davidmartinezmuelas.gastrolink.data.local.GastroLinkDatabase
import tech.davidmartinezmuelas.gastrolink.data.local.OrderEntity
import tech.davidmartinezmuelas.gastrolink.data.local.OrderItemEntity
import tech.davidmartinezmuelas.gastrolink.data.local.ParticipantEntity
import tech.davidmartinezmuelas.gastrolink.data.local.ProfileEntity
import tech.davidmartinezmuelas.gastrolink.domain.GetRecommendationUseCase
import tech.davidmartinezmuelas.gastrolink.domain.NutritionCalculator
import tech.davidmartinezmuelas.gastrolink.domain.NutritionStatsCalculator
import tech.davidmartinezmuelas.gastrolink.domain.RecommendationResult
import tech.davidmartinezmuelas.gastrolink.domain.RecommendationSource
import tech.davidmartinezmuelas.gastrolink.BuildConfig
import tech.davidmartinezmuelas.gastrolink.model.ActivityLevel
import tech.davidmartinezmuelas.gastrolink.model.Branch
import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.Dish
import tech.davidmartinezmuelas.gastrolink.model.Entitlement
import tech.davidmartinezmuelas.gastrolink.model.Goal
import tech.davidmartinezmuelas.gastrolink.model.GroupNutritionProfile
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.Participant
import tech.davidmartinezmuelas.gastrolink.model.RecommendationContext
import tech.davidmartinezmuelas.gastrolink.model.Sex
import tech.davidmartinezmuelas.gastrolink.model.SoloNutritionProfile
import tech.davidmartinezmuelas.gastrolink.model.UserProfile
import java.util.UUID

data class AppUiState(
    val isLoading: Boolean = true,
    val loadErrorMessage: String? = null,
    val isHistoryLoading: Boolean = false,
    val isSavingOrder: Boolean = false,
    val historyErrorMessage: String? = null,
    val branches: List<Branch> = emptyList(),
    val dishes: List<Dish> = emptyList(),
    val entitlement: Entitlement = Entitlement.FREE,
    val orderMode: OrderMode? = null,
    val nutritionMode: NutritionMode? = null,
    val selectedBranch: Branch? = null,
    val participants: List<Participant> = emptyList(),
    val selectedParticipantId: String? = null,
    val soloProfile: SoloNutritionProfile = SoloNutritionProfile(),
    val groupProfiles: Map<String, GroupNutritionProfile> = emptyMap(),
    val cartItems: List<CartItem> = emptyList(),
    val orderHistory: List<OrderHistoryItemUi> = emptyList(),
    val selectedOrderDetail: OrderDetailUi? = null,
    val stats: StatsUi = StatsUi(),
    val useAiRecommendations: Boolean = false,
    val isRecommendationLoading: Boolean = false,
    val recommendationResult: RecommendationResult = RecommendationResult(
        source = RecommendationSource.NONE,
        messages = emptyList()
    )
)

data class SummaryUiState(
    val totals: NutritionTotals,
    val recommendations: List<String>,
    val totalsByParticipant: Map<String, NutritionTotals>,
    val recommendationSource: RecommendationSource,
    val isRecommendationLoading: Boolean
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val catalogRepository = LocalJsonRepository(application)
    private val orderRepository = OrderRepository(
        GastroLinkDatabase.getInstance(application).orderDao()
    )
    private val settingsRepository = SettingsRepository(application)
    private val getRecommendationUseCase = GetRecommendationUseCase(
        aiRecommendationService = AiRecommendationServiceImpl(baseUrl = BuildConfig.AI_BASE_URL),
        isAiEnabledByBuild = BuildConfig.AI_ENABLED,
        includeDebugInfo = BuildConfig.DEBUG
    )
    private val gson = Gson()
    private var recommendationJob: Job? = null

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    val summaryUiState: StateFlow<SummaryUiState> = _uiState
        .map { state ->
            val totals = NutritionCalculator.calculateTotals(state.cartItems)
            val totalsByParticipant = NutritionCalculator.calculateTotalsByParticipant(state.cartItems)
            SummaryUiState(
                totals = totals,
                recommendations = state.recommendationResult.messages,
                totalsByParticipant = totalsByParticipant,
                recommendationSource = state.recommendationResult.source,
                isRecommendationLoading = state.isRecommendationLoading
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SummaryUiState(
                totals = NutritionCalculator.calculateTotals(emptyList()),
                recommendations = emptyList(),
                totalsByParticipant = emptyMap(),
                recommendationSource = RecommendationSource.NONE,
                isRecommendationLoading = false
            )
        )

    init {
        loadCatalog()
        observeSettings()
        observeRecommendationInputs()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadErrorMessage = null) }

            when (val result = catalogRepository.loadCatalog()) {
                is RepositoryResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadErrorMessage = result.message
                        )
                    }
                }
                is RepositoryResult.Success -> {
                    val defaultBranch = result.data.branches.firstOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadErrorMessage = null,
                            branches = result.data.branches,
                            dishes = result.data.dishes,
                            selectedBranch = defaultBranch
                        )
                    }
                    loadOrderHistory()
                }
            }
        }
    }

    fun retryLoadCatalog() {
        loadCatalog()
    }

    fun isPremiumModeEnabled(): Boolean = _uiState.value.entitlement == Entitlement.PREMIUM_DEMO

    fun setPremiumDemoEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(
                entitlement = if (enabled) Entitlement.PREMIUM_DEMO else Entitlement.FREE,
                nutritionMode = if (!enabled && it.nutritionMode == NutritionMode.WITH_PROFILE) {
                    NutritionMode.WITHOUT_PROFILE
                } else {
                    it.nutritionMode
                }
            )
        }
    }

    fun setUseAiRecommendations(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseAiRecommendations(enabled)
        }
    }

    fun setOrderMode(mode: OrderMode) {
        _uiState.update { state ->
            val participants = if (mode == OrderMode.GROUP) {
                state.participants.ifEmpty { defaultParticipants() }
            } else {
                emptyList()
            }

            state.copy(
                orderMode = mode,
                nutritionMode = null,
                participants = participants,
                selectedParticipantId = participants.firstOrNull()?.id,
                groupProfiles = participants.associate { participant ->
                    participant.id to GroupNutritionProfile(participantId = participant.id)
                },
                cartItems = emptyList()
            )
        }
    }

    fun chooseNutritionWithoutProfile() {
        _uiState.update { it.copy(nutritionMode = NutritionMode.WITHOUT_PROFILE) }
    }

    fun chooseNutritionWithProfile(): Boolean {
        if (!isPremiumModeEnabled()) {
            return false
        }

        _uiState.update { it.copy(nutritionMode = NutritionMode.WITH_PROFILE) }
        return true
    }

    fun updateSoloProfile(
        age: String,
        sex: Sex?,
        heightCm: String,
        weightKg: String,
        goal: Goal?,
        activityLevel: ActivityLevel?,
        allergies: String
    ) {
        _uiState.update {
            it.copy(
                soloProfile = SoloNutritionProfile(
                    age = age.toIntOrNull(),
                    sex = sex,
                    heightCm = heightCm.toIntOrNull(),
                    weightKg = weightKg.toIntOrNull(),
                    goal = goal,
                    activityLevel = activityLevel,
                    allergies = allergies.trim()
                )
            )
        }
    }

    fun addParticipant() {
        _uiState.update { state ->
            val newName = "Guest ${state.participants.size + 1}"
            val participant = Participant(id = "p${state.participants.size + 1}", name = newName)
            state.copy(
                participants = state.participants + participant,
                selectedParticipantId = state.selectedParticipantId ?: participant.id,
                groupProfiles = state.groupProfiles + (participant.id to GroupNutritionProfile(participantId = participant.id))
            )
        }
    }

    fun removeParticipant(participantId: String) {
        _uiState.update { state ->
            val updatedParticipants = state.participants.filterNot { it.id == participantId }
            if (updatedParticipants.isEmpty()) {
                return@update state
            }

            state.copy(
                participants = updatedParticipants,
                selectedParticipantId = if (state.selectedParticipantId == participantId) {
                    updatedParticipants.first().id
                } else {
                    state.selectedParticipantId
                },
                groupProfiles = state.groupProfiles - participantId,
                cartItems = state.cartItems.filterNot { it.participantId == participantId }
            )
        }
    }

    fun renameParticipant(participantId: String, name: String) {
        _uiState.update { state ->
            state.copy(
                participants = state.participants.map { participant ->
                    if (participant.id == participantId) participant.copy(name = name) else participant
                }
            )
        }
    }

    fun selectParticipant(participantId: String) {
        _uiState.update { it.copy(selectedParticipantId = participantId) }
    }

    fun updateGroupProfile(
        participantId: String,
        allergies: String,
        preferences: String,
        generalGoal: Goal?
    ) {
        _uiState.update { state ->
            state.copy(
                groupProfiles = state.groupProfiles + (
                    participantId to GroupNutritionProfile(
                        participantId = participantId,
                        allergies = allergies.trim(),
                        preferences = preferences.trim(),
                        generalGoal = generalGoal
                    )
                )
            )
        }
    }

    fun selectBranch(branch: Branch) {
        _uiState.update { it.copy(selectedBranch = branch) }
    }

    fun addDishToCart(dish: Dish, participantId: String? = null) {
        val state = _uiState.value
        val participantForItem = if (state.orderMode == OrderMode.GROUP) {
            participantId ?: state.selectedParticipantId
        } else {
            null
        }
        val existing = state.cartItems.find {
            it.dish.id == dish.id && it.participantId == participantForItem
        }

        val updatedItems = if (existing == null) {
            state.cartItems + CartItem(dish = dish, qty = 1, participantId = participantForItem)
        } else {
            state.cartItems.map {
                if (it.dish.id == dish.id && it.participantId == participantForItem) {
                    it.copy(qty = it.qty + 1)
                } else {
                    it
                }
            }
        }

        _uiState.update { it.copy(cartItems = updatedItems) }
    }

    fun confirmOrder() {
        val state = _uiState.value
        val selectedBranch = state.selectedBranch ?: return
        if (state.cartItems.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingOrder = true, historyErrorMessage = null) }

            runCatching {
                val orderId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val orderEntity = OrderEntity(
                    id = orderId,
                    branchId = selectedBranch.id,
                    orderMode = mapOrderMode(state.orderMode),
                    nutritionMode = mapNutritionMode(state.nutritionMode),
                    createdAt = now
                )

                val participantIdMap = state.participants.associate { participant ->
                    participant.id to "${orderId}_${participant.id}"
                }

                val itemEntities = state.cartItems.map {
                    OrderItemEntity(
                        id = UUID.randomUUID().toString(),
                        orderId = orderId,
                        dishId = it.dish.id,
                        quantity = it.qty,
                        participantId = it.participantId?.let { participantId ->
                            participantIdMap[participantId] ?: participantId
                        }
                    )
                }

                val participantEntities = state.participants.map {
                    ParticipantEntity(
                        id = participantIdMap[it.id] ?: it.id,
                        orderId = orderId,
                        name = it.name
                    )
                }

                val profileEntity = buildProfileEntity(orderId, state)

                orderRepository.saveOrder(
                    order = orderEntity,
                    items = itemEntities,
                    participants = participantEntities,
                    profile = profileEntity
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        cartItems = emptyList(),
                        isSavingOrder = false
                    )
                }
                loadOrderHistory()
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSavingOrder = false,
                        historyErrorMessage = "No se pudo guardar el pedido"
                    )
                }
            }
        }
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHistoryLoading = true, historyErrorMessage = null) }

            runCatching {
                val orders = orderRepository.getOrders()
                val state = _uiState.value
                val dishesById = state.dishes.associateBy { it.id }
                val branchesById = state.branches.associateBy { it.id }

                val historyItems = orders.map { orderWithItems ->
                    val dishCount = orderWithItems.items.sumOf { it.quantity }
                    val totalCalories = orderWithItems.items.sumOf { item ->
                        (dishesById[item.dishId]?.kcal ?: 0) * item.quantity
                    }
                    OrderHistoryItemUi(
                        id = orderWithItems.order.id,
                        createdAt = orderWithItems.order.createdAt,
                        branchName = branchesById[orderWithItems.order.branchId]?.name ?: "Sucursal desconocida",
                        dishCount = dishCount,
                        totalCalories = totalCalories
                    )
                }

                val stats = NutritionStatsCalculator
                    .calculate(orders, dishesById)
                    .toStatsUi { dishId ->
                        dishesById[dishId]?.name ?: "No disponible"
                    }

                historyItems to stats
            }.onSuccess { (history, stats) ->
                _uiState.update {
                    it.copy(
                        isHistoryLoading = false,
                        orderHistory = history,
                        stats = stats
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isHistoryLoading = false,
                        historyErrorMessage = "No se pudo cargar el historial"
                    )
                }
            }
        }
    }

    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            runCatching {
                val detailRecord = orderRepository.getOrderDetails(orderId)
                val withItems = detailRecord.orderWithItems ?: return@runCatching null
                val state = _uiState.value
                val dishesById = state.dishes.associateBy { it.id }
                val branchesById = state.branches.associateBy { it.id }
                val participants = detailRecord
                    .orderWithParticipants
                    ?.participants
                    ?.map { Participant(id = it.id, name = it.name) }
                    .orEmpty()
                val participantNameById = participants.associateBy({ it.id }, { it.name })

                val itemsUi = withItems.items.map { item ->
                    OrderDetailItemUi(
                        dishName = dishesById[item.dishId]?.name ?: item.dishId,
                        quantity = item.quantity,
                        participantName = item.participantId?.let { participantNameById[it] ?: it }
                    )
                }

                val cartItems = withItems.items.mapNotNull { item ->
                    val dish = dishesById[item.dishId] ?: return@mapNotNull null
                    CartItem(
                        dish = dish,
                        qty = item.quantity,
                        participantId = item.participantId
                    )
                }

                val profileAudit = buildProfileAudit(
                    profile = detailRecord.profile,
                    canExposeRawProfile = BuildConfig.DEBUG || state.entitlement == Entitlement.PREMIUM_DEMO
                )

                OrderDetailUi(
                    id = withItems.order.id,
                    createdAt = withItems.order.createdAt,
                    branchName = branchesById[withItems.order.branchId]?.name ?: "Sucursal desconocida",
                    orderMode = withItems.order.orderMode,
                    nutritionMode = withItems.order.nutritionMode,
                    participants = participants,
                    items = itemsUi,
                    totals = NutritionCalculator.calculateTotals(cartItems),
                    profileType = profileAudit.profileType,
                    profileSummaryLines = profileAudit.summaryLines,
                    profileParseable = profileAudit.parseable,
                    rawProfileJson = profileAudit.rawJson,
                    canShowRawProfileJson = profileAudit.canExposeRaw
                )
            }.onSuccess { detail ->
                _uiState.update { it.copy(selectedOrderDetail = detail) }
            }.onFailure {
                _uiState.update { it.copy(historyErrorMessage = "No se pudo cargar el detalle del pedido") }
            }
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            runCatching {
                orderRepository.deleteOrder(orderId)
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        selectedOrderDetail = if (state.selectedOrderDetail?.id == orderId) {
                            null
                        } else {
                            state.selectedOrderDetail
                        }
                    )
                }
                loadOrderHistory()
            }.onFailure {
                _uiState.update { it.copy(historyErrorMessage = "No se pudo eliminar el pedido") }
            }
        }
    }

    fun increaseItem(dishId: String, participantId: String?) {
        val updatedItems = _uiState.value.cartItems.map {
            if (it.dish.id == dishId && it.participantId == participantId) {
                it.copy(qty = it.qty + 1)
            } else {
                it
            }
        }
        _uiState.update { it.copy(cartItems = updatedItems) }
    }

    fun decreaseItem(dishId: String, participantId: String?) {
        val updatedItems = _uiState.value.cartItems.mapNotNull {
            if (it.dish.id != dishId || it.participantId != participantId) {
                return@mapNotNull it
            }

            val newQty = it.qty - 1
            if (newQty <= 0) null else it.copy(qty = newQty)
        }

        _uiState.update { it.copy(cartItems = updatedItems) }
    }

    private fun buildRecommendationContext(state: AppUiState): RecommendationContext {
        val userProfile = when {
            state.nutritionMode != NutritionMode.WITH_PROFILE -> null
            state.orderMode == OrderMode.SOLO -> UserProfile.Solo(state.soloProfile)
            state.orderMode == OrderMode.GROUP -> UserProfile.Group(
                profiles = state.participants.map { participant ->
                    state.groupProfiles[participant.id] ?: GroupNutritionProfile(participant.id)
                }
            )
            else -> null
        }

        return RecommendationContext(
            orderMode = state.orderMode ?: OrderMode.SOLO,
            nutritionMode = state.nutritionMode ?: NutritionMode.WITHOUT_PROFILE,
            userProfile = userProfile
        )
    }

    private fun defaultParticipants(): List<Participant> {
        return listOf(
            Participant(id = "p1", name = "Guest 1"),
            Participant(id = "p2", name = "Guest 2")
        )
    }

    private fun mapOrderMode(orderMode: OrderMode?): String {
        return if (orderMode == OrderMode.GROUP) "GRUPO" else "SOLITARIO"
    }

    private fun mapNutritionMode(nutritionMode: NutritionMode?): String {
        return if (nutritionMode == NutritionMode.WITH_PROFILE) "CON_DATOS" else "SIN_DATOS"
    }

    private fun buildProfileEntity(orderId: String, state: AppUiState): ProfileEntity? {
        if (state.nutritionMode != NutritionMode.WITH_PROFILE) {
            return null
        }

        val (type, json) = if (state.orderMode == OrderMode.GROUP) {
            "GROUP_LIGHT" to gson.toJson(state.groupProfiles.values.toList())
        } else {
            "SOLO" to gson.toJson(state.soloProfile)
        }

        return ProfileEntity(
            id = UUID.randomUUID().toString(),
            orderId = orderId,
            profileType = type,
            dataJson = json
        )
    }

    private fun buildProfileAudit(
        profile: ProfileEntity?,
        canExposeRawProfile: Boolean
    ): ProfileAudit {
        if (profile == null) {
            return ProfileAudit(
                profileType = null,
                summaryLines = emptyList(),
                parseable = true,
                rawJson = null,
                canExposeRaw = false
            )
        }

        return runCatching {
            when (profile.profileType) {
                "SOLO" -> {
                    val parsed = gson.fromJson(profile.dataJson, SoloNutritionProfile::class.java)
                    ProfileAudit(
                        profileType = profile.profileType,
                        summaryLines = listOfNotNull(
                            parsed.age?.let { "Edad: $it" },
                            parsed.sex?.let { "Sexo: ${it.name}" },
                            parsed.goal?.let { "Objetivo: ${it.name}" },
                            parsed.activityLevel?.let { "Actividad: ${it.name}" },
                            parsed.allergies.takeIf { it.isNotBlank() }?.let { "Alergias: $it" }
                        ),
                        parseable = true,
                        rawJson = profile.dataJson,
                        canExposeRaw = canExposeRawProfile
                    )
                }
                "GROUP_LIGHT" -> {
                    val listType = object : TypeToken<List<GroupNutritionProfile>>() {}.type
                    val parsedList = gson.fromJson<List<GroupNutritionProfile>>(profile.dataJson, listType).orEmpty()
                    val summary = mutableListOf<String>()
                    parsedList.forEachIndexed { index, groupProfile ->
                        summary += "Participante ${index + 1}"
                        if (groupProfile.generalGoal != null) {
                            summary += "Objetivo: ${groupProfile.generalGoal.name}"
                        }
                        if (groupProfile.preferences.isNotBlank()) {
                            summary += "Preferencias: ${groupProfile.preferences}"
                        }
                        if (groupProfile.allergies.isNotBlank()) {
                            summary += "Alergias: ${groupProfile.allergies}"
                        }
                    }

                    ProfileAudit(
                        profileType = profile.profileType,
                        summaryLines = summary,
                        parseable = true,
                        rawJson = profile.dataJson,
                        canExposeRaw = canExposeRawProfile
                    )
                }
                else -> {
                    ProfileAudit(
                        profileType = profile.profileType,
                        summaryLines = listOf("Perfil guardado (tipo no reconocido)"),
                        parseable = false,
                        rawJson = profile.dataJson,
                        canExposeRaw = canExposeRawProfile
                    )
                }
            }
        }.getOrElse {
            ProfileAudit(
                profileType = profile.profileType,
                summaryLines = listOf("Perfil guardado (no parseable)"),
                parseable = false,
                rawJson = profile.dataJson,
                canExposeRaw = canExposeRawProfile
            )
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.useAiRecommendationsFlow.collect { enabled ->
                _uiState.update { it.copy(useAiRecommendations = enabled) }
            }
        }
    }

    private fun observeRecommendationInputs() {
        viewModelScope.launch {
            _uiState
                .map { state ->
                    RecommendationTrigger(
                        entitlement = state.entitlement,
                        nutritionMode = state.nutritionMode,
                        orderMode = state.orderMode,
                        cartItems = state.cartItems,
                        soloProfile = state.soloProfile,
                        groupProfiles = state.groupProfiles,
                        participants = state.participants,
                        useAiRecommendations = state.useAiRecommendations
                    )
                }
                .distinctUntilChanged()
                .collect {
                    refreshRecommendations()
                }
        }
    }

    private fun refreshRecommendations() {
        recommendationJob?.cancel()
        recommendationJob = viewModelScope.launch {
            val state = _uiState.value
            val context = buildRecommendationContext(state)
            val totals = NutritionCalculator.calculateTotals(state.cartItems)

            if (state.cartItems.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isRecommendationLoading = false,
                        recommendationResult = RecommendationResult(
                            source = RecommendationSource.NONE,
                            messages = emptyList()
                        )
                    )
                }
                return@launch
            }

            val shouldTryAi = state.useAiRecommendations &&
                state.entitlement != Entitlement.FREE &&
                context.nutritionMode == NutritionMode.WITH_PROFILE

            if (shouldTryAi) {
                _uiState.update { it.copy(isRecommendationLoading = true) }
            }

            val result = getRecommendationUseCase.execute(
                totals = totals,
                cartItems = state.cartItems,
                context = context,
                entitlement = state.entitlement,
                useAiEnabledByUser = state.useAiRecommendations
            )

            _uiState.update {
                it.copy(
                    isRecommendationLoading = false,
                    recommendationResult = result
                )
            }
        }
    }
}

private data class ProfileAudit(
    val profileType: String?,
    val summaryLines: List<String>,
    val parseable: Boolean,
    val rawJson: String?,
    val canExposeRaw: Boolean
)

private data class RecommendationTrigger(
    val entitlement: Entitlement,
    val nutritionMode: NutritionMode?,
    val orderMode: OrderMode?,
    val cartItems: List<CartItem>,
    val soloProfile: SoloNutritionProfile,
    val groupProfiles: Map<String, GroupNutritionProfile>,
    val participants: List<Participant>,
    val useAiRecommendations: Boolean
)

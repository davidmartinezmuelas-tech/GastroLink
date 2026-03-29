package tech.davidmartinezmuelas.gastrolink.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// Navigation flow:
//
//   START_MODE
//     ├─ Solo  ──► NUTRITION_MODE
//     └─ Group ──► NUTRITION_MODE
//
//   NUTRITION_MODE
//     ├─ WithoutProfile ──► BRANCH ──► MENU ──► CART ──► SUMMARY
//     └─ WithProfile    ──► PROFILE ──► BRANCH ──► MENU ──► CART ──► SUMMARY
//
//   SUMMARY
//     └─ confirmOrder ──► HISTORY (pops entire order flow back to START_MODE)
//
//   HISTORY ──► DETAIL/{orderId}
//   HISTORY ──► STATS
//
//   Any screen ──► SETTINGS ──► PLANS
//   SETTINGS ──► HISTORY
//   SETTINGS ── navigateStart ──► START_MODE (clears full back stack)
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.ui.AppViewModel
import tech.davidmartinezmuelas.gastrolink.ui.BuildInfoProvider
import tech.davidmartinezmuelas.gastrolink.ui.screens.BranchScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.CartScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.MenuScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.NutritionModeScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.OrderDetailScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.OrderHistoryScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.PlansScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.ProfileScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.SettingsScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.StartModeScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.StatsScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.SummaryScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val summaryState by viewModel.summaryUiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppRoute.START_MODE,
        modifier = modifier
    ) {
        composable(AppRoute.START_MODE) {
            StartModeScreen(
                selectedMode = state.orderMode,
                savedProfiles = state.savedProfiles,
                hasProfileData = viewModel.hasSoloProfileData(),
                onSelectSolo = {
                    viewModel.setOrderMode(OrderMode.SOLO)
                    navController.navigate(AppRoute.NUTRITION_MODE)
                },
                onSelectGroup = {
                    viewModel.setOrderMode(OrderMode.GROUP)
                    navController.navigate(AppRoute.NUTRITION_MODE)
                },
                onOpenSettings = { navController.navigate(AppRoute.SETTINGS) },
                onManageProfiles = {
                    viewModel.setOrderMode(OrderMode.SOLO)
                    navController.navigate(AppRoute.PROFILE_MANAGE)
                }
            )
        }

        composable(AppRoute.NUTRITION_MODE) {
            NutritionModeScreen(
                isPremiumEnabled = viewModel.isPremiumModeEnabled(),
                savedProfiles = state.savedProfiles,
                onChooseWithoutProfile = {
                    viewModel.chooseNutritionWithoutProfile()
                    navController.navigate(AppRoute.BRANCH)
                },
                onChooseWithProfile = {
                    val allowed = viewModel.chooseNutritionWithProfile()
                    if (allowed) {
                        if (state.orderMode == OrderMode.SOLO && viewModel.hasSoloProfileData()) {
                            navController.navigate(AppRoute.BRANCH)
                        } else {
                            navController.navigate(AppRoute.PROFILE)
                        }
                    }
                },
                onSelectProfile = { profileId ->
                    viewModel.loadSavedProfile(profileId)
                },
                onContinueWithoutProfile = {
                    viewModel.chooseNutritionWithoutProfile()
                    navController.navigate(AppRoute.BRANCH)
                },
                onOpenPlans = { navController.navigate(AppRoute.PLANS) },
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(AppRoute.SETTINGS) }
            )
        }

        composable(AppRoute.PROFILE) {
            ProfileScreen(
                orderMode = state.orderMode,
                soloProfile = state.soloProfile,
                savedProfiles = state.savedProfiles,
                profileResetKey = state.profileResetKey,
                participants = state.participants,
                groupProfiles = state.groupProfiles,
                onSaveSoloProfile = viewModel::updateSoloProfile,
                onSaveProfileAs = viewModel::saveCurrentProfileAs,
                onLoadSavedProfile = viewModel::loadSavedProfile,
                onDeleteSavedProfile = viewModel::deleteSavedProfile,
                onAddParticipant = viewModel::addParticipant,
                onRemoveParticipant = viewModel::removeParticipant,
                onRenameParticipant = viewModel::renameParticipant,
                onUpdateGroupProfile = viewModel::updateGroupProfile,
                onContinue = { navController.navigate(AppRoute.BRANCH) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.BRANCH) {
            BranchScreen(
                isLoading = state.isLoading,
                errorMessage = state.loadErrorMessage,
                branches = state.branches,
                selectedBranchId = state.selectedBranch?.id,
                onRetry = viewModel::retryLoadCatalog,
                onSelectBranch = {
                    viewModel.selectBranch(it)
                    navController.navigate(AppRoute.MENU)
                },
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(AppRoute.SETTINGS) }
            )
        }

        composable(AppRoute.MENU) {
            MenuScreen(
                orderMode = state.orderMode,
                selectedBranchName = state.selectedBranch?.name,
                dishes = state.dishes,
                cartItems = state.cartItems,
                participants = state.participants,
                selectedParticipantId = state.selectedParticipantId,
                onSelectParticipant = viewModel::selectParticipant,
                onAddDish = { dish, participantId -> viewModel.addDishToCart(dish, participantId) },
                onGoToCart = { navController.navigate(AppRoute.CART) },
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(AppRoute.SETTINGS) }
            )
        }

        composable(AppRoute.CART) {
            CartScreen(
                orderMode = state.orderMode,
                items = state.cartItems,
                participants = state.participants,
                onIncrease = { dishId, participantId -> viewModel.increaseItem(dishId, participantId) },
                onDecrease = { dishId, participantId -> viewModel.decreaseItem(dishId, participantId) },
                onGoToSummary = { navController.navigate(AppRoute.SUMMARY) },
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(AppRoute.SETTINGS) }
            )
        }

        composable(AppRoute.SUMMARY) {
            SummaryScreen(
                nutritionMode = state.nutritionMode ?: NutritionMode.WITHOUT_PROFILE,
                totals = summaryState.totals,
                recommendations = summaryState.recommendations,
                recommendationSource = summaryState.recommendationSource,
                isRecommendationLoading = summaryState.isRecommendationLoading,
                participants = state.participants,
                totalsByParticipant = summaryState.totalsByParticipant,
                isSavingOrder = state.isSavingOrder,
                onConfirmOrder = {
                    viewModel.confirmOrder()
                    navController.navigate(AppRoute.HISTORY) {
                        popUpTo(AppRoute.START_MODE) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(AppRoute.SETTINGS) }
            )
        }

        composable(AppRoute.SETTINGS) {
            SettingsScreen(
                isPremiumDemoEnabled = viewModel.isPremiumModeEnabled(),
                canUseAiRecommendations = viewModel.canUseAiRecommendations(),
                nutritionMode = state.nutritionMode,
                useAiRecommendations = state.useAiRecommendations,
                buildInfo = BuildInfoProvider.current(),
                onTogglePremiumDemo = viewModel::setPremiumDemoEnabled,
                onToggleUseAiRecommendations = viewModel::setUseAiRecommendations,
                onOpenPlans = { navController.navigate(AppRoute.PLANS) },
                onExportHistory = viewModel::exportOrderHistory,
                onDeleteAllData = viewModel::wipeAllLocalData,
                onOpenHistory = {
                    viewModel.loadOrderHistory()
                    navController.navigate(AppRoute.HISTORY)
                },
                onNavigateStart = {
                    navController.navigate(AppRoute.START_MODE) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.PLANS) {
            PlansScreen(
                isPremiumEnabled = viewModel.isPremiumModeEnabled(),
                isDebugBuild = tech.davidmartinezmuelas.gastrolink.BuildConfig.DEBUG,
                onActivatePremiumDemo = { viewModel.setPremiumDemoEnabled(true) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.HISTORY) {
            OrderHistoryScreen(
                isLoading = state.isHistoryLoading,
                errorMessage = state.historyErrorMessage,
                orders = state.orderHistory,
                onRefresh = viewModel::loadOrderHistory,
                onExportHistory = viewModel::exportOrderHistory,
                onViewDetails = { orderId ->
                    navController.navigate("${AppRoute.DETAIL}/$orderId")
                },
                onOpenStats = { navController.navigate(AppRoute.STATS) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoute.DETAIL_WITH_ARG,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            LaunchedEffect(orderId) {
                viewModel.clearSelectedOrderDetail()
                if (!orderId.isNullOrBlank()) {
                    viewModel.loadOrderDetails(orderId)
                }
            }

            OrderDetailScreen(
                detail = state.selectedOrderDetail,
                onDeleteOrder = { deleteId ->
                    viewModel.deleteOrder(deleteId)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.STATS) {
            StatsScreen(
                stats = state.stats,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.PROFILE_MANAGE) {
            ProfileScreen(
                orderMode = OrderMode.SOLO,
                soloProfile = state.soloProfile,
                savedProfiles = state.savedProfiles,
                profileResetKey = state.profileResetKey,
                isStandalone = true,
                participants = emptyList(),
                groupProfiles = emptyMap(),
                onSaveSoloProfile = viewModel::updateSoloProfile,
                onSaveProfileAs = viewModel::saveCurrentProfileAs,
                onLoadSavedProfile = viewModel::loadSavedProfile,
                onDeleteSavedProfile = viewModel::deleteSavedProfile,
                onAddParticipant = {},
                onRemoveParticipant = {},
                onRenameParticipant = { _, _ -> },
                onUpdateGroupProfile = { _, _, _, _ -> },
                onContinue = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

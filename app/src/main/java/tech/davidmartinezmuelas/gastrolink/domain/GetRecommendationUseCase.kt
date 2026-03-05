package tech.davidmartinezmuelas.gastrolink.domain

import tech.davidmartinezmuelas.gastrolink.data.ai.AiRecommendationRequest
import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.Entitlement
import tech.davidmartinezmuelas.gastrolink.model.GroupNutritionProfile
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals
import tech.davidmartinezmuelas.gastrolink.model.OrderMode
import tech.davidmartinezmuelas.gastrolink.model.RecommendationContext
import tech.davidmartinezmuelas.gastrolink.model.SoloNutritionProfile
import tech.davidmartinezmuelas.gastrolink.model.UserProfile

class GetRecommendationUseCase(
    private val aiRecommendationService: AiRecommendationService,
    private val isAiEnabledByBuild: Boolean,
    private val includeDebugInfo: Boolean
) {

    suspend fun execute(
        totals: NutritionTotals,
        cartItems: List<CartItem>,
        context: RecommendationContext,
        entitlement: Entitlement,
        useAiEnabledByUser: Boolean
    ): RecommendationResult {
        if (context.nutritionMode != NutritionMode.WITH_PROFILE) {
            return RecommendationResult(RecommendationSource.NONE, emptyList())
        }

        if (entitlement == Entitlement.FREE) {
            return RecommendationResult(RecommendationSource.NONE, emptyList())
        }

        val localFallback = RecommendationEngineLocal.generateRecommendations(
            totals = totals,
            context = context
        ).take(2)

        if (!useAiEnabledByUser || !isAiEnabledByBuild) {
            return RecommendationResult(
                source = RecommendationSource.LOCAL_RULES,
                messages = localFallback,
                debugInfo = debugMessage("IA desactivada por usuario o build")
            )
        }

        val request = buildRequest(totals, cartItems, context)

        return runCatching {
            aiRecommendationService.generate(request)
        }.map { response ->
            val aiMessages = sanitizeAiMessages(response.recommendationText)
            if (aiMessages.isEmpty()) {
                RecommendationResult(
                    source = RecommendationSource.LOCAL_RULES,
                    messages = localFallback,
                    debugInfo = debugMessage("Respuesta IA vacia o invalida")
                )
            } else {
                RecommendationResult(
                    source = RecommendationSource.AI,
                    messages = aiMessages,
                    debugInfo = debugMessage("Modelo: ${response.model ?: "unknown"}")
                )
            }
        }.getOrElse {
            RecommendationResult(
                source = RecommendationSource.LOCAL_RULES,
                messages = localFallback,
                debugInfo = debugMessage("Fallback por error de IA")
            )
        }
    }

    private fun buildRequest(
        totals: NutritionTotals,
        cartItems: List<CartItem>,
        context: RecommendationContext
    ): AiRecommendationRequest {
        val dishes = cartItems.map { item ->
            AiRecommendationRequest.DishPayload(
                name = item.dish.name,
                qty = item.qty,
                kcal = item.dish.kcal,
                proteinG = item.dish.proteinG,
                carbsG = item.dish.carbsG,
                fatG = item.dish.fatG
            )
        }

        val profile = context.userProfile?.let { userProfile ->
            when (userProfile) {
                is UserProfile.Solo -> soloProfilePayload(userProfile.profile)
                is UserProfile.Group -> groupProfilePayload(userProfile.profiles)
            }
        }

        return AiRecommendationRequest(
            orderMode = context.orderMode.name,
            nutritionMode = context.nutritionMode.name,
            totals = AiRecommendationRequest.TotalsPayload(
                kcal = totals.kcal,
                proteinG = totals.proteinG,
                carbsG = totals.carbsG,
                fatG = totals.fatG
            ),
            dishes = dishes,
            profile = profile
        )
    }

    private fun soloProfilePayload(profile: SoloNutritionProfile): AiRecommendationRequest.ProfilePayload {
        return AiRecommendationRequest.ProfilePayload(
            type = "SOLO",
            summary = mapOf(
                "age" to profile.age,
                "sex" to profile.sex?.name,
                "goal" to profile.goal?.name,
                "activityLevel" to profile.activityLevel?.name,
                "allergies" to profile.allergies
            )
        )
    }

    private fun groupProfilePayload(profiles: List<GroupNutritionProfile>): AiRecommendationRequest.ProfilePayload {
        return AiRecommendationRequest.ProfilePayload(
            type = "GROUP_LIGHT",
            summary = mapOf(
                "participants" to profiles.map {
                    mapOf(
                        "participantId" to it.participantId,
                        "allergies" to it.allergies,
                        "preferences" to it.preferences,
                        "generalGoal" to it.generalGoal?.name
                    )
                }
            )
        )
    }

    private fun sanitizeAiMessages(text: String): List<String> {
        if (text.isBlank() || text.length > 500) {
            return emptyList()
        }

        return text
            .split(Regex("[.!?\\n]+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(2)
    }

    private fun debugMessage(message: String): String? {
        return if (includeDebugInfo) message else null
    }
}

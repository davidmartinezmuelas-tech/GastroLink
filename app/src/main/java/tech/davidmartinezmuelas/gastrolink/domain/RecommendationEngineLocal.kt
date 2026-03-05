package tech.davidmartinezmuelas.gastrolink.domain

import tech.davidmartinezmuelas.gastrolink.model.Goal
import tech.davidmartinezmuelas.gastrolink.model.NutritionMode
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals
import tech.davidmartinezmuelas.gastrolink.model.RecommendationContext
import tech.davidmartinezmuelas.gastrolink.model.UserProfile

object RecommendationEngineLocal {

    fun generateRecommendations(
        totals: NutritionTotals,
        context: RecommendationContext
    ): List<String> {
        if (context.nutritionMode != NutritionMode.WITH_PROFILE) {
            return emptyList()
        }

        val kcalHighThreshold = when (context.userProfile) {
            is UserProfile.Solo -> {
                when (context.userProfile.profile.goal) {
                    Goal.LOSE_WEIGHT -> 750
                    Goal.GAIN_MUSCLE -> 1100
                    else -> 900
                }
            }
            is UserProfile.Group -> 1100
            else -> 900
        }

        val proteinLowThreshold = when (context.userProfile) {
            is UserProfile.Solo -> {
                when (context.userProfile.profile.goal) {
                    Goal.GAIN_MUSCLE -> 40
                    else -> 30
                }
            }
            is UserProfile.Group -> 45
            else -> 30
        }

        val messages = mutableListOf<String>()

        if (totals.kcal > kcalHighThreshold) {
            messages += "Pedido alto en calorias para el objetivo seleccionado"
        }

        if (totals.proteinG < proteinLowThreshold) {
            messages += "Proteina total baja para el perfil indicado"
        }

        if (totals.fatG > 40) {
            messages += "Grasas elevadas, considera balancear con platos mas ligeros"
        }

        if (messages.isEmpty()) {
            messages += "Pedido equilibrado segun las reglas locales"
        }

        return messages.take(2)
    }
}

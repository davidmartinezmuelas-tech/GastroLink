package tech.davidmartinezmuelas.gastrolink.domain

import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals

object NutritionCalculator {

    fun calculateTotals(items: List<CartItem>): NutritionTotals {
        val kcal = items.sumOf { it.dish.kcal * it.qty }
        val protein = items.sumOf { it.dish.proteinG * it.qty }
        val carbs = items.sumOf { it.dish.carbsG * it.qty }
        val fat = items.sumOf { it.dish.fatG * it.qty }

        return NutritionTotals(
            kcal = kcal,
            proteinG = protein,
            carbsG = carbs,
            fatG = fat
        )
    }

    fun calculateTotalsByParticipant(items: List<CartItem>): Map<String, NutritionTotals> {
        return items
            .filter { !it.participantId.isNullOrBlank() }
            .groupBy { it.participantId.orEmpty() }
            .mapValues { (_, participantItems) -> calculateTotals(participantItems) }
    }
}

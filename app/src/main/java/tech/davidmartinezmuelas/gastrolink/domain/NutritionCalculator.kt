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

    fun recommendations(totals: NutritionTotals): List<String> {
        val messages = mutableListOf<String>()

        if (totals.kcal > 900) {
            messages += "Calorias altas"
        }
        if (totals.proteinG < 30) {
            messages += "Proteina baja"
        }
        if (messages.isEmpty()) {
            messages += "Balance nutricional correcto"
        }

        return messages.take(2)
    }
}

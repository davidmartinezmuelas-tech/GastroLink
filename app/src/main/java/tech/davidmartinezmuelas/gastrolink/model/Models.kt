package tech.davidmartinezmuelas.gastrolink.model

data class Branch(
    val id: String,
    val name: String,
    val city: String
)

data class Dish(
    val id: String,
    val name: String,
    val kcal: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int
)

data class CartItem(
    val dish: Dish,
    val qty: Int
)

data class NutritionTotals(
    val kcal: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int
)

package tech.davidmartinezmuelas.gastrolink.ui

import tech.davidmartinezmuelas.gastrolink.domain.NutritionStats
import tech.davidmartinezmuelas.gastrolink.model.NutritionTotals
import tech.davidmartinezmuelas.gastrolink.model.Participant

data class OrderHistoryItemUi(
    val id: String,
    val createdAt: Long,
    val branchName: String,
    val dishCount: Int,
    val totalCalories: Int
)

data class OrderDetailItemUi(
    val dishName: String,
    val quantity: Int,
    val participantName: String?
)

data class OrderDetailUi(
    val id: String,
    val createdAt: Long,
    val branchName: String,
    val orderMode: String,
    val nutritionMode: String,
    val participants: List<Participant>,
    val items: List<OrderDetailItemUi>,
    val totals: NutritionTotals,
    val profileType: String? = null,
    val profileSummaryLines: List<String> = emptyList(),
    val profileParseable: Boolean = true,
    val rawProfileJson: String? = null,
    val canShowRawProfileJson: Boolean = false
)

data class StatsUi(
    val averageCaloriesPerOrder: Double = 0.0,
    val averageProtein: Double = 0.0,
    val averageCarbs: Double = 0.0,
    val averageFat: Double = 0.0,
    val mostOrderedDishName: String = "No disponible"
)

fun NutritionStats.toStatsUi(resolveDishName: (String?) -> String): StatsUi {
    return StatsUi(
        averageCaloriesPerOrder = averageCaloriesPerOrder,
        averageProtein = averageProtein,
        averageCarbs = averageCarbs,
        averageFat = averageFat,
        mostOrderedDishName = resolveDishName(mostOrderedDishId)
    )
}

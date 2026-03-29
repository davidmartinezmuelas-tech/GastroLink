package tech.davidmartinezmuelas.gastrolink.data.ai

data class AiRecommendationRequest(
    val orderMode: String,
    val nutritionMode: String,
    val totals: TotalsPayload,
    val dishes: List<DishPayload>,
    val profile: ProfilePayload?
) {
    data class TotalsPayload(
        val kcal: Int,
        val proteinG: Int,
        val carbsG: Int,
        val fatG: Int
    )

    data class DishPayload(
        val name: String,
        val qty: Int,
        val kcal: Int,
        val proteinG: Int,
        val carbsG: Int,
        val fatG: Int
    )

    data class ProfilePayload(
        val type: String,
        val summary: Map<String, Any?>
    )
}

data class AiRecommendationResponse(
    val recommendationText: String,
    val model: String? = null,
    val requestId: String? = null
)

package tech.davidmartinezmuelas.gastrolink.domain

import tech.davidmartinezmuelas.gastrolink.data.ai.AiChatRequest
import tech.davidmartinezmuelas.gastrolink.data.ai.AiChatResponse
import tech.davidmartinezmuelas.gastrolink.data.ai.AiRecommendationRequest
import tech.davidmartinezmuelas.gastrolink.data.ai.AiRecommendationResponse

interface AiRecommendationService {
    suspend fun generate(request: AiRecommendationRequest): AiRecommendationResponse
    suspend fun chat(request: AiChatRequest): AiChatResponse
}

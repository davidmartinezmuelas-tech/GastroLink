package tech.davidmartinezmuelas.gastrolink.data.ai

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.delay
import tech.davidmartinezmuelas.gastrolink.domain.AiRecommendationService
import java.io.IOException

class AiRecommendationServiceImpl(
    private val baseUrl: String,
    private val client: HttpClient = defaultHttpClient()
) : AiRecommendationService {

    override suspend fun generate(request: AiRecommendationRequest): AiRecommendationResponse {
        if (baseUrl.isBlank()) {
            throw IllegalStateException("Servicio IA no configurado")
        }

        var lastError: Throwable? = null
        repeat(2) { attempt ->
            runCatching {
                val response = client.post("${baseUrl.trimEnd('/')}/ai/recommendation") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

                if (response.status != HttpStatusCode.OK) {
                    throw IllegalStateException("Servicio IA no disponible")
                }

                response.body<AiRecommendationResponse>()
            }.onSuccess { return it }
                .onFailure { error ->
                    lastError = error
                    if (error is IOException && attempt == 0) {
                        delay(250)
                    }
                }
        }

        throw lastError ?: IllegalStateException("No se pudo obtener recomendacion IA")
    }

    companion object {
        private fun defaultHttpClient(): HttpClient {
            return HttpClient(Android) {
                install(ContentNegotiation) {
                    gson()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 10_000
                    connectTimeoutMillis = 10_000
                    socketTimeoutMillis = 10_000
                }
            }
        }
    }
}

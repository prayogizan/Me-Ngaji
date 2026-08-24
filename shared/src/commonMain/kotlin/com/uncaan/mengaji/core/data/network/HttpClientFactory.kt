package com.uncaan.mengaji.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory for creating configured Ktor [HttpClient] instances across platforms.
 *
 * Configures standard engine plugins including JSON content negotiation with
 * `kotlinx.serialization`, HTTP timeouts, request headers, and debug logging.
 */
object HttpClientFactory {

    /**
     * Creates a pre-configured [HttpClient] with default Al-Quran API settings.
     *
     * @param baseUrl The base endpoint URL for API requests. Defaults to `"https://api.alquran.cloud/v1/"`.
     * @return The configured [HttpClient] instance ready for network calls.
     */
    fun create(baseUrl: String = "https://api.alquran.cloud/v1/"): HttpClient {
        return HttpClient {
            expectSuccess = true

            defaultRequest {
                url(baseUrl)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = false
                        encodeDefaults = true
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 15_000
            }

            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        println("[KtorClient] $message")
                    }
                }
            }
        }
    }
}

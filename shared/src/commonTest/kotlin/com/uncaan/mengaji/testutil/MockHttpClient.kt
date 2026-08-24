package com.uncaan.mengaji.testutil

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates a mock [HttpClient] engine responding with static JSON responses for unit tests.
 *
 * Configures [MockEngine] with ContentNegotiation and sets the base URL default request plugin.
 *
 * @param responseJson JSON response body string returned by the mock engine.
 * @param statusCode HTTP status code returned in the simulated response (defaults to [HttpStatusCode.OK]).
 * @return Mocked [HttpClient] instance.
 */
fun createMockHttpClient(
    responseJson: String,
    statusCode: HttpStatusCode = HttpStatusCode.OK
): HttpClient {
    val mockEngine = MockEngine { _ ->
        respond(
            content = responseJson,
            status = statusCode,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    return HttpClient(mockEngine) {
        expectSuccess = true
        defaultRequest {
            url("https://api.alquran.cloud/v1/")
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                }
            )
        }
    }
}

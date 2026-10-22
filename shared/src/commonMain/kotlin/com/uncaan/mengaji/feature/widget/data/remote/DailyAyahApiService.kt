package com.uncaan.mengaji.feature.widget.data.remote

import com.uncaan.mengaji.feature.widget.data.model.DailyAyahResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Remote API service interface for fetching random daily Quranic verses from the Al-Quran Cloud REST API.
 *
 * @see DailyAyahApiServiceImpl
 */
interface DailyAyahApiService {

    /**
     * Retrieves a random Ayah with both Arabic Uthmani text and Indonesian translation editions.
     *
     * @return [DailyAyahResponseDto] containing the multi-edition verse data.
     */
    suspend fun getRandomDailyAyah(): DailyAyahResponseDto
}

/**
 * Ktor-powered implementation of [DailyAyahApiService].
 *
 * Calls the endpoint: `ayah/random/editions/quran-uthmani,id.indonesian`.
 *
 * @param httpClient Pre-configured Ktor [HttpClient] instance with JSON serialization.
 * @see DailyAyahApiService
 */
class DailyAyahApiServiceImpl(
    private val httpClient: HttpClient
) : DailyAyahApiService {

    override suspend fun getRandomDailyAyah(): DailyAyahResponseDto {
        return httpClient.get("ayah/random/editions/quran-uthmani,id.indonesian").body()
    }
}

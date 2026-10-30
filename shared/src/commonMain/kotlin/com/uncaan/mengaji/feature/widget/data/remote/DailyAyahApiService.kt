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
     * Retrieves a random Ayah with both Arabic Uthmani text and the specified translation edition.
     *
     * @param edition Identifier of the translation edition (e.g., "id.indonesian", "en.asad").
     *                Defaults to `"id.indonesian"`.
     * @return [DailyAyahResponseDto] containing the multi-edition verse data.
     */
    suspend fun getRandomDailyAyah(edition: String = "id.indonesian"): DailyAyahResponseDto
}

/**
 * Ktor-powered implementation of [DailyAyahApiService].
 *
 * Calls the endpoint: `ayah/random/editions/quran-uthmani,{edition}`.
 *
 * @param httpClient Pre-configured Ktor [HttpClient] instance with JSON serialization.
 * @see DailyAyahApiService
 */
class DailyAyahApiServiceImpl(
    private val httpClient: HttpClient
) : DailyAyahApiService {

    override suspend fun getRandomDailyAyah(edition: String): DailyAyahResponseDto {
        return httpClient.get("ayah/random/editions/quran-uthmani,$edition").body()
    }
}

package com.uncaan.mengaji.feature.quran.data.remote

import com.uncaan.mengaji.feature.quran.data.model.AyahEditionsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Remote API service interface for querying the Al-Quran Cloud REST API.
 */
interface QuranApiService {

    /**
     * Queries multi-edition Ayah data including Arabic text, requested translation, and recitation audio.
     *
     * @param reference The Ayah reference query (e.g. "2:255" or global verse index).
     * @param translationEdition The target translation identifier (e.g. "en.sahih"). Defaults to `"en.sahih"`.
     * @param recitationEdition The target recitation audio identifier (e.g. "ar.alafasy"). Defaults to `"ar.alafasy"`.
     * @return [AyahEditionsResponseDto] containing a list of [AyahDto] per requested edition.
     */
    suspend fun getAyah(
        reference: String,
        translationEdition: String = "en.sahih",
        recitationEdition: String = "ar.alafasy"
    ): AyahEditionsResponseDto
}

/**
 * Default Ktor implementation of [QuranApiService].
 *
 * @param httpClient The configured Ktor [HttpClient] instance.
 * @see QuranApiService
 */
class QuranApiServiceImpl(
    private val httpClient: HttpClient
) : QuranApiService {
    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AyahEditionsResponseDto {
        return httpClient.get("ayah/$reference/editions/quran-uthmani,$translationEdition,$recitationEdition").body()
    }
}

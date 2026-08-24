package com.uncaan.mengaji.feature.quran.data.remote

import com.uncaan.mengaji.feature.quran.data.model.AyahEditionsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface QuranApiService {
    suspend fun getAyah(
        reference: String,
        translationEdition: String = "en.sahih",
        recitationEdition: String = "ar.alafasy"
    ): AyahEditionsResponseDto
}

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

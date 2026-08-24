package com.uncaan.mengaji.feature.quran.data.remote

import com.uncaan.mengaji.feature.quran.data.model.AyahResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface QuranApiService {
    suspend fun getAyah(reference: String, edition: String): AyahResponseDto
}

class QuranApiServiceImpl(
    private val httpClient: HttpClient
) : QuranApiService {
    override suspend fun getAyah(reference: String, edition: String): AyahResponseDto {
        return httpClient.get("ayah/$reference/$edition").body()
    }
}

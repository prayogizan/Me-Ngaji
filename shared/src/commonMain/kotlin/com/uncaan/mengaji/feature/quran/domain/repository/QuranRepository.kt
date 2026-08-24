package com.uncaan.mengaji.feature.quran.domain.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah

interface QuranRepository {
    suspend fun getAyah(reference: String, edition: String): AppResult<Ayah>
}

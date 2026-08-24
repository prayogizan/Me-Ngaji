package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

class GetAyahUseCase(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(
        reference: String,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        recitationEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ): AppResult<Ayah> {
        val trimmed = reference.trim()
        if (trimmed.isBlank()) {
            return AppResult.Error(IllegalArgumentException("Ayah reference cannot be empty"))
        }
        return repository.getAyah(trimmed, translationEdition, recitationEdition)
    }
}

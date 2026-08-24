package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Use case that encapsulates the business logic for retrieving an Ayah.
 *
 * Validates the search reference input before delegating the fetch request
 * to [QuranRepository].
 *
 * @property repository The Quran repository instance.
 * @see QuranRepository
 */
class GetAyahUseCase(
    private val repository: QuranRepository
) {

    /**
     * Executes the Ayah retrieval workflow.
     *
     * @param reference The Ayah reference query (e.g. "2:255"). Trimmed automatically.
     * @param translationEdition The target translation edition. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @param recitationEdition The target audio recitation edition. Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
     * @return [AppResult.Success] with the domain [Ayah], or [AppResult.Error] if input is blank or network fails.
     */
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

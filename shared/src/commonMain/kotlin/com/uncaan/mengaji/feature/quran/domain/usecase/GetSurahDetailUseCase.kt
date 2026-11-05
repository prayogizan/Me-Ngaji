package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Use case that retrieves a complete Surah with parallel multi-edition verses.
 *
 * Validates that [surahNumber] falls within the canonical Quranic bounds (`1..114`)
 * before delegating network I/O to [QuranRepository.getSurahDetail]. Out-of-bounds
 * Surah numbers are rejected immediately with [AppResult.Error] wrapping an [IllegalArgumentException].
 *
 * @property repository The repository abstraction for Quran data access.
 * @see QuranRepository
 * @see SurahDetail
 */
class GetSurahDetailUseCase(
    private val repository: QuranRepository
) {

    /**
     * Executes the Surah detail retrieval flow.
     *
     * @param surahNumber The 1-indexed Surah number (`1..114`).
     * @param translationEdition The translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @param audioEdition The audio recitation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
     * @return [AppResult.Success] containing the merged [SurahDetail],
     *         or [AppResult.Error] on validation or network failure.
     */
    suspend operator fun invoke(
        surahNumber: Int,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        audioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ): AppResult<SurahDetail> {
        if (surahNumber !in MIN_SURAH_NUMBER..MAX_SURAH_NUMBER) {
            return AppResult.Error(
                IllegalArgumentException(
                    "Surah number must be between $MIN_SURAH_NUMBER and $MAX_SURAH_NUMBER, got $surahNumber"
                )
            )
        }
        val resolvedTranslation = translationEdition.trim().ifBlank { QuranEditionPresets.DEFAULT_TRANSLATION }
        val resolvedAudio = audioEdition.trim().ifBlank { QuranEditionPresets.DEFAULT_RECITATION }
        return repository.getSurahDetail(
            surahNumber = surahNumber,
            translationEdition = resolvedTranslation,
            audioEdition = resolvedAudio
        )
    }

    /**
     * Canonical boundary constants for Quranic Surah numbers.
     */
    companion object {
        /** First Surah in the Quran (Al-Fatihah). */
        const val MIN_SURAH_NUMBER = 1

        /** Last Surah in the Quran (An-Nas). */
        const val MAX_SURAH_NUMBER = 114
    }
}


package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Single-responsibility Use Case for persisting the user's latest recitation progress.
 *
 * Validates domain constraints (Surah number, Mushaf page number, and verse number)
 * before delegating persistence to [QuranRepository.saveLastReadProgress].
 *
 * @property repository The repository contract for Quran data access.
 * @see QuranRepository
 * @see LastReadProgress
 */
class SaveLastReadUseCase(
    private val repository: QuranRepository
) {

    /**
     * Executes the persistence of the user's recitation progress.
     *
     * @param progress The [LastReadProgress] to validate and persist.
     * @throws IllegalArgumentException If [LastReadProgress.surahNumber] is not within 1..114,
     *                                  [LastReadProgress.pageNumber] is not within 1..604,
     *                                  or [LastReadProgress.ayahNumberInSurah] is less than 1.
     */
    suspend operator fun invoke(progress: LastReadProgress) {
        require(progress.surahNumber in 1..114) {
            "Surah number must be between 1 and 114, got ${progress.surahNumber}"
        }
        require(progress.pageNumber in 1..604) {
            "Mushaf page number must be between 1 and 604, got ${progress.pageNumber}"
        }
        require(progress.ayahNumberInSurah >= 1) {
            "Ayah number in surah must be at least 1, got ${progress.ayahNumberInSurah}"
        }

        repository.saveLastReadProgress(progress)
    }
}


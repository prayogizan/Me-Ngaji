package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Single-responsibility Use Case for retrieving the user's latest recitation progress.
 *
 * Delegates retrieval to [QuranRepository.getLastReadProgress].
 *
 * @property repository The repository contract for Quran data access.
 * @see QuranRepository
 * @see LastReadProgress
 */
class GetLastReadUseCase(
    private val repository: QuranRepository
) {

    /**
     * Executes the retrieval of the user's last recorded recitation progress.
     *
     * @return The persisted [LastReadProgress] entity, or `null` if none has been saved.
     */
    suspend operator fun invoke(): LastReadProgress? {
        return repository.getLastReadProgress()
    }
}


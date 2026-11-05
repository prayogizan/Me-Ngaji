package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Use case that retrieves the complete 114-Surah catalog.
 *
 * Delegates catalog retrieval to [QuranRepository.getSurahList], supporting optional
 * forced refresh to bypass cached catalog entries.
 *
 * @property repository The repository abstraction for Quran data access.
 * @see QuranRepository
 * @see SurahSummary
 */
class GetSurahListUseCase(
    private val repository: QuranRepository
) {

    /**
     * Executes the Surah catalog retrieval flow.
     *
     * @param forceRefresh When `true`, forces a remote network fetch bypassing cached entries.
     *                     Defaults to `false`.
     * @return [AppResult.Success] containing the 114 [SurahSummary] entries,
     *         or [AppResult.Error] if the retrieval fails.
     */
    suspend operator fun invoke(
        forceRefresh: Boolean = false
    ): AppResult<List<SurahSummary>> {
        return repository.getSurahList(forceRefresh = forceRefresh)
    }
}


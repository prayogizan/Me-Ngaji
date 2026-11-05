package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Use case that retrieves a single Medina Mushaf page (`1..604`) with multi-edition verse text.
 *
 * Validates that [pageNumber] falls within the standard 604-page Medina Mushaf range (`1..604`)
 * before delegating network I/O to [QuranRepository.getMushafPage]. Out-of-bounds page numbers
 * are rejected immediately with [AppResult.Error] wrapping an [IllegalArgumentException].
 *
 * @property repository The repository abstraction for Quran data access.
 * @see QuranRepository
 * @see MushafPage
 */
class GetMushafPageUseCase(
    private val repository: QuranRepository
) {

    /**
     * Executes the Mushaf page retrieval flow.
     *
     * @param pageNumber The 1-indexed Medina Mushaf page number (`1..604`).
     * @param translationEdition The translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @return [AppResult.Success] containing the merged [MushafPage],
     *         or [AppResult.Error] on validation or network failure.
     */
    suspend operator fun invoke(
        pageNumber: Int,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION
    ): AppResult<MushafPage> {
        if (pageNumber !in MIN_PAGE_NUMBER..MAX_PAGE_NUMBER) {
            return AppResult.Error(
                IllegalArgumentException(
                    "Mushaf page number must be between $MIN_PAGE_NUMBER and $MAX_PAGE_NUMBER, got $pageNumber"
                )
            )
        }
        val resolvedTranslation = translationEdition.trim().ifBlank { QuranEditionPresets.DEFAULT_TRANSLATION }
        return repository.getMushafPage(
            pageNumber = pageNumber,
            translationEdition = resolvedTranslation
        )
    }

    /**
     * Canonical boundary constants for Medina Mushaf page numbers.
     */
    companion object {
        /** First page of the Medina Mushaf. */
        const val MIN_PAGE_NUMBER = 1

        /** Last page of the 604-page Medina Mushaf. */
        const val MAX_PAGE_NUMBER = 604
    }
}


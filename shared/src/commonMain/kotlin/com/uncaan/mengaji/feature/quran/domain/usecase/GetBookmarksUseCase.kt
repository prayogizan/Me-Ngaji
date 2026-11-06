package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Single-responsibility Use Case for retrieving all persisted Ayah bookmarks.
 *
 * Delegates retrieval to [QuranRepository.getBookmarks], returning items sorted
 * descending by creation timestamp.
 *
 * @property repository The repository contract for Quran data access.
 * @see QuranRepository
 * @see AyahBookmark
 */
class GetBookmarksUseCase(
    private val repository: QuranRepository
) {

    /**
     * Executes the retrieval of all saved Ayah bookmarks.
     *
     * @return Ordered list of [AyahBookmark] entities sorted newest first.
     */
    suspend operator fun invoke(): List<AyahBookmark> {
        return repository.getBookmarks()
    }
}


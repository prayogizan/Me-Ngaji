package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository

/**
 * Single-responsibility Use Case for toggling the bookmark state of an Ayah.
 *
 * Validates domain boundaries before delegating the toggle operation to [QuranRepository.toggleBookmark].
 *
 * @property repository The repository contract for Quran data access.
 * @see QuranRepository
 * @see AyahBookmark
 */
class ToggleBookmarkUseCase(
    private val repository: QuranRepository
) {

    /**
     * Toggles bookmark persistence for the given [bookmark].
     *
     * @param bookmark The [AyahBookmark] entity to add or remove.
     * @return `true` if the bookmark was added, `false` if it was removed.
     * @throws IllegalArgumentException If [AyahBookmark.globalAyahNumber] is outside 1..6236
     *                                  or [AyahBookmark.surahNumber] is outside 1..114.
     */
    suspend operator fun invoke(bookmark: AyahBookmark): Boolean {
        require(bookmark.globalAyahNumber in 1..6236) {
            "Global Ayah number must be between 1 and 6236, got ${bookmark.globalAyahNumber}"
        }
        require(bookmark.surahNumber in 1..114) {
            "Surah number must be between 1 and 114, got ${bookmark.surahNumber}"
        }

        return repository.toggleBookmark(bookmark)
    }
}


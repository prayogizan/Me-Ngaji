package com.uncaan.mengaji.feature.quran.domain.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary

/**
 * Contract defining the data access operations for the Quran feature.
 *
 * Implemented in the data layer by [com.uncaan.mengaji.feature.quran.data.repository.QuranRepositoryImpl]
 * and injected into Use Cases via Koin.
 *
 * @see com.uncaan.mengaji.feature.quran.data.repository.QuranRepositoryImpl
 */
interface QuranRepository {

    /**
     * Retrieves a single Ayah with parallel text and audio editions.
     *
     * @param reference Ayah reference formatted as `surah:ayah` (e.g. "2:255") or global index (e.g. "262").
     * @param translationEdition Translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @param recitationEdition Audio recitation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
     * @return [AppResult.Success] containing the domain [Ayah] model, or [AppResult.Error] on failure.
     */
    suspend fun getAyah(
        reference: String,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        recitationEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ): AppResult<Ayah>

    /**
     * Retrieves the complete 114-Surah catalog.
     *
     * @param forceRefresh When `true`, bypasses any in-memory or local cache and fetches directly from the remote API.
     *                     Defaults to `false`.
     * @return [AppResult.Success] containing the ordered list of 114 [SurahSummary] items, or [AppResult.Error] on failure.
     */
    suspend fun getSurahList(
        forceRefresh: Boolean = false
    ): AppResult<List<SurahSummary>>

    /**
     * Retrieves a complete Surah with parallel Uthmani Arabic, translation, and audio editions in a single round-trip.
     *
     * @param surahNumber The 1-indexed Surah number (`1..114`).
     * @param translationEdition Translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @param audioEdition Audio recitation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
     * @return [AppResult.Success] containing the merged [SurahDetail], or [AppResult.Error] on failure.
     */
    suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        audioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ): AppResult<SurahDetail>

    /**
     * Retrieves a Medina Mushaf page (`1..604`) with parallel Uthmani Arabic and translation editions.
     *
     * @param pageNumber The 1-indexed Mushaf page number (`1..604`).
     * @param translationEdition Translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @return [AppResult.Success] containing the merged [MushafPage], or [AppResult.Error] on failure.
     */
    suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION
    ): AppResult<MushafPage>

    /**
     * Retrieves the user's last recorded recitation progress, or `null` if none has been saved.
     *
     * @return The persisted [LastReadProgress] domain model, or `null` if no progress is recorded.
     * @see LastReadProgress
     */
    suspend fun getLastReadProgress(): LastReadProgress?

    /**
     * Persists the user's current recitation progress into local storage.
     *
     * @param progress The [LastReadProgress] domain model to store.
     * @see LastReadProgress
     */
    suspend fun saveLastReadProgress(progress: LastReadProgress)

    /**
     * Retrieves all saved ayah bookmarks, sorted newest first ([AyahBookmark.savedAtEpochMillis] descending).
     *
     * @return List of persisted [AyahBookmark] entities, or an empty list if none are saved.
     * @see AyahBookmark
     */
    suspend fun getBookmarks(): List<AyahBookmark>

    /**
     * Toggles the bookmark state for an Ayah.
     *
     * If a bookmark with matching [AyahBookmark.globalAyahNumber] exists, it is removed and `false` is returned.
     * If not present, it is added to the bookmarks list and `true` is returned.
     * Persisted bookmarks remain sorted descending by [AyahBookmark.savedAtEpochMillis].
     *
     * @param bookmark The [AyahBookmark] to add or remove.
     * @return `true` if the bookmark was added, `false` if it was removed.
     * @see AyahBookmark
     */
    suspend fun toggleBookmark(bookmark: AyahBookmark): Boolean
}

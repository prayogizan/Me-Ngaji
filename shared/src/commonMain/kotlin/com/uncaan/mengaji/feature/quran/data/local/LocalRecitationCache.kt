package com.uncaan.mengaji.feature.quran.data.local

import com.uncaan.mengaji.feature.quran.data.model.AyahBookmarkDto
import com.uncaan.mengaji.feature.quran.data.model.LastReadProgressDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto

/**
 * Local cache abstraction for persisting recitation progress, bookmarks, and offline catalog data.
 *
 * Provides synchronous key-value storage backed by platform-native preferences:
 * - **Android:** Backed by [android.content.SharedPreferences].
 * - **iOS:** Backed by `platform.Foundation.NSUserDefaults`.
 *
 * @see PlatformLocalRecitationCache
 */
interface LocalRecitationCache {

    /**
     * Retrieves the persisted [LastReadProgressDto], or `null` if no progress has been saved.
     *
     * @return The cached [LastReadProgressDto], or `null` if the cache is empty or corrupted.
     */
    fun getLastReadProgress(): LastReadProgressDto?

    /**
     * Persists the given [LastReadProgressDto] into local storage.
     *
     * @param progress The [LastReadProgressDto] to store.
     */
    fun saveLastReadProgress(progress: LastReadProgressDto)

    /**
     * Retrieves all saved ayah bookmarks.
     *
     * @return List of persisted [AyahBookmarkDto] items, or an empty list if none are saved or data is corrupted.
     */
    fun getBookmarks(): List<AyahBookmarkDto>

    /**
     * Persists the given list of [AyahBookmarkDto] items into local storage.
     *
     * @param bookmarks The complete list of [AyahBookmarkDto] items to persist.
     */
    fun saveBookmarks(bookmarks: List<AyahBookmarkDto>)

    /**
     * Retrieves the cached 114-Surah catalog, or `null` if the catalog has not been cached yet.
     *
     * @return List of [SurahSummaryDto] items for offline fallback, or `null` if absent or corrupted.
     */
    fun getCachedSurahCatalog(): List<SurahSummaryDto>?

    /**
     * Persists the 114-Surah catalog for offline fallback functionality.
     *
     * @param catalog The list of [SurahSummaryDto] items to store.
     */
    fun cacheSurahCatalog(catalog: List<SurahSummaryDto>)

    /**
     * Clears all persisted recitation progress, bookmarks, and cached catalogs.
     */
    fun clear()

    companion object {
        /** Preference storage key for serialized [LastReadProgressDto]. */
        const val KEY_LAST_READ_PROGRESS = "mengaji_last_read_progress_v1"

        /** Preference storage key for serialized list of [AyahBookmarkDto]. */
        const val KEY_AYAH_BOOKMARKS = "mengaji_ayah_bookmarks_v1"

        /** Preference storage key for serialized 114-Surah catalog fallback. */
        const val KEY_SURAH_CATALOG_CACHE = "mengaji_surah_catalog_cache_v1"
    }
}

/**
 * Platform-specific native backing implementation of [LocalRecitationCache].
 *
 * - **Android:** Backed by [android.content.SharedPreferences].
 * - **iOS:** Backed by `platform.Foundation.NSUserDefaults`.
 *
 * @see LocalRecitationCache
 */
expect class PlatformLocalRecitationCache : LocalRecitationCache {
    override fun getLastReadProgress(): LastReadProgressDto?
    override fun saveLastReadProgress(progress: LastReadProgressDto)
    override fun getBookmarks(): List<AyahBookmarkDto>
    override fun saveBookmarks(bookmarks: List<AyahBookmarkDto>)
    override fun getCachedSurahCatalog(): List<SurahSummaryDto>?
    override fun cacheSurahCatalog(catalog: List<SurahSummaryDto>)
    override fun clear()
}


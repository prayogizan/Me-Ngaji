package com.uncaan.mengaji.feature.widget.data.local

import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah

/**
 * Local cache abstraction for persisting and retrieving the daily Ayah quote.
 *
 * Provides caching of the most recently fetched verse along with its timestamp to enable
 * offline functionality and minimize redundant API calls within the same calendar day.
 *
 * @see PlatformLocalAyahCache
 */
interface LocalAyahCache {

    /**
     * Retrieves the currently persisted [DailyAyah], or `null` if the cache is empty.
     *
     * @return The cached [DailyAyah] domain model, or null if never cached.
     */
    fun getCachedAyah(): DailyAyah?

    /**
     * Persists the given [DailyAyah] into local storage.
     *
     * @param ayah The [DailyAyah] to store.
     */
    fun cacheAyah(ayah: DailyAyah)

    /**
     * Retrieves the ISO-8601 date string ("yyyy-MM-dd") when the quote was cached.
     *
     * @return The cached date string, or `null` if no entry exists.
     */
    fun getCacheDate(): String?
}

/**
 * Platform-specific native backing implementation of [LocalAyahCache].
 *
 * - **Android:** Backed by Android [android.content.SharedPreferences].
 * - **iOS:** In-memory or stub implementation (Phase 2).
 *
 * @see LocalAyahCache
 */
expect class PlatformLocalAyahCache : LocalAyahCache {
    override fun getCachedAyah(): DailyAyah?
    override fun cacheAyah(ayah: DailyAyah)
    override fun getCacheDate(): String?
}

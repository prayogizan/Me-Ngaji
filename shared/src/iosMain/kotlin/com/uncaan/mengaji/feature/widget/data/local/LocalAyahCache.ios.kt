package com.uncaan.mengaji.feature.widget.data.local

import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah

/**
 * iOS placeholder implementation of [PlatformLocalAyahCache] for Phase 2.
 *
 * Always returns null for cache reads and ignores writes. Will be implemented using
 * NSUserDefaults / App Group Shared Defaults in the iOS WidgetKit phase.
 *
 * @see LocalAyahCache
 * @see PlatformLocalAyahCache
 */
actual class PlatformLocalAyahCache : LocalAyahCache {

    actual override fun getCachedAyah(): DailyAyah? = null

    actual override fun cacheAyah(ayah: DailyAyah) {
        // Stub: Phase 2 WidgetKit support
    }

    actual override fun getCacheDate(): String? = null
}

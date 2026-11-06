package com.uncaan.mengaji.feature.quran.data.local

import com.uncaan.mengaji.feature.quran.data.model.AyahBookmarkDto
import com.uncaan.mengaji.feature.quran.data.model.LastReadProgressDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of [PlatformLocalRecitationCache] backed by [NSUserDefaults].
 *
 * Persists JSON-serialized recitation records, bookmarks, and catalog fallbacks synchronously into
 * standard user defaults. Gracefully handles corrupted JSON by returning `null` or `emptyList()`
 * without throwing exceptions.
 *
 * @see LocalRecitationCache
 * @see PlatformLocalRecitationCache
 */
actual class PlatformLocalRecitationCache : LocalRecitationCache {

    private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    actual override fun getLastReadProgress(): LastReadProgressDto? {
        val raw = userDefaults.stringForKey(LocalRecitationCache.KEY_LAST_READ_PROGRESS) ?: return null
        return try {
            json.decodeFromString<LastReadProgressDto>(raw)
        } catch (_: Throwable) {
            null
        }
    }

    actual override fun saveLastReadProgress(progress: LastReadProgressDto) {
        val raw = json.encodeToString(progress)
        userDefaults.setObject(raw, forKey = LocalRecitationCache.KEY_LAST_READ_PROGRESS)
    }

    actual override fun getBookmarks(): List<AyahBookmarkDto> {
        val raw = userDefaults.stringForKey(LocalRecitationCache.KEY_AYAH_BOOKMARKS) ?: return emptyList()
        return try {
            json.decodeFromString<List<AyahBookmarkDto>>(raw)
        } catch (_: Throwable) {
            emptyList()
        }
    }

    actual override fun saveBookmarks(bookmarks: List<AyahBookmarkDto>) {
        val raw = json.encodeToString(bookmarks)
        userDefaults.setObject(raw, forKey = LocalRecitationCache.KEY_AYAH_BOOKMARKS)
    }

    actual override fun getCachedSurahCatalog(): List<SurahSummaryDto>? {
        val raw = userDefaults.stringForKey(LocalRecitationCache.KEY_SURAH_CATALOG_CACHE) ?: return null
        return try {
            json.decodeFromString<List<SurahSummaryDto>>(raw)
        } catch (_: Throwable) {
            null
        }
    }

    actual override fun cacheSurahCatalog(catalog: List<SurahSummaryDto>) {
        val raw = json.encodeToString(catalog)
        userDefaults.setObject(raw, forKey = LocalRecitationCache.KEY_SURAH_CATALOG_CACHE)
    }

    actual override fun clear() {
        userDefaults.removeObjectForKey(LocalRecitationCache.KEY_LAST_READ_PROGRESS)
        userDefaults.removeObjectForKey(LocalRecitationCache.KEY_AYAH_BOOKMARKS)
        userDefaults.removeObjectForKey(LocalRecitationCache.KEY_SURAH_CATALOG_CACHE)
    }
}


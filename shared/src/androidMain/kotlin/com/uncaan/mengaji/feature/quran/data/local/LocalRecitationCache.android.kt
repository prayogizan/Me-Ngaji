package com.uncaan.mengaji.feature.quran.data.local

import android.content.Context
import android.content.SharedPreferences
import com.uncaan.mengaji.feature.quran.data.model.AyahBookmarkDto
import com.uncaan.mengaji.feature.quran.data.model.LastReadProgressDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Android implementation of [PlatformLocalRecitationCache] backed by [SharedPreferences].
 *
 * Persists JSON-serialized recitation records, bookmarks, and catalog fallbacks synchronously into
 * private shared preferences. Gracefully handles corrupted JSON by returning `null` or `emptyList()`
 * without throwing exceptions.
 *
 * @param context Android Application Context used to retrieve [SharedPreferences].
 * @see LocalRecitationCache
 * @see PlatformLocalRecitationCache
 */
actual class PlatformLocalRecitationCache(context: Context) : LocalRecitationCache {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    actual override fun getLastReadProgress(): LastReadProgressDto? {
        val raw = prefs.getString(LocalRecitationCache.KEY_LAST_READ_PROGRESS, null) ?: return null
        return try {
            json.decodeFromString<LastReadProgressDto>(raw)
        } catch (_: Throwable) {
            null
        }
    }

    actual override fun saveLastReadProgress(progress: LastReadProgressDto) {
        val raw = json.encodeToString(progress)
        prefs.edit()
            .putString(LocalRecitationCache.KEY_LAST_READ_PROGRESS, raw)
            .apply()
    }

    actual override fun getBookmarks(): List<AyahBookmarkDto> {
        val raw = prefs.getString(LocalRecitationCache.KEY_AYAH_BOOKMARKS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<AyahBookmarkDto>>(raw)
        } catch (_: Throwable) {
            emptyList()
        }
    }

    actual override fun saveBookmarks(bookmarks: List<AyahBookmarkDto>) {
        val raw = json.encodeToString(bookmarks)
        prefs.edit()
            .putString(LocalRecitationCache.KEY_AYAH_BOOKMARKS, raw)
            .apply()
    }

    actual override fun getCachedSurahCatalog(): List<SurahSummaryDto>? {
        val raw = prefs.getString(LocalRecitationCache.KEY_SURAH_CATALOG_CACHE, null) ?: return null
        return try {
            json.decodeFromString<List<SurahSummaryDto>>(raw)
        } catch (_: Throwable) {
            null
        }
    }

    actual override fun cacheSurahCatalog(catalog: List<SurahSummaryDto>) {
        val raw = json.encodeToString(catalog)
        prefs.edit()
            .putString(LocalRecitationCache.KEY_SURAH_CATALOG_CACHE, raw)
            .apply()
    }

    actual override fun clear() {
        prefs.edit()
            .remove(LocalRecitationCache.KEY_LAST_READ_PROGRESS)
            .remove(LocalRecitationCache.KEY_AYAH_BOOKMARKS)
            .remove(LocalRecitationCache.KEY_SURAH_CATALOG_CACHE)
            .apply()
    }

    private companion object {
        private const val PREF_NAME = "mengaji_recitation_cache"
    }
}


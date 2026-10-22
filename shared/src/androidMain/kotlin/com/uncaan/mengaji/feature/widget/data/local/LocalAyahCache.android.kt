package com.uncaan.mengaji.feature.widget.data.local

import android.content.Context
import android.content.SharedPreferences
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah

/**
 * Android implementation of [PlatformLocalAyahCache] backed by [SharedPreferences].
 *
 * Persists the daily Quran verse fields as individual preference keys, allowing rapid synchronous
 * reads for Android App Widgets without spinning up asynchronous database engines.
 *
 * @param context Android Application Context used to initialize SharedPreferences.
 * @see LocalAyahCache
 * @see PlatformLocalAyahCache
 */
actual class PlatformLocalAyahCache(context: Context) : LocalAyahCache {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    actual override fun getCachedAyah(): DailyAyah? {
        val date = prefs.getString(KEY_FETCHED_DATE, null) ?: return null
        val arabicText = prefs.getString(KEY_ARABIC_TEXT, null) ?: return null
        val translationText = prefs.getString(KEY_TRANSLATION_TEXT, "") ?: ""
        val surahName = prefs.getString(KEY_SURAH_NAME, "") ?: ""
        val surahNameArabic = prefs.getString(KEY_SURAH_NAME_ARABIC, "") ?: ""
        val surahNumber = prefs.getInt(KEY_SURAH_NUMBER, 0)
        val ayahNumberInSurah = prefs.getInt(KEY_AYAH_NUMBER_IN_SURAH, 0)
        val globalAyahNumber = prefs.getInt(KEY_GLOBAL_AYAH_NUMBER, 0)
        val translationEdition = prefs.getString(KEY_TRANSLATION_EDITION, "id.indonesian") ?: "id.indonesian"

        return DailyAyah(
            arabicText = arabicText,
            translationText = translationText,
            surahName = surahName,
            surahNameArabic = surahNameArabic,
            surahNumber = surahNumber,
            ayahNumberInSurah = ayahNumberInSurah,
            globalAyahNumber = globalAyahNumber,
            translationEdition = translationEdition,
            fetchedDate = date
        )
    }

    actual override fun cacheAyah(ayah: DailyAyah) {
        prefs.edit()
            .putString(KEY_ARABIC_TEXT, ayah.arabicText)
            .putString(KEY_TRANSLATION_TEXT, ayah.translationText)
            .putString(KEY_SURAH_NAME, ayah.surahName)
            .putString(KEY_SURAH_NAME_ARABIC, ayah.surahNameArabic)
            .putInt(KEY_SURAH_NUMBER, ayah.surahNumber)
            .putInt(KEY_AYAH_NUMBER_IN_SURAH, ayah.ayahNumberInSurah)
            .putInt(KEY_GLOBAL_AYAH_NUMBER, ayah.globalAyahNumber)
            .putString(KEY_TRANSLATION_EDITION, ayah.translationEdition)
            .putString(KEY_FETCHED_DATE, ayah.fetchedDate)
            .apply()
    }

    actual override fun getCacheDate(): String? {
        return prefs.getString(KEY_FETCHED_DATE, null)
    }

    private companion object {
        private const val PREF_NAME = "daily_ayah_widget_cache"
        private const val KEY_ARABIC_TEXT = "key_arabic_text"
        private const val KEY_TRANSLATION_TEXT = "key_translation_text"
        private const val KEY_SURAH_NAME = "key_surah_name"
        private const val KEY_SURAH_NAME_ARABIC = "key_surah_name_arabic"
        private const val KEY_SURAH_NUMBER = "key_surah_number"
        private const val KEY_AYAH_NUMBER_IN_SURAH = "key_ayah_number_in_surah"
        private const val KEY_GLOBAL_AYAH_NUMBER = "key_global_ayah_number"
        private const val KEY_TRANSLATION_EDITION = "key_translation_edition"
        private const val KEY_FETCHED_DATE = "key_fetched_date"
    }
}

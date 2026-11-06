package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Reading presentation mode for the Quran recitation interface.
 */
enum class ReaderMode {
    /**
     * Vertical scrolling verse-by-verse list layout displaying Arabic text, translations, and audio controls.
     */
    VERSE_LIST,

    /**
     * Authentic Medina Mushaf page-by-page layout corresponding to traditional 604-page printings.
     */
    MUSHAF_PAGE
}

/**
 * Domain entity capturing the user's latest Quran reading position and preferred viewing mode.
 *
 * Persisted locally to enable immediate continuation of recitation across app restarts and
 * to populate the "Continue Reciting" hero card on the home and catalog screens.
 *
 * @property surahNumber The 1-indexed Surah ordinal number (`1..114`).
 * @property surahNameEnglish The transliterated English name of the Surah (e.g., "Al-Baqara").
 * @property surahNameArabic The Arabic script name of the Surah (e.g., "سُورَةُ البَقَرَةِ").
 * @property ayahNumberInSurah The 1-indexed Ayah number within the parent Surah.
 * @property pageNumber The 1-indexed Medina Mushaf page number (`1..604`).
 * @property juzNumber The 1-indexed Juz part number (`1..30`).
 * @property preferredReaderMode The user's chosen reader presentation mode at the time of saving.
 *                               Defaults to [ReaderMode.VERSE_LIST].
 * @property updatedAtEpochMillis Epoch timestamp in milliseconds indicating when this progress record was saved.
 * @see ReaderMode
 */
data class LastReadProgress(
    val surahNumber: Int,
    val surahNameEnglish: String,
    val surahNameArabic: String,
    val ayahNumberInSurah: Int,
    val pageNumber: Int,
    val juzNumber: Int,
    val preferredReaderMode: ReaderMode = ReaderMode.VERSE_LIST,
    val updatedAtEpochMillis: Long
)

/**
 * Domain entity representing a user-bookmarked Quranic Ayah (verse).
 *
 * Allows users to quickly reference and navigate back to specific verses of spiritual or study interest.
 *
 * @property globalAyahNumber The sequential global Ayah number across the entire Quran (`1..6236`).
 * @property surahNumber The 1-indexed Surah ordinal number (`1..114`).
 * @property surahNameEnglish The transliterated English name of the Surah.
 * @property surahNameArabic The Arabic script name of the Surah.
 * @property ayahNumberInSurah The 1-indexed Ayah number within the Surah.
 * @property pageNumber The 1-indexed Medina Mushaf page number (`1..604`).
 * @property arabicSnippet An excerpt or complete Arabic text of the bookmarked verse.
 * @property savedAtEpochMillis Epoch timestamp in milliseconds when the bookmark was created.
 */
data class AyahBookmark(
    val globalAyahNumber: Int,
    val surahNumber: Int,
    val surahNameEnglish: String,
    val surahNameArabic: String,
    val ayahNumberInSurah: Int,
    val pageNumber: Int,
    val arabicSnippet: String,
    val savedAtEpochMillis: Long
)


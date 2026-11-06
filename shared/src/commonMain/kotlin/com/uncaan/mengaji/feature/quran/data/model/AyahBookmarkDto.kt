package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serializable Data Transfer Object for persisting user-bookmarked Ayahs in local storage.
 *
 * Mapped to the domain model [com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark] via
 * [com.uncaan.mengaji.feature.quran.data.mapper.toDomain].
 *
 * @property globalAyahNumber The sequential global Ayah number across the entire Quran (`1..6236`).
 * @property surahNumber The 1-indexed Surah ordinal number (`1..114`).
 * @property surahNameEnglish The transliterated English name of the Surah.
 * @property surahNameArabic The Arabic script name of the Surah.
 * @property ayahNumberInSurah The 1-indexed Ayah number within the Surah.
 * @property pageNumber The 1-indexed Medina Mushaf page number (`1..604`).
 * @property arabicSnippet An excerpt or complete Arabic text of the bookmarked verse.
 * @property savedAtEpochMillis Epoch timestamp in milliseconds when the bookmark was stored.
 * @see com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
 */
@Serializable
data class AyahBookmarkDto(
    @SerialName("globalAyahNumber") val globalAyahNumber: Int,
    @SerialName("surahNumber") val surahNumber: Int,
    @SerialName("surahNameEnglish") val surahNameEnglish: String,
    @SerialName("surahNameArabic") val surahNameArabic: String,
    @SerialName("ayahNumberInSurah") val ayahNumberInSurah: Int,
    @SerialName("pageNumber") val pageNumber: Int,
    @SerialName("arabicSnippet") val arabicSnippet: String,
    @SerialName("savedAtEpochMillis") val savedAtEpochMillis: Long
)


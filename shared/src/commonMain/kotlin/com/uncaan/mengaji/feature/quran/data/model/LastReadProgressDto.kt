package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serializable Data Transfer Object for persisting the user's latest recitation progress in local storage.
 *
 * Mapped to the domain model [com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress] via
 * [com.uncaan.mengaji.feature.quran.data.mapper.toDomain].
 *
 * @property surahNumber The 1-indexed Surah ordinal number (`1..114`).
 * @property surahNameEnglish The transliterated English name of the Surah.
 * @property surahNameArabic The Arabic script name of the Surah.
 * @property ayahNumberInSurah The 1-indexed Ayah number within the Surah.
 * @property pageNumber The 1-indexed Medina Mushaf page number (`1..604`).
 * @property juzNumber The 1-indexed Juz part number (`1..30`).
 * @property preferredReaderMode The serialized representation of the user's chosen reader mode ("VERSE_LIST" or "MUSHAF_PAGE").
 * @property updatedAtEpochMillis Epoch timestamp in milliseconds when this progress record was persisted.
 * @see com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
 */
@Serializable
data class LastReadProgressDto(
    @SerialName("surahNumber") val surahNumber: Int,
    @SerialName("surahNameEnglish") val surahNameEnglish: String,
    @SerialName("surahNameArabic") val surahNameArabic: String,
    @SerialName("ayahNumberInSurah") val ayahNumberInSurah: Int,
    @SerialName("pageNumber") val pageNumber: Int,
    @SerialName("juzNumber") val juzNumber: Int,
    @SerialName("preferredReaderMode") val preferredReaderMode: String = "VERSE_LIST",
    @SerialName("updatedAtEpochMillis") val updatedAtEpochMillis: Long
)


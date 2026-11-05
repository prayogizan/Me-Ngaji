package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Pure domain entity representing a single Surah summary item in the 114-Surah catalog.
 *
 * Mapped from [com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto] in the data layer via
 * [com.uncaan.mengaji.feature.quran.data.mapper.toDomain].
 *
 * @property number The 1-indexed Surah ordinal number (1–114).
 * @property name The original Uthmani Arabic script name of the Surah (e.g., "سُورَةُ ٱلْفَاتِحَةِ").
 * @property englishName The transliterated English name of the Surah (e.g., "Al-Faatiha").
 * @property englishNameTranslation The English meaning of the Surah name (e.g., "The Opening").
 * @property numberOfAyahs The total number of verses (Ayahs) in this Surah.
 * @property revelationType The revelation classification ([RevelationType.MECCAN] or [RevelationType.MEDINAN]).
 * @see RevelationType
 * @see SurahDetail
 */
data class SurahSummary(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: RevelationType
)


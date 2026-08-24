package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Domain entity representing a Surah (chapter) of the Quran.
 *
 * @property number The 1-indexed Surah number (1–114).
 * @property name The original Arabic script name of the Surah (e.g. "سورة الفاتحة").
 * @property englishName Transliterated English name of the Surah (e.g. "Al-Faatiha").
 * @property englishNameTranslation English meaning of the Surah name (e.g. "The Opening").
 * @property numberOfAyahs Total number of verses in this Surah.
 * @property revelationType Revelation location category ("Meccan" or "Medinan").
 */
data class Surah(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
)

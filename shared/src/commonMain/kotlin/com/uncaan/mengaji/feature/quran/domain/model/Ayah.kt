package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Domain entity representing a single verse (Ayah) of the Quran with metadata.
 *
 * This is a pure Kotlin domain model decoupled from any external API structure or UI framework.
 *
 * @property number Global Ayah index within the entire Quran (1–6236).
 * @property text The Quranic Arabic text of the Ayah.
 * @property translation The translated text of the Ayah in the selected edition.
 * @property audioUrl Direct streaming URL for audio recitation, or `null` if unavailable.
 * @property surahNumber Ordinal number of the Surah (1–114).
 * @property surahName English transliteration of the Surah name (e.g. "Al-Baqara").
 * @property surahArabicName Arabic script name of the Surah (e.g. "سورة البقرة").
 * @property englishNameTranslation English meaning of the Surah name (e.g. "The Cow").
 * @property numberInSurah The Ayah's position within its Surah (1-indexed).
 * @property juz The Juz (part) number containing this Ayah (1–30).
 */
data class Ayah(
    val number: Int,
    val text: String,
    val translation: String,
    val audioUrl: String?,
    val surahNumber: Int,
    val surahName: String,
    val surahArabicName: String = "",
    val englishNameTranslation: String = "",
    val numberInSurah: Int,
    val juz: Int
)

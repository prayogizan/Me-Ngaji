package com.uncaan.mengaji.feature.widget.domain.model

/**
 * Represents a daily Quranic Ayah (verse) quote with both Arabic text and Indonesian translation.
 *
 * This entity serves as the domain model for daily quotes consumed by home screen widgets
 * (CMP-502), background workers (CMP-503), and notification/deep-link flows (CMP-504).
 *
 * @property arabicText Original Quranic verse text in Uthmani script.
 * @property translationText Indonesian translation of the verse.
 * @property surahName Latinized English name of the Surah (e.g., "Al-Baqara", "Saba").
 * @property surahNameArabic Original Arabic name of the Surah (e.g., "سُورَةُ سَبَإٍ").
 * @property surahNumber The 1-indexed Surah position in the Quran (1–114).
 * @property ayahNumberInSurah Ordinal position of this Ayah within its Surah.
 * @property globalAyahNumber Absolute Ayah index across the entire Quran (1–6236).
 * @property translationEdition Identifier for the translation source (e.g., "id.indonesian").
 * @property fetchedDate Date on which this Ayah was fetched and cached in ISO-8601 format ("yyyy-MM-dd").
 */
data class DailyAyah(
    val arabicText: String,
    val translationText: String,
    val surahName: String,
    val surahNameArabic: String,
    val surahNumber: Int,
    val ayahNumberInSurah: Int,
    val globalAyahNumber: Int,
    val translationEdition: String,
    val fetchedDate: String
)

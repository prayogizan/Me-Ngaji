package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Pure domain entity representing a single Medina Mushaf page (`1..604`) with its verses and metadata.
 *
 * Combines Uthmani Arabic script (`quran-uthmani`) and the selected translation edition
 * for all verses residing on the requested physical Mushaf page.
 *
 * @property pageNumber The 1-indexed Medina Mushaf page number (1–604).
 * @property ayahs The ordered list of [Ayah] verses appearing on this Mushaf page.
 * @property surahs The distinct list of [SurahSummary] chapters present on this page, ordered by Surah number.
 * @property juzNumber The Juz (part) number at the start of this page (1–30).
 * @property hizbQuarter The Hizb quarter index at the start of this page (1–240).
 * @property translationEdition The active translation edition identifier used to populate [Ayah.translation].
 * @see Ayah
 * @see SurahSummary
 */
data class MushafPage(
    val pageNumber: Int,
    val ayahs: List<Ayah>,
    val surahs: List<SurahSummary>,
    val juzNumber: Int,
    val hizbQuarter: Int,
    val translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION
)


package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Pure domain entity representing a complete Surah with its merged multi-edition verses.
 *
 * Combines Uthmani Arabic script (`quran-uthmani`), the selected translation text,
 * and verse-by-verse audio stream URLs into a unified list of [Ayah] domain entities.
 *
 * @property number The 1-indexed Surah ordinal number (1–114).
 * @property name The original Uthmani Arabic script name of the Surah.
 * @property englishName The transliterated English name of the Surah (e.g., "Al-Baqara").
 * @property englishNameTranslation The English meaning of the Surah name (e.g., "The Cow").
 * @property revelationType The revelation classification ([RevelationType.MECCAN] or [RevelationType.MEDINAN]).
 * @property numberOfAyahs The total number of verses in this Surah.
 * @property ayahs The ordered list of verses (`1..numberOfAyahs`) with merged Arabic, translation, and audio URLs.
 * @property translationEdition The active translation edition identifier used to populate [Ayah.translation].
 * @property audioEdition The active audio recitation edition identifier used to populate [Ayah.audioUrl].
 * @see Ayah
 * @see SurahSummary
 * @see RevelationType
 */
data class SurahDetail(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: RevelationType,
    val numberOfAyahs: Int,
    val ayahs: List<Ayah>,
    val translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
    val audioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
)


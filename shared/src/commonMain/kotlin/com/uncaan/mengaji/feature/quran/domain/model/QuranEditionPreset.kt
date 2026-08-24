package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Data model representing a user-selectable translation or recitation preset.
 *
 * @property identifier Unique API identifier for the edition (e.g. "en.sahih").
 * @property displayName Human-readable label for UI selection chips.
 * @property language Natural language name of the preset (e.g. "English", "Arabic").
 */
data class QuranEditionOption(
    val identifier: String,
    val displayName: String,
    val language: String
)

/**
 * Predefined translation and recitation edition presets supported by MeNgaji.
 */
object QuranEditionPresets {
    /** Pre-configured translation options (English, Indonesian). */
    val TRANSLATIONS = listOf(
        QuranEditionOption(
            identifier = "en.sahih",
            displayName = "Sahih International (English)",
            language = "English"
        ),
        QuranEditionOption(
            identifier = "id.indonesian",
            displayName = "Bahasa Indonesia (Kemenag)",
            language = "Indonesian"
        )
    )

    /** Pre-configured recitation options with high-quality audio streams. */
    val RECITATIONS = listOf(
        QuranEditionOption(
            identifier = "ar.alafasy",
            displayName = "Mishary Rashid Alafasy",
            language = "Arabic"
        ),
        QuranEditionOption(
            identifier = "ar.abdulbasitmurattal",
            displayName = "AbdulBaset AbdulSamad (Murattal)",
            language = "Arabic"
        )
    )

    /** Default translation edition identifier. */
    const val DEFAULT_TRANSLATION = "en.sahih"

    /** Default audio recitation edition identifier. */
    const val DEFAULT_RECITATION = "ar.alafasy"
}

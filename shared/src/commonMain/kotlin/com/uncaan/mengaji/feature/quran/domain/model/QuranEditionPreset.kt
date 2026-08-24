package com.uncaan.mengaji.feature.quran.domain.model

data class QuranEditionOption(
    val identifier: String,
    val displayName: String,
    val language: String
)

object QuranEditionPresets {
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

    const val DEFAULT_TRANSLATION = "en.sahih"
    const val DEFAULT_RECITATION = "ar.alafasy"
}

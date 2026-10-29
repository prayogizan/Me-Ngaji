package com.uncaan.mengaji.navigation

/**
 * Encapsulates type-safe deep-link parameters for navigating to a specific Ayah in the Quran reader.
 *
 * Typically populated from external intent sources such as Android home screen Glance widgets (CMP-504)
 * or push notifications, and propagated to [AlQuranRoute].
 *
 * @property surahNumber The 1-indexed Surah position in the Quran (1–114).
 * @property ayahNumber The 1-indexed ordinal position of the Ayah within its Surah.
 * @see AlQuranRoute
 * @see DeepLinkConstants
 */
data class AyahDeepLink(
    val surahNumber: Int,
    val ayahNumber: Int
)

/**
 * Canonical constant keys used for deep-link intent extras and Glance action parameter serializations.
 */
object DeepLinkConstants {
    /** Intent extra key and Glance parameter identifier for the Surah number. */
    const val EXTRA_SURAH_NUMBER = "surah_number"

    /** Intent extra key and Glance parameter identifier for the Ayah ordinal number. */
    const val EXTRA_AYAH_NUMBER = "ayah_number"
}


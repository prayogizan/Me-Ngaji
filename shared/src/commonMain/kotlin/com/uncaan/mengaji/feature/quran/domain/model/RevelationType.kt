package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Enumeration representing the historical place of revelation for a Quranic Surah.
 *
 * This is a pure Kotlin domain type with no framework or serialization dependencies.
 *
 * @property displayName Human-readable English label for UI presentation ("Meccan" or "Medinan").
 * @see SurahSummary
 * @see SurahDetail
 */
enum class RevelationType(val displayName: String) {

    /** Revealed before the Hijrah in Mecca. */
    MECCAN("Meccan"),

    /** Revealed after the Hijrah in Medina. */
    MEDINAN("Medinan");

    /**
     * Companion utilities for parsing API string values into [RevelationType].
     */
    companion object {

        /**
         * Parses a raw revelation type string from the remote API into a [RevelationType].
         *
         * Matches `"Medinan"` case-insensitively to [MEDINAN] and defaults all other values
         * (including `"Meccan"`, `null`, or blank strings) to [MECCAN].
         *
         * @param value The raw revelation type string returned by the API (e.g., "Meccan", "Medinan").
         * @return The corresponding [RevelationType] enum entry.
         */
        fun fromApiValue(value: String?): RevelationType {
            return if (value?.trim().equals("Medinan", ignoreCase = true)) {
                MEDINAN
            } else {
                MECCAN
            }
        }
    }
}


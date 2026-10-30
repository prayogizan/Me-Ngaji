package com.uncaan.mengaji.widget.config

/**
 * Visual color themes available for the MeNgaji Daily Ayah home screen widget.
 *
 * Persisted in [android.content.SharedPreferences] per widget instance ID using the string [storageKey].
 *
 * @property storageKey Raw string identifier stored in SharedPreferences.
 * @property displayName Human-readable localized label displayed in the configuration UI.
 */
enum class WidgetTheme(val storageKey: String, val displayName: String) {
    /**
     * Fixed warm ivory/cream background with espresso Quranic Arabic text.
     */
    LIGHT("light", "Terang"),

    /**
     * Fixed midnight navy background with luminous gold Quranic Arabic text.
     */
    DARK("dark", "Gelap"),

    /**
     * Automatically adapts between light and dark palettes following the system dark mode setting.
     */
    AUTO("auto", "Otomatis (Sistem)");

    companion object {
        /**
         * Resolves a [WidgetTheme] from its persisted [storageKey], falling back to [AUTO] if unknown.
         *
         * @param key The raw string key retrieved from storage, or `null`.
         * @return The matching [WidgetTheme] enum variant.
         */
        fun fromStorageKey(key: String?): WidgetTheme {
            return values().firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: AUTO
        }
    }
}

/**
 * Translation languages available for the MeNgaji Daily Ayah home screen widget.
 *
 * Controls the translation edition queried from the Al-Quran Cloud REST API.
 *
 * @property editionKey The Al-Quran Cloud API translation edition identifier (e.g. "id.indonesian").
 * @property displayName Localized label displayed in the configuration UI.
 */
enum class WidgetLanguage(val editionKey: String, val displayName: String) {
    /**
     * Indonesian Ministry of Religious Affairs (Kemenag) translation.
     */
    INDONESIAN("id.indonesian", "Bahasa Indonesia"),

    /**
     * English translation by Muhammad Asad.
     */
    ENGLISH("en.asad", "English (Muhammad Asad)");

    companion object {
        /**
         * Resolves a [WidgetLanguage] from its persisted [editionKey], falling back to [INDONESIAN] if unknown.
         *
         * @param key The raw edition string retrieved from storage, or `null`.
         * @return The matching [WidgetLanguage] enum variant.
         */
        fun fromEditionKey(key: String?): WidgetLanguage {
            return values().firstOrNull { it.editionKey.equals(key, ignoreCase = true) } ?: INDONESIAN
        }
    }
}

/**
 * Immutable configuration snapshot for a specific home screen widget instance.
 *
 * @property language Selected translation language for the widget. Defaults to [WidgetLanguage.INDONESIAN].
 * @property theme Selected visual color theme. Defaults to [WidgetTheme.AUTO].
 */
data class WidgetConfig(
    val language: WidgetLanguage = WidgetLanguage.INDONESIAN,
    val theme: WidgetTheme = WidgetTheme.AUTO
)

package com.uncaan.mengaji.widget.config

import com.uncaan.mengaji.widget.ui.theme.DailyAyahWidgetColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests verifying models, serialization keys, and theme tokens for widget configuration.
 */
class WidgetConfigModelsTest {

    @Test
    fun widgetTheme_fromStorageKey_resolvesExpectedVariants() {
        assertEquals(WidgetTheme.LIGHT, WidgetTheme.fromStorageKey("light"))
        assertEquals(WidgetTheme.DARK, WidgetTheme.fromStorageKey("dark"))
        assertEquals(WidgetTheme.AUTO, WidgetTheme.fromStorageKey("auto"))
        assertEquals(WidgetTheme.AUTO, WidgetTheme.fromStorageKey("UNKNOWN"))
        assertEquals(WidgetTheme.AUTO, WidgetTheme.fromStorageKey(null))
    }

    @Test
    fun widgetLanguage_fromEditionKey_resolvesExpectedVariants() {
        assertEquals(WidgetLanguage.INDONESIAN, WidgetLanguage.fromEditionKey("id.indonesian"))
        assertEquals(WidgetLanguage.ENGLISH, WidgetLanguage.fromEditionKey("en.asad"))
        assertEquals(WidgetLanguage.INDONESIAN, WidgetLanguage.fromEditionKey("fr.hamidullah"))
        assertEquals(WidgetLanguage.INDONESIAN, WidgetLanguage.fromEditionKey(null))
    }

    @Test
    fun dailyAyahWidgetConfigManager_keysMatchExpectedContract() {
        assertEquals("daily_ayah_widget_preferences", DailyAyahWidgetConfigManager.PREFS_NAME)
        assertEquals("widget_config_123_language", DailyAyahWidgetConfigManager.KEY_PREFIX_LANGUAGE.format(123))
        assertEquals("widget_config_123_theme", DailyAyahWidgetConfigManager.KEY_PREFIX_THEME.format(123))
    }

    @Test
    fun colorsForTheme_returnsValidTokensForAllThemes() {
        val lightTokens = DailyAyahWidgetColors.colorsForTheme(WidgetTheme.LIGHT)
        val darkTokens = DailyAyahWidgetColors.colorsForTheme(WidgetTheme.DARK)
        val autoTokens = DailyAyahWidgetColors.colorsForTheme(WidgetTheme.AUTO)

        assertNotNull(lightTokens)
        assertNotNull(darkTokens)
        assertNotNull(autoTokens)

        assertEquals(DailyAyahWidgetColors.lightTokens, lightTokens)
        assertEquals(DailyAyahWidgetColors.darkTokens, darkTokens)
        assertEquals(DailyAyahWidgetColors.autoTokens, autoTokens)
    }
}


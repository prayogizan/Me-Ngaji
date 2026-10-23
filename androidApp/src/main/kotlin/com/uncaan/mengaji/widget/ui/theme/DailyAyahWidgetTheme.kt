package com.uncaan.mengaji.widget.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider
import androidx.glance.unit.ColorProvider

/**
 * Color design tokens for the MeNgaji Daily Ayah home screen widget.
 *
 * Provides day (light) and night (dark) dynamic color pairings matching the MeNgaji
 * Islamic design palette:
 * - **Light Mode:** Warm parchment background (`#FFF8F0`), deep espresso Arabic text (`#3E2723`),
 *   and teal accent for surah info and translation (`#00695C`).
 * - **Dark Mode:** Deep navy midnight background (`#1A1A2E`), luminous gold Arabic text (`#FFD54F`),
 *   and high-contrast soft grey translation (`#E0E0E0`).
 *
 * All tokens expose [ColorProvider] instances directly compatible with Glance layouts.
 */
object DailyAyahWidgetColors {

    /**
     * Card background container color.
     */
    val background: ColorProvider = ColorProvider(
        day = Color(0xFFFFF8F0),
        night = Color(0xFF1A1A2E)
    )

    /**
     * Primary color for the Arabic Quranic script.
     */
    val arabicText: ColorProvider = ColorProvider(
        day = Color(0xFF3E2723),
        night = Color(0xFFFFD54F)
    )

    /**
     * Color for the Indonesian translation body text.
     */
    val translationText: ColorProvider = ColorProvider(
        day = Color(0xFF00695C),
        night = Color(0xFFE0E0E0)
    )

    /**
     * Accent color for surah name and verse index badge text.
     */
    val surahBadgeText: ColorProvider = ColorProvider(
        day = Color(0xFF00695C),
        night = Color(0xFFFFD54F)
    )

    /**
     * Subtle pill background color behind the surah name badge.
     */
    val surahBadgeBackground: ColorProvider = ColorProvider(
        day = Color(0x1F00695C),
        night = Color(0x2EFFD54F)
    )

    /**
     * Secondary muted text color for attribution and metadata.
     */
    val secondaryText: ColorProvider = ColorProvider(
        day = Color(0xFF795548),
        night = Color(0xFF90A4AE)
    )

    /**
     * Tint color for actionable icon buttons (e.g., refresh).
     */
    val iconTint: ColorProvider = ColorProvider(
        day = Color(0xFF00695C),
        night = Color(0xFFFFD54F)
    )

    /**
     * Button background for actionable touch targets inside the widget.
     */
    val buttonBackground: ColorProvider = ColorProvider(
        day = Color(0x1400695C),
        night = Color(0x26FFD54F)
    )
}

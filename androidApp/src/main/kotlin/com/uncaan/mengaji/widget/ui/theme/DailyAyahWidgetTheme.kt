package com.uncaan.mengaji.widget.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider
import androidx.glance.unit.ColorProvider
import com.uncaan.mengaji.widget.config.WidgetTheme

/**
 * Encapsulates the complete set of Glance [ColorProvider] tokens required to render
 * any Daily Ayah widget layout.
 *
 * @property background Card background container color.
 * @property arabicText Primary color for the Arabic Quranic script.
 * @property translationText Color for the translation body text.
 * @property surahBadgeText Accent color for Surah reference badge text.
 * @property surahBadgeBackground Pill container background color behind the Surah reference badge.
 * @property secondaryText Muted text color for metadata, attribution, and status messages.
 * @property iconTint Tint color for actionable icon buttons (e.g., refresh).
 * @property buttonBackground Background color for actionable touch targets.
 */
data class DailyAyahColorTokens(
    val background: ColorProvider,
    val arabicText: ColorProvider,
    val translationText: ColorProvider,
    val surahBadgeText: ColorProvider,
    val surahBadgeBackground: ColorProvider,
    val secondaryText: ColorProvider,
    val iconTint: ColorProvider,
    val buttonBackground: ColorProvider
)

/**
 * Color design tokens for the MeNgaji Daily Ayah home screen widget.
 *
 * Supports three theme modalities:
 * - **Light (Cream/Ivory):** Warm parchment background (`#FFF8F0`), deep espresso Arabic (`#3E2723`),
 *   and teal accent for translation (`#00695C`).
 * - **Dark (Navy/Gold):** Midnight navy background (`#1A1A2E`), luminous gold Arabic (`#FFD54F`),
 *   and high-contrast soft grey translation (`#E0E0E0`).
 * - **Auto (System):** Dynamically switches between the Light and Dark palettes based on the host device's
 *   system dark mode setting.
 *
 * @see DailyAyahColorTokens
 * @see WidgetTheme
 */
object DailyAyahWidgetColors {

    // Light Palette Constants
    val LightBackground = Color(0xFFFFF8F0)
    val LightArabicText = Color(0xFF3E2723)
    val LightTranslationText = Color(0xFF00695C)
    val LightSurahBadgeText = Color(0xFF00695C)
    val LightSurahBadgeBackground = Color(0x1F00695C)
    val LightSecondaryText = Color(0xFF795548)
    val LightIconTint = Color(0xFF00695C)
    val LightButtonBackground = Color(0x1400695C)

    // Dark Palette Constants
    val DarkBackground = Color(0xFF1A1A2E)
    val DarkArabicText = Color(0xFFFFD54F)
    val DarkTranslationText = Color(0xFFE0E0E0)
    val DarkSurahBadgeText = Color(0xFFFFD54F)
    val DarkSurahBadgeBackground = Color(0x2EFFD54F)
    val DarkSecondaryText = Color(0xFF90A4AE)
    val DarkIconTint = Color(0xFFFFD54F)
    val DarkButtonBackground = Color(0x26FFD54F)

    /** Fixed Light color tokens (applied in both day and night modes). */
    val lightTokens: DailyAyahColorTokens = DailyAyahColorTokens(
        background = ColorProvider(day = LightBackground, night = LightBackground),
        arabicText = ColorProvider(day = LightArabicText, night = LightArabicText),
        translationText = ColorProvider(day = LightTranslationText, night = LightTranslationText),
        surahBadgeText = ColorProvider(day = LightSurahBadgeText, night = LightSurahBadgeText),
        surahBadgeBackground = ColorProvider(day = LightSurahBadgeBackground, night = LightSurahBadgeBackground),
        secondaryText = ColorProvider(day = LightSecondaryText, night = LightSecondaryText),
        iconTint = ColorProvider(day = LightIconTint, night = LightIconTint),
        buttonBackground = ColorProvider(day = LightButtonBackground, night = LightButtonBackground)
    )

    /** Fixed Dark color tokens (applied in both day and night modes). */
    val darkTokens: DailyAyahColorTokens = DailyAyahColorTokens(
        background = ColorProvider(day = DarkBackground, night = DarkBackground),
        arabicText = ColorProvider(day = DarkArabicText, night = DarkArabicText),
        translationText = ColorProvider(day = DarkTranslationText, night = DarkTranslationText),
        surahBadgeText = ColorProvider(day = DarkSurahBadgeText, night = DarkSurahBadgeText),
        surahBadgeBackground = ColorProvider(day = DarkSurahBadgeBackground, night = DarkSurahBadgeBackground),
        secondaryText = ColorProvider(day = DarkSecondaryText, night = DarkSecondaryText),
        iconTint = ColorProvider(day = DarkIconTint, night = DarkIconTint),
        buttonBackground = ColorProvider(day = DarkButtonBackground, night = DarkButtonBackground)
    )

    /** Dynamic Auto tokens adapting between Light (day) and Dark (night) modes. */
    val autoTokens: DailyAyahColorTokens = DailyAyahColorTokens(
        background = ColorProvider(day = LightBackground, night = DarkBackground),
        arabicText = ColorProvider(day = LightArabicText, night = DarkArabicText),
        translationText = ColorProvider(day = LightTranslationText, night = DarkTranslationText),
        surahBadgeText = ColorProvider(day = LightSurahBadgeText, night = DarkSurahBadgeText),
        surahBadgeBackground = ColorProvider(day = LightSurahBadgeBackground, night = DarkSurahBadgeBackground),
        secondaryText = ColorProvider(day = LightSecondaryText, night = DarkSecondaryText),
        iconTint = ColorProvider(day = LightIconTint, night = DarkIconTint),
        buttonBackground = ColorProvider(day = LightButtonBackground, night = DarkButtonBackground)
    )

    /**
     * Resolves the appropriate [DailyAyahColorTokens] for a given [WidgetTheme].
     *
     * @param theme The chosen visual theme.
     * @return [DailyAyahColorTokens] corresponding to the theme selection.
     */
    fun colorsForTheme(theme: WidgetTheme): DailyAyahColorTokens {
        return when (theme) {
            WidgetTheme.LIGHT -> lightTokens
            WidgetTheme.DARK -> darkTokens
            WidgetTheme.AUTO -> autoTokens
        }
    }

    // Default backward-compatible references (delegating to autoTokens)
    val background: ColorProvider get() = autoTokens.background
    val arabicText: ColorProvider get() = autoTokens.arabicText
    val translationText: ColorProvider get() = autoTokens.translationText
    val surahBadgeText: ColorProvider get() = autoTokens.surahBadgeText
    val surahBadgeBackground: ColorProvider get() = autoTokens.surahBadgeBackground
    val secondaryText: ColorProvider get() = autoTokens.secondaryText
    val iconTint: ColorProvider get() = autoTokens.iconTint
    val buttonBackground: ColorProvider get() = autoTokens.buttonBackground
}

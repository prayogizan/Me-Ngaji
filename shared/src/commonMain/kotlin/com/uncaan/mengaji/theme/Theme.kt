package com.uncaan.mengaji.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = Color.White,
    secondary = SecondaryGold,
    onSecondary = Color.Black,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenLight,
    onPrimary = Color.Black,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = SecondaryGold,
    onSecondary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark
)

/**
 * Root Material 3 theme wrapper for the MeNgaji application.
 *
 * Automatically adapts between [LightColorScheme] and [DarkColorScheme] based on the
 * system dark theme state or explicit [darkTheme] override. Configures the typography
 * with the Amiri font family.
 *
 * @param darkTheme Whether dark color scheme should be rendered. Defaults to system setting via [isSystemInDarkTheme].
 * @param content The composable tree to be themed.
 * @see getAppTypography
 */
@Composable
fun MeNgajiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = getAppTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

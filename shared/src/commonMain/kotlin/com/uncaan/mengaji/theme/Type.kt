package com.uncaan.mengaji.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mengaji.shared.generated.resources.Res
import mengaji.shared.generated.resources.amiri_bold
import mengaji.shared.generated.resources.amiri_regular
import org.jetbrains.compose.resources.Font

/**
 * Resolves and constructs the [FontFamily] for the Amiri Arabic and Latin font resource.
 *
 * @return The configured Amiri [FontFamily].
 */
@Composable
fun getAmiriFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.amiri_regular, FontWeight.Normal),
        Font(Res.font.amiri_bold, FontWeight.Bold)
    )
}

/**
 * Returns the customized Material 3 [Typography] configuration for MeNgaji.
 *
 * Uses the Amiri font family for headline and title styles, and default system fonts
 * for body and label styles to maintain high readability.
 *
 * @return Configured Material 3 [Typography] instance.
 */
@Composable
fun getAppTypography(): Typography {
    val amiriFamily = getAmiriFontFamily()

    return Typography(
        headlineLarge = TextStyle(
            fontFamily = amiriFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = amiriFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),
        titleLarge = TextStyle(
            fontFamily = amiriFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    )
}

/**
 * Returns the dedicated [TextStyle] tailored for rendering Quranic Arabic script.
 *
 * Configures Amiri regular font with relaxed line height (44sp) to ensure diacritics
 * (Harakat / Tashkeel) do not collide.
 *
 * @return Quranic Arabic text style.
 */
@Composable
fun getQuranArabicTextStyle(): TextStyle {
    return TextStyle(
        fontFamily = getAmiriFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 44.sp
    )
}

package com.uncaan.mengaji.widget.config

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.R
import com.uncaan.mengaji.theme.PrimaryGreen
import com.uncaan.mengaji.theme.SecondaryGold
import com.uncaan.mengaji.theme.getQuranArabicTextStyle
import com.uncaan.mengaji.widget.ui.theme.DailyAyahWidgetColors

/**
 * Stateless Jetpack Compose screen rendering the Daily Ayah Widget Configuration interface.
 *
 * Provides:
 * - Live Preview panel rendering an authentic mock representation of the home screen widget.
 * - Translation language selector (Bahasa Indonesia vs English).
 * - Theme mode selector with visual color swatches (Light, Dark, and System Auto).
 * - Confirmation and cancellation action controls.
 *
 * @param uiState Current immutable [WidgetConfigUiState].
 * @param onAction Callback to dispatch [WidgetConfigUiAction] events.
 * @param onConfirm Callback invoked when the user confirms configuration to place the widget.
 * @param onCancel Callback invoked when the user cancels the configuration.
 * @param modifier Root layout modifier.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigContent(
    uiState: WidgetConfigUiState,
    onAction: (WidgetConfigUiAction) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.widget_config_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.widget_config_subtitle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.widget_config_cancel_button))
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text(
                        text = stringResource(R.string.widget_config_confirm_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Live Preview Panel
            WidgetLivePreviewCard(
                selectedTheme = uiState.selectedTheme,
                selectedLanguage = uiState.selectedLanguage
            )

            // Section 2: Translation Language Selector
            WidgetLanguageSelectorCard(
                selectedLanguage = uiState.selectedLanguage,
                onLanguageSelected = { onAction(WidgetConfigUiAction.SelectLanguage(it)) }
            )

            // Section 3: Widget Theme Selector
            WidgetThemeSelectorCard(
                selectedTheme = uiState.selectedTheme,
                onThemeSelected = { onAction(WidgetConfigUiAction.SelectTheme(it)) }
            )
        }
    }
}

/**
 * Renders an authentic live mock widget preview reflecting user configuration in real-time.
 *
 * @param selectedTheme Currently chosen [WidgetTheme].
 * @param selectedLanguage Currently chosen [WidgetLanguage].
 * @param modifier Composable modifier.
 */
@Composable
fun WidgetLivePreviewCard(
    selectedTheme: WidgetTheme,
    selectedLanguage: WidgetLanguage,
    modifier: Modifier = Modifier
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (selectedTheme) {
        WidgetTheme.LIGHT -> false
        WidgetTheme.DARK -> true
        WidgetTheme.AUTO -> isSystemDark
    }

    val previewBg = if (isDark) DailyAyahWidgetColors.DarkBackground else DailyAyahWidgetColors.LightBackground
    val previewArabic = if (isDark) DailyAyahWidgetColors.DarkArabicText else DailyAyahWidgetColors.LightArabicText
    val previewTranslation = if (isDark) DailyAyahWidgetColors.DarkTranslationText else DailyAyahWidgetColors.LightTranslationText
    val previewBadgeBg = if (isDark) DailyAyahWidgetColors.DarkSurahBadgeBackground else DailyAyahWidgetColors.LightSurahBadgeBackground
    val previewBadgeText = if (isDark) DailyAyahWidgetColors.DarkSurahBadgeText else DailyAyahWidgetColors.LightSurahBadgeText

    val translationText = when (selectedLanguage) {
        WidgetLanguage.INDONESIAN -> stringResource(R.string.widget_config_sample_trans_id)
        WidgetLanguage.ENGLISH -> stringResource(R.string.widget_config_sample_trans_en)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.widget_config_preview_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = previewBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header: Surah Badge & Mode Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(previewBadgeBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.widget_config_sample_surah),
                            color = previewBadgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "سُورَةُ البَقَرَةِ",
                        color = previewArabic,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Arabic Quran Script (Right-Aligned)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = stringResource(R.string.widget_config_sample_arabic),
                        style = getQuranArabicTextStyle(),
                        color = previewArabic,
                        fontSize = 20.sp,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Translation Text (Left-Aligned)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = translationText,
                        color = previewTranslation,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Card containing language selector options (Bahasa Indonesia vs English).
 */
@Composable
fun WidgetLanguageSelectorCard(
    selectedLanguage: WidgetLanguage,
    onLanguageSelected: (WidgetLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.widget_config_language_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            WidgetLanguage.values().forEach { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLanguageSelected(language) }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLanguage == language,
                        onClick = { onLanguageSelected(language) },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selectedLanguage == language) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = language.editionKey,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card containing theme selector swatches and options (Light, Dark, Auto).
 */
@Composable
fun WidgetThemeSelectorCard(
    selectedTheme: WidgetTheme,
    onThemeSelected: (WidgetTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.widget_config_theme_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WidgetTheme.values().forEach { theme ->
                    val isSelected = selectedTheme == theme
                    val borderColor = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.outlineVariant

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onThemeSelected(theme) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ThemeSwatchPreview(theme = theme)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = theme.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visual miniature swatch illustrating the theme's background and accent color tones.
 */
@Composable
fun ThemeSwatchPreview(
    theme: WidgetTheme,
    modifier: Modifier = Modifier
) {
    val swatchSize = 36.dp

    when (theme) {
        WidgetTheme.LIGHT -> {
            Box(
                modifier = modifier
                    .size(swatchSize)
                    .clip(CircleShape)
                    .background(DailyAyahWidgetColors.LightBackground)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(DailyAyahWidgetColors.LightTranslationText)
                )
            }
        }
        WidgetTheme.DARK -> {
            Box(
                modifier = modifier
                    .size(swatchSize)
                    .clip(CircleShape)
                    .background(DailyAyahWidgetColors.DarkBackground)
                    .border(1.dp, Color(0xFF33334D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(DailyAyahWidgetColors.DarkArabicText)
                )
            }
        }
        WidgetTheme.AUTO -> {
            Box(
                modifier = modifier
                    .size(swatchSize)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                DailyAyahWidgetColors.LightBackground,
                                DailyAyahWidgetColors.LightBackground,
                                DailyAyahWidgetColors.DarkBackground,
                                DailyAyahWidgetColors.DarkBackground
                            )
                        )
                    )
                    .border(1.dp, Color(0xFFCCCCCC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(SecondaryGold)
                )
            }
        }
    }
}

package com.uncaan.mengaji.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.uncaan.mengaji.MainActivity
import com.uncaan.mengaji.R
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.widget.action.RefreshAyahAction
import com.uncaan.mengaji.widget.action.createAyahDeepLinkAction
import com.uncaan.mengaji.widget.ui.theme.DailyAyahColorTokens
import com.uncaan.mengaji.widget.ui.theme.DailyAyahWidgetColors

/**
 * Small layout (2x2 grid cells) for the Daily Ayah home screen widget.
 *
 * Designed for compact square spaces:
 * - Header: Surah reference badge and refresh icon button.
 * - Arabic Section: Prominent, right-aligned Quranic text.
 * - Translation Section: Compact 2-line Indonesian translation text.
 *
 * Tapping container deep-links into [MainActivity] with Surah and Ayah parameters (CMP-504).
 * Refresh icon triggers [RefreshAyahAction].
 *
 * @param ayah The current [DailyAyah] domain model to render.
 * @param colors Color tokens matching the configured theme palette.
 * @param modifier Glance layout modifier applied to the root container.
 */
@Composable
fun DailyAyahSmallLayout(
    ayah: DailyAyah,
    colors: DailyAyahColorTokens = DailyAyahWidgetColors.autoTokens,
    modifier: GlanceModifier = GlanceModifier
) {
    val surahBadge = if (ayah.surahName.isNotBlank() && ayah.ayahNumberInSurah > 0) {
        "${ayah.surahName}: ${ayah.ayahNumberInSurah}"
    } else {
        "Ayat Harian"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .cornerRadius(16.dp)
            .padding(10.dp)
            .clickable(createAyahDeepLinkAction(ayah))
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // Header: Surah Badge & Refresh Action
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .background(colors.surahBadgeBackground)
                        .cornerRadius(6.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = surahBadge,
                        style = TextStyle(
                            color = colors.surahBadgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                Box(
                    modifier = GlanceModifier
                        .size(24.dp)
                        .background(colors.buttonBackground)
                        .cornerRadius(12.dp)
                        .clickable(actionRunCallback<RefreshAyahAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh),
                        contentDescription = "Refresh Daily Ayah",
                        modifier = GlanceModifier.size(14.dp),
                        colorFilter = ColorFilter.tint(colors.iconTint)
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Arabic text (Right-aligned)
            Text(
                text = ayah.arabicText,
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    color = colors.arabicText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                ),
                maxLines = 3
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Indonesian translation text
            Text(
                text = ayah.translationText,
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    color = colors.translationText,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Start
                ),
                maxLines = 2
            )
        }
    }
}

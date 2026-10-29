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
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.uncaan.mengaji.MainActivity
import com.uncaan.mengaji.R
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.widget.action.RefreshAyahAction
import com.uncaan.mengaji.widget.action.createAyahDeepLinkAction
import com.uncaan.mengaji.widget.ui.theme.DailyAyahWidgetColors

/**
 * Full layout (4x2 grid cells) for the Daily Ayah home screen widget.
 *
 * Displays the complete daily Quran verse experience:
 * - Header: Surah reference badge (e.g. "QS. Al-Baqarah: 255"), Surah Arabic title, and interactive refresh button.
 * - Arabic Section: Large, right-aligned Quranic Arabic script.
 * - Translation Section: Indonesian translation text with clear contrast.
 *
 * Tapping anywhere on the widget container deep-links into [MainActivity] with Surah and Ayah
 * parameters (CMP-504). Tapping the refresh icon triggers [RefreshAyahAction] to force-update today's verse.
 *
 * @param ayah The current [DailyAyah] domain model to render.
 * @param modifier Glance layout modifier applied to the root container.
 */
@Composable
fun DailyAyahFullLayout(
    ayah: DailyAyah,
    modifier: GlanceModifier = GlanceModifier
) {
    val surahBadge = if (ayah.surahName.isNotBlank() && ayah.ayahNumberInSurah > 0) {
        "QS. ${ayah.surahName}: ${ayah.ayahNumberInSurah}"
    } else {
        "Ayat Hari Ini"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DailyAyahWidgetColors.background)
            .cornerRadius(16.dp)
            .padding(14.dp)
            .clickable(createAyahDeepLinkAction(ayah))
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // Header Row: Surah Reference, Arabic Surah Name, and Refresh Button
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .background(DailyAyahWidgetColors.surahBadgeBackground)
                        .cornerRadius(8.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = surahBadge,
                        style = TextStyle(
                            color = DailyAyahWidgetColors.surahBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (ayah.surahNameArabic.isNotBlank()) {
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = ayah.surahNameArabic,
                        style = TextStyle(
                            color = DailyAyahWidgetColors.arabicText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                Box(
                    modifier = GlanceModifier
                        .size(28.dp)
                        .background(DailyAyahWidgetColors.buttonBackground)
                        .cornerRadius(14.dp)
                        .clickable(actionRunCallback<RefreshAyahAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh),
                        contentDescription = "Refresh Daily Ayah",
                        modifier = GlanceModifier.size(16.dp),
                        colorFilter = ColorFilter.tint(DailyAyahWidgetColors.iconTint)
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Quran Arabic Script (Right-Aligned for RTL)
            Text(
                text = ayah.arabicText,
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    color = DailyAyahWidgetColors.arabicText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                ),
                maxLines = 4
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Indonesian Translation Text
            Text(
                text = ayah.translationText,
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    color = DailyAyahWidgetColors.translationText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                ),
                maxLines = 3
            )
        }
    }
}

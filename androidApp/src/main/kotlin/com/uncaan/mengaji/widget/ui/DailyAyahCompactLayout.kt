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
 * Compact layout (4x1 grid cells) for the Daily Ayah home screen widget.
 *
 * Optimized for wide horizontal strips with constrained vertical space:
 * - Top Row: Surah badge on the left, right-aligned Arabic snippet, and refresh button.
 * - Bottom Row: Concise Indonesian translation snippet.
 *
 * Tapping anywhere deep-links into [MainActivity] with Surah and Ayah parameters (CMP-504).
 * Refresh icon invokes [RefreshAyahAction].
 *
 * @param ayah The current [DailyAyah] domain model to render.
 * @param modifier Glance layout modifier applied to the root container.
 */
@Composable
fun DailyAyahCompactLayout(
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
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(createAyahDeepLinkAction(ayah))
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Top Row: Surah reference, Arabic snippet, Refresh button
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .background(DailyAyahWidgetColors.surahBadgeBackground)
                        .cornerRadius(6.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
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

                Spacer(modifier = GlanceModifier.width(8.dp))

                Text(
                    text = ayah.arabicText,
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = DailyAyahWidgetColors.arabicText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    ),
                    maxLines = 1
                )

                Spacer(modifier = GlanceModifier.width(8.dp))

                Box(
                    modifier = GlanceModifier
                        .size(24.dp)
                        .background(DailyAyahWidgetColors.buttonBackground)
                        .cornerRadius(12.dp)
                        .clickable(actionRunCallback<RefreshAyahAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_refresh),
                        contentDescription = "Refresh Daily Ayah",
                        modifier = GlanceModifier.size(14.dp),
                        colorFilter = ColorFilter.tint(DailyAyahWidgetColors.iconTint)
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Bottom Row: Indonesian translation snippet
            Text(
                text = ayah.translationText,
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(
                    color = DailyAyahWidgetColors.translationText,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Start
                ),
                maxLines = 1
            )
        }
    }
}

package com.uncaan.mengaji.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.uncaan.mengaji.MainActivity
import com.uncaan.mengaji.widget.action.RefreshAyahAction
import com.uncaan.mengaji.widget.ui.theme.DailyAyahWidgetColors

/**
 * Fallback Glance UI layout displayed when no cached Daily Ayah is available, during initial load,
 * or when an offline network failure occurs without a prior cached verse.
 *
 * Provides a user-friendly status message along with a retry/refresh action trigger.
 * Tapping the container opens [MainActivity], while tapping the action button runs [RefreshAyahAction].
 *
 * @param message Informational or instructional message displayed to the user.
 * @param isError Indicates whether this fallback represents an error state requiring a retry action.
 * @param modifier Glance layout modifier applied to the root container.
 */
@Composable
fun DailyAyahFallbackLayout(
    message: String,
    isError: Boolean = false,
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DailyAyahWidgetColors.background)
            .cornerRadius(16.dp)
            .padding(14.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MeNgaji",
                style = TextStyle(
                    color = DailyAyahWidgetColors.surahBadgeText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            Text(
                text = message,
                style = TextStyle(
                    color = DailyAyahWidgetColors.secondaryText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 3
            )

            if (isError) {
                Spacer(modifier = GlanceModifier.height(10.dp))

                Box(
                    modifier = GlanceModifier
                        .background(DailyAyahWidgetColors.buttonBackground)
                        .cornerRadius(12.dp)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable(actionRunCallback<RefreshAyahAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Muat Ulang",
                        style = TextStyle(
                            color = DailyAyahWidgetColors.surahBadgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

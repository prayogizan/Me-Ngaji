package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.SecondaryGold
import com.uncaan.mengaji.theme.SecondaryGoldLight
import com.uncaan.mengaji.theme.getQuranArabicTextStyle

/**
 * Hero Banner Card displaying the user's latest Quran reading checkpoint with 1-tap continuation.
 *
 * Designed to match Figma Screen 01 specifications (`node-id=1-36`) featuring an emerald gradient
 * container, Gold accent tags, Arabic calligraphy title, and a high-contrast resume button.
 *
 * @param progress The persisted [LastReadProgress] entity capturing the current reading position.
 * @param onResume Callback invoked when the user taps the card or the "Continue Reciting" button.
 * @param modifier Layout modifier applied to the root card container.
 * @see LastReadProgress
 */
@Composable
fun LastReadHeroCard(
    progress: LastReadProgress,
    onResume: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF044736),
            Color(0xFF065F46)
        )
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onResume)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SecondaryGold
                    ) {
                        Text(
                            text = "LAST READ • PAGE ${progress.pageNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Color(0xFF121413),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33000000)
                    ) {
                        Text(
                            text = "🔥 5-Day Streak",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Color(0xFFFFFDF7),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Middle Content: Surah Names & Verse metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = progress.surahNameEnglish,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ayah ${progress.ayahNumberInSurah} • Juz ${progress.juzNumber}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp
                            ),
                            color = SecondaryGoldLight
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = progress.surahNameArabic,
                            style = getQuranArabicTextStyle().copy(
                                fontSize = 22.sp,
                                lineHeight = 32.sp
                            ),
                            color = SecondaryGoldLight,
                            textAlign = TextAlign.Right
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onResume,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondaryGold,
                            contentColor = Color(0xFF121413)
                        )
                    ) {
                        Text(
                            text = "Continue Reciting ▶",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    val modeLabel = when (progress.preferredReaderMode) {
                        ReaderMode.VERSE_LIST -> "Verse List Mode"
                        ReaderMode.MUSHAF_PAGE -> "Mushaf Page Mode"
                    }

                    Text(
                        text = "Saved in $modeLabel",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp
                        ),
                        color = Color(0xFFE2E3DF)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LastReadHeroCardPreview() {
    MeNgajiTheme {
        LastReadHeroCard(
            progress = LastReadProgress(
                surahNumber = 2,
                surahNameEnglish = "Al-Baqarah",
                surahNameArabic = "سُورَةُ البَقَرَةِ",
                ayahNumberInSurah = 255,
                pageNumber = 42,
                juzNumber = 3,
                preferredReaderMode = ReaderMode.VERSE_LIST,
                updatedAtEpochMillis = 0L
            ),
            onResume = {}
        )
    }
}


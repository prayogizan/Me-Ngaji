package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uncaan.mengaji.core.audio.AudioRepeatMode
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.feature.quran.presentation.QuranReaderState
import com.uncaan.mengaji.feature.quran.presentation.QuranUiAction
import com.uncaan.mengaji.theme.SecondaryGold

/**
 * Floating thumb-zone recitation audio transport dock matching Figma `ThumbZoneRecitationDock` (node `1:140`).
 *
 * Provides one-handed ergonomic controls at the bottom of the reader screen:
 * - Real-time active Surah and Ayah index display.
 * - Qari metadata and playback status indicator.
 * - Linear track queue progress bar.
 * - Playback rate selection multiplier (`0.75x`, `1.0x`, `1.25x`, `1.5x`).
 * - Repeat mode cycle toggle (`NONE`, `REPEAT_AYAH`, `REPEAT_SURAH`).
 * - Previous verse, Play/Pause toggle, and Next verse transport buttons.
 * - Smooth dismissal during distraction-free *Khushu'* immersive mode.
 *
 * @param readerState Current reader state holding active Surah and verse count.
 * @param audioState Current audio playback engine state.
 * @param onAction Action dispatcher for audio transport events.
 * @param modifier Layout modifier applied to the floating container.
 * @see AudioState
 * @see QuranReaderState
 * @see QuranUiAction
 */
@Composable
fun RecitationAudioDock(
    readerState: QuranReaderState,
    audioState: AudioState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !readerState.isKhushuMode,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF044736), // Authentic Deep Emerald Islamic Dock Palette
            shadowElevation = 8.dp,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                // Top Mini Queue Progress Line
                val currentAyahInSurah = audioState.currentAyahNumberInSurah ?: readerState.currentAyahNumber
                val totalAyahsInSurah = readerState.surahDetail?.numberOfAyahs ?: 1
                val progressFraction = (currentAyahInSurah.toFloat() / totalAyahsInSurah.toFloat()).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = SecondaryGold,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Recitation Title & Metadata
                val activeSurahName = readerState.surahDetail?.englishName ?: "Surah ${readerState.currentSurahNumber}"
                Text(
                    text = "$activeSurahName • Ayah $currentAyahInSurah of $totalAyahsInSurah",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Qari: Mishary Rashid Alafasy • ar.alafasy",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Transport & Audio Configuration Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Playback Speed Selector Chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.clickable {
                            val nextSpeed = when (audioState.playbackSpeed) {
                                1.0f -> 1.25f
                                1.25f -> 1.5f
                                1.5f -> 0.75f
                                else -> 1.0f
                            }
                            onAction(QuranUiAction.SetPlaybackSpeed(nextSpeed))
                        }
                    ) {
                        Text(
                            text = "${audioState.playbackSpeed}x",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Repeat Mode Toggle Chip
                    val repeatLabel = when (audioState.repeatMode) {
                        AudioRepeatMode.NONE -> "🔁 Off"
                        AudioRepeatMode.REPEAT_AYAH -> "🔁 1x Ayah"
                        AudioRepeatMode.REPEAT_SURAH -> "🔁 Surah"
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (audioState.repeatMode != AudioRepeatMode.NONE) {
                            SecondaryGold.copy(alpha = 0.35f)
                        } else {
                            Color.White.copy(alpha = 0.15f)
                        },
                        modifier = Modifier.clickable { onAction(QuranUiAction.CycleRepeatMode) }
                    ) {
                        Text(
                            text = repeatLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (audioState.repeatMode != AudioRepeatMode.NONE) SecondaryGold else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Transport Navigation Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Skip Previous Button (⏮)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable { onAction(QuranUiAction.SkipToPreviousVerse) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⏮", fontSize = 13.sp, color = Color.White)
                        }

                        // Play / Pause Toggle Button (▶ / ⏸)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SecondaryGold)
                                .clickable {
                                    if (audioState.isPlaying) {
                                        onAction(QuranUiAction.PauseAudio)
                                    } else if (audioState.isPaused) {
                                        onAction(QuranUiAction.ResumeAudio)
                                    } else {
                                        // If idle, start recitation from current verse
                                        val currentAyah = readerState.surahDetail?.ayahs?.find {
                                            it.numberInSurah == readerState.currentAyahNumber
                                        } ?: readerState.surahDetail?.ayahs?.firstOrNull()
                                        if (currentAyah != null) {
                                            onAction(QuranUiAction.PlayFromAyah(currentAyah))
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (audioState.isBuffering) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF044736),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (audioState.isPlaying) "⏸" else "▶",
                                    fontSize = 16.sp,
                                    color = Color(0xFF044736)
                                )
                            }
                        }

                        // Skip Next Button (⏭)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable { onAction(QuranUiAction.SkipToNextVerse) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⏭", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.theme.getQuranArabicTextStyle

/**
 * Elevated card component rendering full Ayah details and integrated audio controls.
 *
 * Displays the Surah header with metadata badge, Quranic Arabic text rendered in
 * RTL direction with Amiri font typography, translation text, and audio recitation
 * controls with visual border highlights during active playback.
 *
 * @param ayah The domain [Ayah] model to display.
 * @param translationEdition The active translation edition identifier.
 * @param recitationEdition The active audio recitation edition identifier.
 * @param audioState The current playback state of the audio engine.
 * @param onPlay Callback dispatched when the play button is tapped with an audio URL.
 * @param onPause Callback dispatched to pause active playback.
 * @param onResume Callback dispatched to resume paused playback.
 * @param onStop Callback dispatched to stop and reset playback.
 * @param modifier Layout modifier applied to the root card container.
 * @see SurahMetadataHeader
 * @see AyahAudioControls
 * @see getQuranArabicTextStyle
 */
@Composable
fun AyahCard(
    ayah: Ayah,
    translationEdition: String,
    recitationEdition: String,
    audioState: AudioState = AudioState.Idle,
    onPlay: (String) -> Unit = {},
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onStop: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isPlaying = audioState is AudioState.Playing
    val isBuffering = audioState is AudioState.Buffering
    val activeBorderColor by animateColorAsState(
        targetValue = when {
            isPlaying -> MaterialTheme.colorScheme.secondary
            isBuffering -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        label = "AyahCardBorderColor"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isPlaying || isBuffering) 1.5.dp else 0.dp,
                color = activeBorderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isPlaying) 6.dp else 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Surah Metadata Header
            SurahMetadataHeader(ayah = ayah)

            Spacer(modifier = Modifier.height(16.dp))

            // Arabic Quranic Text (RTL)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = ayah.text,
                        style = getQuranArabicTextStyle(),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Translation Text (LTR)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Translation",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ayah.translation.ifBlank { ayah.text },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Recitation Controls
            AyahAudioControls(
                audioUrl = ayah.audioUrl,
                audioState = audioState,
                onPlay = onPlay,
                onPause = onPause,
                onResume = onResume,
                onStop = onStop,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SurahMetadataHeader(
    ayah: Ayah,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ayah.surahName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (ayah.surahArabicName.isNotBlank()) {
                    Text(
                        text = " (${ayah.surahArabicName})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (ayah.englishNameTranslation.isNotBlank()) {
                Text(
                    text = ayah.englishNameTranslation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SuggestionChip(
            onClick = {},
            label = {
                Text(
                    text = "${ayah.surahNumber}:${ayah.numberInSurah} • Juz ${ayah.juz}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                labelColor = MaterialTheme.colorScheme.primary
            ),
            border = null
        )
    }
}


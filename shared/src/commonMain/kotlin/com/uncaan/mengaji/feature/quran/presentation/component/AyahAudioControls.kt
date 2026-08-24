package com.uncaan.mengaji.feature.quran.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uncaan.mengaji.core.audio.AudioState
import mengaji.shared.generated.resources.Res
import mengaji.shared.generated.resources.ic_pause
import mengaji.shared.generated.resources.ic_play_arrow
import mengaji.shared.generated.resources.ic_stop
import org.jetbrains.compose.resources.painterResource

/**
 * Audio playback controls component for an Ayah card.
 *
 * Provides responsive Play, Pause, Resume, and Stop controls synchronized with [AudioState],
 * animated playing equalizer bars, buffering progress indicators, and disabled fallback
 * states for recitations without audio streams.
 *
 * @param audioUrl The HTTPS URL of the audio recitation stream, or `null` if unavailable.
 * @param audioState The current playback state of the audio engine.
 * @param onPlay Callback dispatched when the play button is tapped with a valid audio URL.
 * @param onPause Callback dispatched to pause active playback.
 * @param onResume Callback dispatched to resume paused playback.
 * @param onStop Callback dispatched to stop and reset playback.
 * @param modifier Layout modifier applied to the control container.
 * @see AudioState
 * @see EqualizerBarsAnimation
 */
@Composable
fun AyahAudioControls(
    audioUrl: String?,
    audioState: AudioState,
    onPlay: (String) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAudioAvailable = !audioUrl.isNullOrBlank()
    val isPlaying = audioState is AudioState.Playing
    val isBuffering = audioState is AudioState.Buffering
    val isPaused = audioState is AudioState.Paused
    val isActive = isPlaying || isBuffering || isPaused

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play / Pause / Buffering Interactive Button
            FilledIconButton(
                onClick = {
                    if (!isAudioAvailable) return@FilledIconButton
                    when (audioState) {
                        is AudioState.Playing -> onPause()
                        is AudioState.Paused -> onResume()
                        is AudioState.Buffering -> onPause()
                        is AudioState.Idle, is AudioState.Error -> onPlay(audioUrl!!)
                    }
                },
                enabled = isAudioAvailable,
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isPlaying) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    contentColor = if (isPlaying) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.size(44.dp)
            ) {
                AnimatedContent(
                    targetState = audioState,
                    transitionSpec = {
                        (scaleIn(tween(200)) + fadeIn(tween(200))) togetherWith
                                (scaleOut(tween(200)) + fadeOut(tween(200)))
                    },
                    label = "AudioPlayPauseIconTransition"
                ) { state ->
                    when (state) {
                        is AudioState.Buffering -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        is AudioState.Playing -> {
                            Icon(
                                painter = painterResource(Res.drawable.ic_pause),
                                contentDescription = "Pause Audio Recitation",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        is AudioState.Paused, is AudioState.Idle, is AudioState.Error -> {
                            Icon(
                                painter = painterResource(Res.drawable.ic_play_arrow),
                                contentDescription = "Play Audio Recitation",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Stop Button (visible when active)
            AnimatedVisibility(
                visible = isActive,
                enter = fadeIn(tween(200)) + scaleIn(tween(200)),
                exit = fadeOut(tween(200)) + scaleOut(tween(200))
            ) {
                OutlinedIconButton(
                    onClick = onStop,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_stop),
                        contentDescription = "Stop Audio Recitation",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (!isAudioAvailable) {
                Text(
                    text = "No audio available for this edition",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Animated Equalizer Indicator when Playing
        AnimatedVisibility(
            visible = isPlaying,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250))
        ) {
            EqualizerBarsAnimation(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

/**
 * Animated equalizer bars reflecting active audio playback.
 *
 * Renders three vertical rounded bars animating at distinct intervals and heights to
 * provide a lively, premium visual feedback indicator.
 *
 * @param color The accent color of the equalizer bars. Defaults to secondary gold.
 * @param modifier Layout modifier applied to the animation container.
 */
@Composable
fun EqualizerBarsAnimation(
    color: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "EqualizerTransition")

    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bar1Height"
    )

    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bar2Height"
    )

    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bar3Height"
    )

    Row(
        modifier = modifier.height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.5.dp)
                .height(bar1Height.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Box(
            modifier = Modifier
                .width(3.5.dp)
                .height(bar2Height.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Box(
            modifier = Modifier
                .width(3.5.dp)
                .height(bar3Height.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
    }
}

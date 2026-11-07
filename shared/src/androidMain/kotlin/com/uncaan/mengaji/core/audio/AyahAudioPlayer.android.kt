package com.uncaan.mengaji.core.audio

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Android implementation of [AyahAudioPlayer] using AndroidX Media3 ExoPlayer.
 *
 * Supports gapless multi-verse queue streaming via [ExoPlayer.setMediaItems], real-time
 * verse metadata emission on media item transitions, playback rate scaling, and loop controls.
 *
 * @param context The Android application context for ExoPlayer initialization.
 * @see AyahAudioPlayer
 * @see AudioState
 * @see AudioPlayer
 * @see AudioTrack
 * @see AudioRepeatMode
 */
actual class AyahAudioPlayer(context: Context) : AudioPlayer {

    private val _audioState = MutableStateFlow(AudioState.IDLE)

    /**
     * Observable playback state emitted as a [StateFlow].
     */
    actual override val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build().apply {
        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _audioState.update { it.copy(isBuffering = true, isPlaying = false) }
                    }
                    Player.STATE_READY -> {
                        _audioState.update {
                            it.copy(
                                isBuffering = false,
                                isPlaying = playWhenReady
                            )
                        }
                    }
                    Player.STATE_ENDED -> {
                        _audioState.update { it.copy(isPlaying = false, isBuffering = false) }
                    }
                    Player.STATE_IDLE -> {
                        if (_audioState.value.error == null) {
                            _audioState.update { it.copy(isPlaying = false, isBuffering = false) }
                        }
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playbackState == Player.STATE_READY) {
                    _audioState.update { it.copy(isPlaying = playWhenReady, isBuffering = false) }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val track = mediaItem?.localConfiguration?.tag as? AudioTrack
                if (track != null) {
                    _audioState.update {
                        it.copy(
                            currentUrl = track.url,
                            currentSurahNumber = if (track.surahNumber > 0) track.surahNumber else null,
                            currentAyahNumberInSurah = if (track.ayahNumberInSurah > 0) track.ayahNumberInSurah else null,
                            currentGlobalAyahNumber = if (track.globalAyahNumber > 0) track.globalAyahNumber else null,
                            error = null
                        )
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _audioState.update {
                    it.copy(
                        isPlaying = false,
                        isBuffering = false,
                        error = error.message ?: "Playback error"
                    )
                }
            }
        })
    }

    /**
     * Begins playback from a single remote audio URL.
     *
     * Preserves backward compatibility by wrapping the URL in a transient [AudioTrack].
     *
     * @param url The HTTPS URL of the audio stream.
     */
    actual override fun play(url: String) {
        val singleTrack = AudioTrack(
            url = url,
            surahNumber = 0,
            ayahNumberInSurah = 0,
            globalAyahNumber = 0
        )
        playQueue(listOf(singleTrack), 0)
    }

    /**
     * Initializes and begins continuous playback across a queue of verses.
     *
     * @param tracks The ordered list of [AudioTrack] instances.
     * @param startIndex The 0-based index to start playing from.
     */
    actual override fun playQueue(tracks: List<AudioTrack>, startIndex: Int) {
        if (tracks.isEmpty()) return

        try {
            _audioState.update {
                it.copy(
                    isBuffering = true,
                    isPlaying = false,
                    error = null
                )
            }

            val mediaItems = tracks.map { track ->
                MediaItem.Builder()
                    .setUri(track.url)
                    .setMediaId("${track.surahNumber}:${track.ayahNumberInSurah}")
                    .setTag(track)
                    .build()
            }

            val safeIndex = startIndex.coerceIn(0, tracks.lastIndex)
            player.setMediaItems(mediaItems, safeIndex, C.TIME_UNSET)
            player.prepare()
            player.setPlaybackSpeed(_audioState.value.playbackSpeed)
            player.playWhenReady = true
        } catch (e: Exception) {
            _audioState.update {
                it.copy(
                    isPlaying = false,
                    isBuffering = false,
                    error = e.message ?: "Failed to play audio queue"
                )
            }
        }
    }

    /**
     * Pauses the current playback.
     */
    actual override fun pause() {
        player.playWhenReady = false
        _audioState.update { it.copy(isPlaying = false) }
    }

    /**
     * Resumes paused playback.
     */
    actual override fun resume() {
        player.playWhenReady = true
        _audioState.update { it.copy(isPlaying = true) }
    }

    /**
     * Stops playback and clears the active media items queue.
     */
    actual override fun stop() {
        player.stop()
        player.clearMediaItems()
        _audioState.update {
            it.copy(
                isPlaying = false,
                isBuffering = false,
                currentUrl = null,
                currentSurahNumber = null,
                currentAyahNumberInSurah = null,
                currentGlobalAyahNumber = null,
                error = null
            )
        }
    }

    /**
     * Advances playback to the next verse in the queue if available.
     */
    actual override fun skipToNext() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }

    /**
     * Navigates playback to the previous verse in the queue if available.
     */
    actual override fun skipToPrevious() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        }
    }

    /**
     * Configures the audio playback rate multiplier.
     *
     * @param speed The playback speed factor (e.g. `0.75f`, `1.0f`, `1.25f`, `1.5f`).
     */
    actual override fun setPlaybackSpeed(speed: Float) {
        val clampedSpeed = speed.coerceIn(0.5f, 2.0f)
        player.setPlaybackSpeed(clampedSpeed)
        _audioState.update { it.copy(playbackSpeed = clampedSpeed) }
    }

    /**
     * Configures the looping behavior for verses and queue completion.
     *
     * @param mode The desired [AudioRepeatMode] behavior.
     */
    actual override fun setRepeatMode(mode: AudioRepeatMode) {
        val media3RepeatMode = when (mode) {
            AudioRepeatMode.NONE -> Player.REPEAT_MODE_OFF
            AudioRepeatMode.REPEAT_AYAH -> Player.REPEAT_MODE_ONE
            AudioRepeatMode.REPEAT_SURAH -> Player.REPEAT_MODE_ALL
        }
        player.repeatMode = media3RepeatMode
        _audioState.update { it.copy(repeatMode = mode) }
    }

    /**
     * Releases all native ExoPlayer resources.
     */
    actual override fun release() {
        player.stop()
        player.clearMediaItems()
        player.release()
        _audioState.value = AudioState.IDLE
    }
}

package com.uncaan.mengaji.core.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android implementation of [AyahAudioPlayer] using AndroidX Media3 ExoPlayer.
 *
 * Requires an Android [Context] for ExoPlayer initialization. Playback state changes
 * are reported via the [Player.Listener] callback mechanism and emitted through [audioState].
 *
 * @param context The Android application context for ExoPlayer setup.
 * @see AyahAudioPlayer
 * @see AudioState
 * @see AudioPlayer
 */
actual class AyahAudioPlayer(context: Context) : AudioPlayer {

    private val _audioState = MutableStateFlow<AudioState>(AudioState.Idle)

    /**
     * Observable playback state emitted as a [StateFlow].
     */
    actual override val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build().apply {
        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> _audioState.value = AudioState.Buffering
                    Player.STATE_READY -> {
                        _audioState.value = if (playWhenReady) AudioState.Playing else AudioState.Paused
                    }
                    Player.STATE_ENDED -> _audioState.value = AudioState.Idle
                    Player.STATE_IDLE -> {
                        if (_audioState.value !is AudioState.Error) {
                            _audioState.value = AudioState.Idle
                        }
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playbackState == Player.STATE_READY) {
                    _audioState.value = if (playWhenReady) AudioState.Playing else AudioState.Paused
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _audioState.value = AudioState.Error(error.message ?: "Playback error")
            }
        })
    }

    /**
     * Begins streaming and playback from the given audio URL.
     *
     * @param url The HTTPS URL of the audio file to stream.
     */
    actual override fun play(url: String) {
        try {
            _audioState.value = AudioState.Buffering
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        } catch (e: Exception) {
            _audioState.value = AudioState.Error(e.message ?: "Failed to play audio")
        }
    }

    /**
     * Pauses the current playback.
     */
    actual override fun pause() {
        player.playWhenReady = false
        _audioState.value = AudioState.Paused
    }

    /**
     * Resumes paused playback.
     */
    actual override fun resume() {
        player.playWhenReady = true
        _audioState.value = AudioState.Playing
    }

    /**
     * Stops playback and resets position.
     */
    actual override fun stop() {
        player.stop()
        _audioState.value = AudioState.Idle
    }

    /**
     * Releases all native ExoPlayer resources.
     */
    actual override fun release() {
        player.stop()
        player.release()
        _audioState.value = AudioState.Idle
    }
}

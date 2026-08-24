package com.uncaan.mengaji.core.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.Foundation.NSURL

/**
 * iOS implementation of [AyahAudioPlayer] using AVFoundation AVPlayer.
 *
 * Configures [AVAudioSession] with `AVAudioSessionCategoryPlayback` on initialization
 * to allow uninterrupted audio recitation even when the app is backgrounded.
 *
 * @see AyahAudioPlayer
 * @see AudioState
 * @see AudioPlayer
 */
@OptIn(ExperimentalForeignApi::class)
actual class AyahAudioPlayer : AudioPlayer {

    private val _audioState = MutableStateFlow<AudioState>(AudioState.Idle)

    /**
     * Observable playback state emitted as a [StateFlow].
     */
    actual override val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private var player: AVPlayer = AVPlayer()

    init {
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, error = null)
            session.setActive(true, error = null)
        } catch (_: Exception) {
            // Best effort session activation
        }
    }

    /**
     * Begins streaming and playback from the given audio URL.
     *
     * @param url The HTTPS URL of the audio file to stream.
     */
    actual override fun play(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: run {
            _audioState.value = AudioState.Error("Invalid audio URL")
            return
        }
        val item = AVPlayerItem(uRL = nsUrl)
        _audioState.value = AudioState.Buffering
        player.replaceCurrentItemWithPlayerItem(item)
        player.play()
        _audioState.value = AudioState.Playing
    }

    /**
     * Pauses the current playback.
     */
    actual override fun pause() {
        player.pause()
        _audioState.value = AudioState.Paused
    }

    /**
     * Resumes paused playback.
     */
    actual override fun resume() {
        player.play()
        _audioState.value = AudioState.Playing
    }

    /**
     * Stops playback and resets the current media item.
     */
    actual override fun stop() {
        player.pause()
        player.replaceCurrentItemWithPlayerItem(null)
        _audioState.value = AudioState.Idle
    }

    /**
     * Releases player resources and resets state to [AudioState.Idle].
     */
    actual override fun release() {
        stop()
    }
}

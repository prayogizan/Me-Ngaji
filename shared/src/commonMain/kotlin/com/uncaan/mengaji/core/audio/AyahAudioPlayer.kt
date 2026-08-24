package com.uncaan.mengaji.core.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Cross-platform audio player for Quran Ayah recitation.
 *
 * Provides a unified API for play, pause, resume, stop, and release operations.
 * The current playback state is observable via [audioState] as a `StateFlow<AudioState>`.
 *
 * **Platform implementations:**
 * - **Android:** Uses AndroidX Media3 ExoPlayer for streaming playback.
 * - **iOS:** Uses AVFoundation AVPlayer with `AVAudioSessionCategoryPlayback`.
 *
 * Callers must invoke [release] when the player is no longer needed to free
 * native resources and prevent memory leaks.
 *
 * @see AudioState
 */
expect class AyahAudioPlayer {
    /** Observable playback state emitted as a [StateFlow]. */
    val audioState: StateFlow<AudioState>

    /**
     * Begins playback from the given audio URL.
     *
     * If a previous track is playing, it is replaced. The state transitions
     * through [AudioState.Buffering] → [AudioState.Playing].
     *
     * @param url The HTTPS URL of the audio file to stream.
     */
    fun play(url: String)

    /** Pauses the current playback. State transitions to [AudioState.Paused]. */
    fun pause()

    /** Resumes paused playback. State transitions to [AudioState.Playing]. */
    fun resume()

    /** Stops playback and resets position. State transitions to [AudioState.Idle]. */
    fun stop()

    /**
     * Releases all native resources held by the player.
     *
     * After calling this method, the player instance must not be reused.
     * State transitions to [AudioState.Idle].
     */
    fun release()
}

package com.uncaan.mengaji.core.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface contract for Quran audio recitation playback engines.
 *
 * Exposes observable [audioState] and operations for playback lifecycle management.
 *
 * @see AudioState
 * @see AyahAudioPlayer
 */
interface AudioPlayer {
    /** Observable playback state emitted as a [StateFlow]. */
    val audioState: StateFlow<AudioState>

    /**
     * Begins playback from the given audio URL.
     *
     * @param url The HTTPS URL of the audio file to stream.
     */
    fun play(url: String)

    /** Pauses the current playback. */
    fun pause()

    /** Resumes paused playback. */
    fun resume()

    /** Stops playback and resets position to idle. */
    fun stop()

    /** Releases all native resources held by the player. */
    fun release()
}

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
 * @see AudioPlayer
 * @see AudioState
 */
expect class AyahAudioPlayer : AudioPlayer {
    /** Observable playback state emitted as a [StateFlow]. */
    override val audioState: StateFlow<AudioState>

    /**
     * Begins playback from the given audio URL.
     *
     * If a previous track is playing, it is replaced. The state transitions
     * through [AudioState.Buffering] → [AudioState.Playing].
     *
     * @param url The HTTPS URL of the audio file to stream.
     */
    override fun play(url: String)

    /** Pauses the current playback. State transitions to [AudioState.Paused]. */
    override fun pause()

    /** Resumes paused playback. State transitions to [AudioState.Playing]. */
    override fun resume()

    /** Stops playback and resets position. State transitions to [AudioState.Idle]. */
    override fun stop()

    /**
     * Releases all native resources held by the player.
     *
     * After calling this method, the player instance must not be reused.
     * State transitions to [AudioState.Idle].
     */
    override fun release()
}


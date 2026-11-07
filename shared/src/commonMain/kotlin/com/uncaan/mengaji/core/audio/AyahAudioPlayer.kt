package com.uncaan.mengaji.core.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface contract for Quran audio recitation playback engines.
 *
 * Exposes observable [audioState] and operations for continuous verse queue streaming,
 * transport navigation, playback rate control, and repeat mode configuration.
 *
 * @see AudioState
 * @see AudioTrack
 * @see AudioRepeatMode
 * @see AyahAudioPlayer
 */
interface AudioPlayer {
    /** Observable playback state emitted as a [StateFlow]. */
    val audioState: StateFlow<AudioState>

    /**
     * Begins playback from a single remote audio URL.
     *
     * Clears any active multi-verse queue and initiates playback for the specified stream.
     *
     * @param url The HTTPS URL of the audio stream.
     */
    fun play(url: String)

    /**
     * Initializes and begins continuous playback across a queue of verses.
     *
     * Seamlessly transitions from one verse to the next upon completion, updating track
     * metadata and respecting the configured [AudioRepeatMode].
     *
     * @param tracks The ordered list of [AudioTrack] instances representing verses in the queue.
     * @param startIndex The 0-based index of the initial track to begin playing. Defaults to 0.
     */
    fun playQueue(tracks: List<AudioTrack>, startIndex: Int = 0)

    /** Pauses the current playback. */
    fun pause()

    /** Resumes paused playback. */
    fun resume()

    /** Stops playback and resets state to idle. */
    fun stop()

    /**
     * Advances playback to the next verse in the current queue if available.
     */
    fun skipToNext()

    /**
     * Navigates playback to the preceding verse in the current queue if available.
     */
    fun skipToPrevious()

    /**
     * Configures the audio playback rate multiplier.
     *
     * @param speed The playback speed factor (e.g. `0.75f`, `1.0f`, `1.25f`, `1.5f`).
     */
    fun setPlaybackSpeed(speed: Float)

    /**
     * Configures the looping behavior for verses and queue completion.
     *
     * @param mode The desired [AudioRepeatMode] behavior.
     */
    fun setRepeatMode(mode: AudioRepeatMode)

    /** Releases all native resources held by the player. */
    fun release()
}

/**
 * Cross-platform audio player for Quran Ayah recitation.
 *
 * Provides a unified API for continuous verse queue streaming, single-track playback,
 * speed adjustment, and repeat mode cycling.
 * The current playback state is observable via [audioState] as a `StateFlow<AudioState>`.
 *
 * **Platform implementations:**
 * - **Android:** Uses AndroidX Media3 ExoPlayer for gapless multi-item streaming playback.
 * - **iOS:** Uses AVFoundation AVPlayer with `AVAudioSessionCategoryPlayback` and end-time notifications.
 *
 * Callers must invoke [release] when the player is no longer needed to free
 * native resources and prevent memory leaks.
 *
 * @see AudioPlayer
 * @see AudioState
 * @see AudioTrack
 * @see AudioRepeatMode
 */
expect class AyahAudioPlayer : AudioPlayer {
    /** Observable playback state emitted as a [StateFlow]. */
    override val audioState: StateFlow<AudioState>

    /**
     * Begins playback from a single remote audio URL.
     *
     * @param url The HTTPS URL of the audio stream.
     */
    override fun play(url: String)

    /**
     * Initializes and begins continuous playback across a queue of verses.
     *
     * @param tracks The ordered list of [AudioTrack] instances representing verses in the queue.
     * @param startIndex The 0-based index of the initial track to begin playing.
     */
    override fun playQueue(tracks: List<AudioTrack>, startIndex: Int)

    /** Pauses the current playback. */
    override fun pause()

    /** Resumes paused playback. */
    override fun resume()

    /** Stops playback and resets position to idle. */
    override fun stop()

    /** Advances playback to the next verse in the current queue if available. */
    override fun skipToNext()

    /** Navigates playback to the preceding verse in the current queue if available. */
    override fun skipToPrevious()

    /**
     * Configures the audio playback rate multiplier.
     *
     * @param speed The playback speed factor (e.g. `0.75f`, `1.0f`, `1.25f`, `1.5f`).
     */
    override fun setPlaybackSpeed(speed: Float)

    /**
     * Configures the looping behavior for verses and queue completion.
     *
     * @param mode The desired [AudioRepeatMode] behavior.
     */
    override fun setRepeatMode(mode: AudioRepeatMode)

    /**
     * Releases all native resources held by the player.
     *
     * After calling this method, the player instance must not be reused.
     */
    override fun release()
}

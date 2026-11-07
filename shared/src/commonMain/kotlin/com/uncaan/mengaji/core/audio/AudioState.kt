package com.uncaan.mengaji.core.audio

/**
 * Immutable state representation of the [AyahAudioPlayer] recitation engine.
 *
 * Emitted as a reactive stream via `StateFlow<AudioState>` to synchronize presentation
 * controls, active verse highlighting, and auto-scrolling during Quran playback.
 *
 * @property isPlaying Indicates whether audio is currently actively playing.
 * @property isBuffering Indicates whether audio stream data is currently being fetched or buffered.
 * @property currentUrl The HTTPS URL of the currently loaded or playing audio track, or `null` if idle.
 * @property currentSurahNumber The 1-based Surah index of the active verse, or `null` if idle.
 * @property currentAyahNumberInSurah The 1-based Ayah index within the Surah for the active verse, or `null` if idle.
 * @property currentGlobalAyahNumber The sequential 1-based Ayah index across the Quran for the active verse, or `null` if idle.
 * @property playbackSpeed The active audio playback rate multiplier (e.g. `0.75f`, `1.0f`, `1.25f`, `1.5f`).
 * @property repeatMode The active looping behavior for track or queue progression.
 * @property error A descriptive error message if playback encountered a fatal failure, or `null`.
 * @see AudioRepeatMode
 * @see AudioTrack
 * @see AudioPlayer
 */
data class AudioState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentUrl: String? = null,
    val currentSurahNumber: Int? = null,
    val currentAyahNumberInSurah: Int? = null,
    val currentGlobalAyahNumber: Int? = null,
    val playbackSpeed: Float = 1.0f,
    val repeatMode: AudioRepeatMode = AudioRepeatMode.NONE,
    val error: String? = null
) {
    /**
     * Determines whether audio playback is currently paused at a valid track position.
     */
    val isPaused: Boolean
        get() = !isPlaying && !isBuffering && currentUrl != null && error == null

    /**
     * Determines whether the audio engine is currently idle with no track loaded.
     */
    val isIdle: Boolean
        get() = !isPlaying && !isBuffering && currentUrl == null && error == null

    /**
     * Determines whether audio playback has an active session (playing, buffering, or paused).
     */
    val isActive: Boolean
        get() = isPlaying || isBuffering || isPaused

    companion object {
        /**
         * Represents the default idle audio state prior to loading any recitation tracks.
         */
        val IDLE = AudioState()
    }
}

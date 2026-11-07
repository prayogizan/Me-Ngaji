package com.uncaan.mengaji.core.audio

/**
 * Defines the looping behavior for Quran audio recitation playback.
 *
 * Configures whether playback halts after finishing the current track, continuously loops
 * the active verse, or advances continuously through the entire Surah queue.
 *
 * @see AudioPlayer.setRepeatMode
 * @see AudioState.repeatMode
 */
enum class AudioRepeatMode {
    /**
     * No looping. Playback halts after the final verse in the queue finishes.
     */
    NONE,

    /**
     * Loops the currently playing verse indefinitely until explicitly changed or stopped.
     */
    REPEAT_AYAH,

    /**
     * Loops the entire queue of verses in the active Surah upon reaching the final track.
     */
    REPEAT_SURAH
}


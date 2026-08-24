package com.uncaan.mengaji.core.audio

/**
 * Represents the discrete states of the [AyahAudioPlayer] playback lifecycle.
 *
 * Composables and ViewModels observe this type via `StateFlow<AudioState>` to
 * update play/pause/stop button states and loading indicators.
 *
 * @see AyahAudioPlayer.audioState
 */
sealed interface AudioState {

    /** No audio is loaded or playback has completed. The player is idle. */
    data object Idle : AudioState

    /** Audio data is being fetched from the network. Show a loading indicator. */
    data object Buffering : AudioState

    /** Audio is actively playing. Show pause/stop controls. */
    data object Playing : AudioState

    /** Audio playback is paused and can be resumed from the current position. */
    data object Paused : AudioState

    /**
     * An error occurred during playback.
     *
     * @property message A descriptive error message suitable for logging or user display.
     */
    data class Error(val message: String) : AudioState
}

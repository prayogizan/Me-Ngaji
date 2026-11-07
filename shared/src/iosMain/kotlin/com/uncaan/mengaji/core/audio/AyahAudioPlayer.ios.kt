package com.uncaan.mengaji.core.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSObjectProtocol

/**
 * iOS implementation of [AyahAudioPlayer] using AVFoundation AVPlayer.
 *
 * Configures [AVAudioSession] with `AVAudioSessionCategoryPlayback` on initialization
 * to enable uninterrupted recitation playback even when the screen is locked or app is in background.
 * Manages continuous queue progression and repeat modes using notification observers.
 *
 * @see AyahAudioPlayer
 * @see AudioState
 * @see AudioPlayer
 * @see AudioTrack
 * @see AudioRepeatMode
 */
@OptIn(ExperimentalForeignApi::class)
actual class AyahAudioPlayer : AudioPlayer {

    private val _audioState = MutableStateFlow(AudioState.IDLE)

    /**
     * Observable playback state emitted as a [StateFlow].
     */
    actual override val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private val player: AVPlayer = AVPlayer()
    private var queue: List<AudioTrack> = emptyList()
    private var currentIndex: Int = -1
    private var currentSpeed: Float = 1.0f
    private var repeatMode: AudioRepeatMode = AudioRepeatMode.NONE
    private var endObserverToken: NSObjectProtocol? = null

    init {
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, error = null)
            session.setActive(true, error = null)
        } catch (_: Exception) {
            // Best effort session activation
        }

        registerEndObserver()
    }

    private fun registerEndObserver() {
        endObserverToken = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                handleTrackCompletion()
            }
        )
    }

    private fun handleTrackCompletion() {
        when (repeatMode) {
            AudioRepeatMode.REPEAT_AYAH -> {
                if (currentIndex in queue.indices) {
                    playIndex(currentIndex)
                }
            }
            AudioRepeatMode.REPEAT_SURAH -> {
                if (queue.isNotEmpty()) {
                    val nextIndex = if (currentIndex + 1 < queue.size) currentIndex + 1 else 0
                    playIndex(nextIndex)
                }
            }
            AudioRepeatMode.NONE -> {
                if (currentIndex + 1 < queue.size) {
                    playIndex(currentIndex + 1)
                } else {
                    player.pause()
                    _audioState.update {
                        it.copy(
                            isPlaying = false,
                            isBuffering = false
                        )
                    }
                }
            }
        }
    }

    private fun playIndex(index: Int) {
        if (index !in queue.indices) return

        currentIndex = index
        val track = queue[index]
        val nsUrl = NSURL.URLWithString(track.url) ?: run {
            _audioState.update {
                it.copy(
                    isPlaying = false,
                    isBuffering = false,
                    error = "Invalid audio URL"
                )
            }
            return
        }

        _audioState.update {
            it.copy(
                isBuffering = true,
                isPlaying = false,
                currentUrl = track.url,
                currentSurahNumber = if (track.surahNumber > 0) track.surahNumber else null,
                currentAyahNumberInSurah = if (track.ayahNumberInSurah > 0) track.ayahNumberInSurah else null,
                currentGlobalAyahNumber = if (track.globalAyahNumber > 0) track.globalAyahNumber else null,
                error = null
            )
        }

        val item = AVPlayerItem(uRL = nsUrl)
        player.replaceCurrentItemWithPlayerItem(item)
        player.play()
        player.rate = currentSpeed
        _audioState.update {
            it.copy(
                isPlaying = true,
                isBuffering = false
            )
        }
    }

    /**
     * Begins streaming and playback from a single remote audio URL.
     *
     * @param url The HTTPS URL of the audio file to stream.
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

        queue = tracks
        val safeIndex = startIndex.coerceIn(0, tracks.lastIndex)
        playIndex(safeIndex)
    }

    /**
     * Pauses the current playback.
     */
    actual override fun pause() {
        player.pause()
        _audioState.update { it.copy(isPlaying = false) }
    }

    /**
     * Resumes paused playback.
     */
    actual override fun resume() {
        player.play()
        player.rate = currentSpeed
        _audioState.update { it.copy(isPlaying = true) }
    }

    /**
     * Stops playback and resets the current media item and queue.
     */
    actual override fun stop() {
        player.pause()
        player.replaceCurrentItemWithPlayerItem(null)
        queue = emptyList()
        currentIndex = -1
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
        if (queue.isEmpty()) return
        val nextIndex = currentIndex + 1
        if (nextIndex < queue.size) {
            playIndex(nextIndex)
        } else if (repeatMode == AudioRepeatMode.REPEAT_SURAH) {
            playIndex(0)
        }
    }

    /**
     * Navigates playback to the previous verse in the queue if available.
     */
    actual override fun skipToPrevious() {
        if (queue.isEmpty()) return
        val prevIndex = currentIndex - 1
        if (prevIndex >= 0) {
            playIndex(prevIndex)
        } else if (repeatMode == AudioRepeatMode.REPEAT_SURAH) {
            playIndex(queue.lastIndex)
        }
    }

    /**
     * Configures the audio playback rate multiplier.
     *
     * @param speed The playback speed factor (e.g. `0.75f`, `1.0f`, `1.25f`, `1.5f`).
     */
    actual override fun setPlaybackSpeed(speed: Float) {
        val clampedSpeed = speed.coerceIn(0.5f, 2.0f)
        currentSpeed = clampedSpeed
        if (_audioState.value.isPlaying) {
            player.rate = clampedSpeed
        }
        _audioState.update { it.copy(playbackSpeed = clampedSpeed) }
    }

    /**
     * Configures the looping behavior for verses and queue completion.
     *
     * @param mode The desired [AudioRepeatMode] behavior.
     */
    actual override fun setRepeatMode(mode: AudioRepeatMode) {
        repeatMode = mode
        _audioState.update { it.copy(repeatMode = mode) }
    }

    /**
     * Releases player resources and unregisters notification listeners.
     */
    actual override fun release() {
        endObserverToken?.let { token ->
            NSNotificationCenter.defaultCenter.removeObserver(token)
            endObserverToken = null
        }
        stop()
    }
}

package com.uncaan.mengaji.core.audio

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Concrete test-double audio player simulating continuous queue, speed scaling,
 * and repeat modes for multi-platform unit tests.
 */
private class TestContinuousAudioPlayer : AudioPlayer {
    private val tracks = mutableListOf<AudioTrack>()
    private var currentIndex: Int = -1
    private var _state = AudioState.IDLE

    override val audioState = kotlinx.coroutines.flow.MutableStateFlow(_state)

    override fun play(url: String) {
        val single = AudioTrack(url = url, surahNumber = 0, ayahNumberInSurah = 0, globalAyahNumber = 0)
        playQueue(listOf(single), 0)
    }

    override fun playQueue(tracks: List<AudioTrack>, startIndex: Int) {
        if (tracks.isEmpty()) return
        this.tracks.clear()
        this.tracks.addAll(tracks)
        val safeIndex = startIndex.coerceIn(0, tracks.lastIndex)
        currentIndex = safeIndex
        val current = tracks[safeIndex]
        _state = _state.copy(
            isPlaying = true,
            isBuffering = false,
            currentUrl = current.url,
            currentSurahNumber = if (current.surahNumber > 0) current.surahNumber else null,
            currentAyahNumberInSurah = if (current.ayahNumberInSurah > 0) current.ayahNumberInSurah else null,
            currentGlobalAyahNumber = if (current.globalAyahNumber > 0) current.globalAyahNumber else null,
            error = null
        )
        audioState.value = _state
    }

    override fun pause() {
        _state = _state.copy(isPlaying = false)
        audioState.value = _state
    }

    override fun resume() {
        _state = _state.copy(isPlaying = true)
        audioState.value = _state
    }

    override fun stop() {
        tracks.clear()
        currentIndex = -1
        _state = AudioState.IDLE.copy(
            playbackSpeed = _state.playbackSpeed,
            repeatMode = _state.repeatMode
        )
        audioState.value = _state
    }

    override fun skipToNext() {
        if (tracks.isEmpty()) return
        val next = currentIndex + 1
        if (next < tracks.size) {
            currentIndex = next
            val track = tracks[next]
            _state = _state.copy(
                isPlaying = true,
                currentUrl = track.url,
                currentSurahNumber = track.surahNumber,
                currentAyahNumberInSurah = track.ayahNumberInSurah,
                currentGlobalAyahNumber = track.globalAyahNumber
            )
            audioState.value = _state
        } else if (_state.repeatMode == AudioRepeatMode.REPEAT_SURAH) {
            currentIndex = 0
            val track = tracks[0]
            _state = _state.copy(
                isPlaying = true,
                currentUrl = track.url,
                currentSurahNumber = track.surahNumber,
                currentAyahNumberInSurah = track.ayahNumberInSurah,
                currentGlobalAyahNumber = track.globalAyahNumber
            )
            audioState.value = _state
        }
    }

    override fun skipToPrevious() {
        if (tracks.isEmpty()) return
        val prev = currentIndex - 1
        if (prev >= 0) {
            currentIndex = prev
            val track = tracks[prev]
            _state = _state.copy(
                isPlaying = true,
                currentUrl = track.url,
                currentSurahNumber = track.surahNumber,
                currentAyahNumberInSurah = track.ayahNumberInSurah,
                currentGlobalAyahNumber = track.globalAyahNumber
            )
            audioState.value = _state
        } else if (_state.repeatMode == AudioRepeatMode.REPEAT_SURAH) {
            currentIndex = tracks.lastIndex
            val track = tracks[tracks.lastIndex]
            _state = _state.copy(
                isPlaying = true,
                currentUrl = track.url,
                currentSurahNumber = track.surahNumber,
                currentAyahNumberInSurah = track.ayahNumberInSurah,
                currentGlobalAyahNumber = track.globalAyahNumber
            )
            audioState.value = _state
        }
    }

    override fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.5f, 2.0f)
        _state = _state.copy(playbackSpeed = clamped)
        audioState.value = _state
    }

    override fun setRepeatMode(mode: AudioRepeatMode) {
        _state = _state.copy(repeatMode = mode)
        audioState.value = _state
    }

    override fun release() {
        stop()
    }
}

/**
 * Unit test suite verifying continuous queue transitions, transport operations,
 * repeat modes, and playback speed adjustment across the audio engine abstraction.
 */
class ContinuousAudioEngineTest {

    private lateinit var player: AudioPlayer

    private val sampleQueue = listOf(
        AudioTrack("https://example.com/1.mp3", surahNumber = 1, ayahNumberInSurah = 1, globalAyahNumber = 1),
        AudioTrack("https://example.com/2.mp3", surahNumber = 1, ayahNumberInSurah = 2, globalAyahNumber = 2),
        AudioTrack("https://example.com/3.mp3", surahNumber = 1, ayahNumberInSurah = 3, globalAyahNumber = 3)
    )

    @BeforeTest
    fun setUp() {
        player = TestContinuousAudioPlayer()
    }

    @Test
    fun playQueue_initiatesPlaybackWithStartIndex() {
        player.playQueue(sampleQueue, startIndex = 1)

        val state = player.audioState.value
        assertTrue(state.isPlaying)
        assertEquals("https://example.com/2.mp3", state.currentUrl)
        assertEquals(1, state.currentSurahNumber)
        assertEquals(2, state.currentAyahNumberInSurah)
        assertEquals(2, state.currentGlobalAyahNumber)
    }

    @Test
    fun playQueue_clampsInvalidStartIndex() {
        player.playQueue(sampleQueue, startIndex = 99)

        val state = player.audioState.value
        assertEquals("https://example.com/3.mp3", state.currentUrl)
        assertEquals(3, state.currentAyahNumberInSurah)
    }

    @Test
    fun skipToNext_advancesTrackSequentially() {
        player.playQueue(sampleQueue, startIndex = 0)
        assertEquals(1, player.audioState.value.currentAyahNumberInSurah)

        player.skipToNext()
        assertEquals(2, player.audioState.value.currentAyahNumberInSurah)

        player.skipToNext()
        assertEquals(3, player.audioState.value.currentAyahNumberInSurah)
    }

    @Test
    fun skipToPrevious_movesBackwardSequentially() {
        player.playQueue(sampleQueue, startIndex = 2)
        assertEquals(3, player.audioState.value.currentAyahNumberInSurah)

        player.skipToPrevious()
        assertEquals(2, player.audioState.value.currentAyahNumberInSurah)

        player.skipToPrevious()
        assertEquals(1, player.audioState.value.currentAyahNumberInSurah)
    }

    @Test
    fun repeatSurah_loopsQueueOnNextAndPrevious() {
        player.setRepeatMode(AudioRepeatMode.REPEAT_SURAH)
        player.playQueue(sampleQueue, startIndex = 2)

        player.skipToNext()
        assertEquals(1, player.audioState.value.currentAyahNumberInSurah)

        player.skipToPrevious()
        assertEquals(3, player.audioState.value.currentAyahNumberInSurah)
    }

    @Test
    fun setPlaybackSpeed_updatesActiveMultiplier() {
        player.setPlaybackSpeed(1.5f)
        assertEquals(1.5f, player.audioState.value.playbackSpeed)

        player.setPlaybackSpeed(0.75f)
        assertEquals(0.75f, player.audioState.value.playbackSpeed)
    }

    @Test
    fun singlePlay_preservesBackwardCompatibility() {
        player.play("https://example.com/single.mp3")

        val state = player.audioState.value
        assertTrue(state.isPlaying)
        assertEquals("https://example.com/single.mp3", state.currentUrl)
        assertNull(state.currentSurahNumber)
        assertNull(state.currentAyahNumberInSurah)
    }

    @Test
    fun pauseAndResume_togglesPlaybackState() {
        player.playQueue(sampleQueue, startIndex = 0)
        assertTrue(player.audioState.value.isPlaying)

        player.pause()
        assertFalse(player.audioState.value.isPlaying)
        assertTrue(player.audioState.value.isPaused)

        player.resume()
        assertTrue(player.audioState.value.isPlaying)
    }

    @Test
    fun stop_clearsActiveQueueAndResetsState() {
        player.playQueue(sampleQueue, startIndex = 1)
        player.stop()

        val state = player.audioState.value
        assertFalse(state.isPlaying)
        assertNull(state.currentUrl)
        assertTrue(state.isIdle)
    }
}


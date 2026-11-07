package com.uncaan.mengaji.core.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit test suite verifying the behavior, invariants, and state flags of [AudioState],
 * [AudioTrack], and [AudioRepeatMode].
 */
class AudioStateTest {

    @Test
    fun idleState_hasDefaultValuesAndCorrectFlags() {
        val state = AudioState.IDLE

        assertFalse(state.isPlaying)
        assertFalse(state.isBuffering)
        assertNull(state.currentUrl)
        assertNull(state.currentSurahNumber)
        assertNull(state.currentAyahNumberInSurah)
        assertNull(state.currentGlobalAyahNumber)
        assertEquals(1.0f, state.playbackSpeed)
        assertEquals(AudioRepeatMode.NONE, state.repeatMode)
        assertNull(state.error)

        assertTrue(state.isIdle)
        assertFalse(state.isPaused)
        assertFalse(state.isActive)
    }

    @Test
    fun bufferingState_flagsActiveAndNotIdle() {
        val state = AudioState(
            isBuffering = true,
            currentUrl = "https://example.com/audio.mp3",
            currentSurahNumber = 1,
            currentAyahNumberInSurah = 1
        )

        assertTrue(state.isBuffering)
        assertFalse(state.isPlaying)
        assertTrue(state.isActive)
        assertFalse(state.isIdle)
        assertFalse(state.isPaused)
    }

    @Test
    fun playingState_flagsActiveAndPlaying() {
        val state = AudioState(
            isPlaying = true,
            currentUrl = "https://example.com/audio.mp3",
            currentSurahNumber = 1,
            currentAyahNumberInSurah = 2,
            currentGlobalAyahNumber = 2
        )

        assertTrue(state.isPlaying)
        assertFalse(state.isBuffering)
        assertTrue(state.isActive)
        assertFalse(state.isIdle)
        assertFalse(state.isPaused)
    }

    @Test
    fun pausedState_flagsActiveAndPaused() {
        val state = AudioState(
            isPlaying = false,
            isBuffering = false,
            currentUrl = "https://example.com/audio.mp3",
            currentSurahNumber = 1,
            currentAyahNumberInSurah = 3
        )

        assertFalse(state.isPlaying)
        assertFalse(state.isBuffering)
        assertTrue(state.isPaused)
        assertTrue(state.isActive)
        assertFalse(state.isIdle)
    }

    @Test
    fun errorState_holdsErrorMessage() {
        val errorMessage = "Playback network stream timeout"
        val state = AudioState(
            error = errorMessage,
            currentUrl = "https://example.com/audio.mp3"
        )

        assertEquals(errorMessage, state.error)
        assertFalse(state.isPlaying)
        assertFalse(state.isBuffering)
        assertFalse(state.isPaused)
    }

    @Test
    fun playbackSpeed_canBeConfigured() {
        val speedState = AudioState(playbackSpeed = 1.25f)
        assertEquals(1.25f, speedState.playbackSpeed)
    }

    @Test
    fun repeatModes_allEnumValuesAvailable() {
        val modes = AudioRepeatMode.entries
        assertEquals(3, modes.size)
        assertTrue(modes.contains(AudioRepeatMode.NONE))
        assertTrue(modes.contains(AudioRepeatMode.REPEAT_AYAH))
        assertTrue(modes.contains(AudioRepeatMode.REPEAT_SURAH))
    }

    @Test
    fun audioTrack_holdsCorrectMetadata() {
        val track = AudioTrack(
            url = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
            surahNumber = 1,
            ayahNumberInSurah = 1,
            globalAyahNumber = 1
        )

        assertEquals("https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3", track.url)
        assertEquals(1, track.surahNumber)
        assertEquals(1, track.ayahNumberInSurah)
        assertEquals(1, track.globalAyahNumber)
    }
}

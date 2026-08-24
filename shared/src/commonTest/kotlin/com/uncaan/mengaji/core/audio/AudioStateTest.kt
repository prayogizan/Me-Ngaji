package com.uncaan.mengaji.core.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AudioStateTest {

    @Test
    fun idleState_isDistinctAndMatchesType() {
        val state: AudioState = AudioState.Idle
        assertTrue(state is AudioState.Idle)
        assertEquals(AudioState.Idle, state)
    }

    @Test
    fun bufferingState_isDistinctAndMatchesType() {
        val state: AudioState = AudioState.Buffering
        assertTrue(state is AudioState.Buffering)
        assertEquals(AudioState.Buffering, state)
    }

    @Test
    fun playingState_isDistinctAndMatchesType() {
        val state: AudioState = AudioState.Playing
        assertTrue(state is AudioState.Playing)
        assertEquals(AudioState.Playing, state)
    }

    @Test
    fun pausedState_isDistinctAndMatchesType() {
        val state: AudioState = AudioState.Paused
        assertTrue(state is AudioState.Paused)
        assertEquals(AudioState.Paused, state)
    }

    @Test
    fun errorState_holdsMessageAndMatchesEquality() {
        val errorMessage = "Failed to load audio stream"
        val state: AudioState = AudioState.Error(errorMessage)

        assertTrue(state is AudioState.Error)
        assertEquals(errorMessage, state.message)
        assertEquals(AudioState.Error(errorMessage), state)
        assertNotEquals(AudioState.Error("Different error"), state)
    }

    @Test
    fun whenExpression_exhaustivelyEvaluatesAllStates() {
        val states: List<AudioState> = listOf(
            AudioState.Idle,
            AudioState.Buffering,
            AudioState.Playing,
            AudioState.Paused,
            AudioState.Error("Test error")
        )

        val labels = states.map { state ->
            when (state) {
                is AudioState.Idle -> "idle"
                is AudioState.Buffering -> "buffering"
                is AudioState.Playing -> "playing"
                is AudioState.Paused -> "paused"
                is AudioState.Error -> "error: ${state.message}"
            }
        }

        assertEquals(
            listOf("idle", "buffering", "playing", "paused", "error: Test error"),
            labels
        )
    }
}

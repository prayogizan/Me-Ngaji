package com.uncaan.mengaji.feature.shalat.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShalatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ShalatViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ShalatViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasCorrectDefaults() {
        val state = viewModel.uiState.value

        assertFalse(state.isSubscribed)
        assertNull(state.userMessage)
        assertEquals(3, state.roadmapItems.size)

        assertEquals("prayer_times", state.roadmapItems[0].id)
        assertEquals("Location-Based Prayer Times", state.roadmapItems[0].title)
        assertEquals("5 Daily Prayers", state.roadmapItems[0].highlightBadge)

        assertEquals("qibla_compass", state.roadmapItems[1].id)
        assertEquals("Qibla Direction Compass", state.roadmapItems[1].title)
        assertEquals("Precision Sensor", state.roadmapItems[1].highlightBadge)

        assertEquals("adhan_notifications", state.roadmapItems[2].id)
        assertEquals("Customizable Adhan & Reminders", state.roadmapItems[2].title)
        assertEquals("Audio Alerts", state.roadmapItems[2].highlightBadge)
    }

    @Test
    fun notifyMeClicked_setsSubscribedAndSetsUserMessage() = runTest {
        viewModel.onAction(ShalatUiAction.NotifyMeClicked)

        val state = viewModel.uiState.value
        assertTrue(state.isSubscribed)
        assertEquals("You will be notified when prayer times launch!", state.userMessage)
    }

    @Test
    fun userMessageShown_clearsUserMessage() = runTest {
        viewModel.onAction(ShalatUiAction.NotifyMeClicked)
        assertEquals("You will be notified when prayer times launch!", viewModel.uiState.value.userMessage)

        viewModel.onAction(ShalatUiAction.UserMessageShown)
        val state = viewModel.uiState.value
        assertNull(state.userMessage)
        assertTrue(state.isSubscribed)
    }

    @Test
    fun notifyMeClicked_repeatedly_maintainsSubscribedState() = runTest {
        viewModel.onAction(ShalatUiAction.NotifyMeClicked)
        viewModel.onAction(ShalatUiAction.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
        assertTrue(viewModel.uiState.value.isSubscribed)

        viewModel.onAction(ShalatUiAction.NotifyMeClicked)
        assertTrue(viewModel.uiState.value.isSubscribed)
        assertEquals("You will be notified when prayer times launch!", viewModel.uiState.value.userMessage)
    }
}

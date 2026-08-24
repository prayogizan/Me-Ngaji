package com.uncaan.mengaji.feature.shalat.presentation

import androidx.lifecycle.ViewModel
import com.uncaan.mengaji.feature.shalat.domain.model.ShalatRoadmapItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mengaji.shared.generated.resources.Res
import mengaji.shared.generated.resources.ic_adhan_bell
import mengaji.shared.generated.resources.ic_compass
import mengaji.shared.generated.resources.ic_prayer_time

/**
 * ViewModel for the Shalat Schedule feature tab, orchestrating the "Coming Soon" roadmap state.
 *
 * Exposes an immutable [uiState] as `StateFlow<ShalatUiState>` following Unidirectional
 * Data Flow (UDF). Handles user interactions via [onAction].
 *
 * Injected via Koin using `viewModelOf(::ShalatViewModel)`.
 *
 * @see ShalatUiState
 * @see ShalatUiAction
 */
class ShalatViewModel : ViewModel() {

    private val defaultRoadmapItems = listOf(
        ShalatRoadmapItem(
            id = "prayer_times",
            title = "Location-Based Prayer Times",
            description = "Precise daily timings for Fajr, Dhuhr, Asr, Maghrib, and Isha calculated automatically using your device GPS location and preferred calculation method.",
            icon = Res.drawable.ic_prayer_time,
            highlightBadge = "5 Daily Prayers"
        ),
        ShalatRoadmapItem(
            id = "qibla_compass",
            title = "Qibla Direction Compass",
            description = "Real-time interactive directional compass pointing straight to the Holy Kaaba in Mecca with magnetic declination correction.",
            icon = Res.drawable.ic_compass,
            highlightBadge = "Precision Sensor"
        ),
        ShalatRoadmapItem(
            id = "adhan_notifications",
            title = "Customizable Adhan & Reminders",
            description = "Timely audio Adhan alerts with your choice of renowned reciters, pre-prayer preparation chimes, and customizable notification schedules.",
            icon = Res.drawable.ic_adhan_bell,
            highlightBadge = "Audio Alerts"
        )
    )

    private val _uiState = MutableStateFlow(
        ShalatUiState(
            isSubscribed = false,
            roadmapItems = defaultRoadmapItems,
            userMessage = null
        )
    )

    /**
     * Observable stream of the current [ShalatUiState].
     */
    val uiState: StateFlow<ShalatUiState> = _uiState.asStateFlow()

    /**
     * Dispatches user-initiated actions to update the UI state.
     *
     * @param action The [ShalatUiAction] to process.
     */
    fun onAction(action: ShalatUiAction) {
        when (action) {
            ShalatUiAction.NotifyMeClicked -> handleNotifyMe()
            ShalatUiAction.UserMessageShown -> clearUserMessage()
        }
    }

    private fun handleNotifyMe() {
        _uiState.update { currentState ->
            currentState.copy(
                isSubscribed = true,
                userMessage = "You will be notified when prayer times launch!"
            )
        }
    }

    private fun clearUserMessage() {
        _uiState.update { currentState ->
            currentState.copy(userMessage = null)
        }
    }
}

package com.uncaan.mengaji.widget.config

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel orchestrating user selections and UI state for the widget configuration activity.
 *
 * Implements Unidirectional Data Flow (UDF), managing [WidgetConfigUiState] and processing
 * [WidgetConfigUiAction] dispatches.
 *
 * @param appWidgetId The unique Android widget instance identifier received from the launch intent.
 * @param initialConfig The existing or default [WidgetConfig] for this instance.
 * @see WidgetConfigUiState
 * @see WidgetConfigUiAction
 */
class WidgetConfigViewModel(
    val appWidgetId: Int,
    initialConfig: WidgetConfig = WidgetConfig()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WidgetConfigUiState(
            appWidgetId = appWidgetId,
            selectedLanguage = initialConfig.language,
            selectedTheme = initialConfig.theme
        )
    )

    /**
     * Immutable state flow observed by the configuration screen composables.
     */
    val uiState: StateFlow<WidgetConfigUiState> = _uiState.asStateFlow()

    /**
     * Central action dispatcher for user interactions on the configuration screen.
     *
     * @param action The user-initiated [WidgetConfigUiAction] event.
     */
    fun onAction(action: WidgetConfigUiAction) {
        when (action) {
            is WidgetConfigUiAction.SelectLanguage -> {
                _uiState.update { it.copy(selectedLanguage = action.language) }
            }
            is WidgetConfigUiAction.SelectTheme -> {
                _uiState.update { it.copy(selectedTheme = action.theme) }
            }
        }
    }

    /**
     * Generates a consolidated [WidgetConfig] reflecting current UI state selections.
     *
     * @return The current [WidgetConfig] to be persisted.
     */
    fun getCurrentConfig(): WidgetConfig {
        val state = _uiState.value
        return WidgetConfig(
            language = state.selectedLanguage,
            theme = state.selectedTheme
        )
    }
}


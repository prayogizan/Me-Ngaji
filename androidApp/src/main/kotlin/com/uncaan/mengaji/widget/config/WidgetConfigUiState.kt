package com.uncaan.mengaji.widget.config

/**
 * Immutable UI state for the widget configuration screen.
 *
 * Exposes the currently selected translation language, visual theme, and persistence status.
 *
 * @property appWidgetId The target Android widget instance ID being configured.
 * @property selectedLanguage Current translation language selection.
 * @property selectedTheme Current visual color theme selection.
 * @property isSaving Whether configuration persistence is actively in progress.
 */
data class WidgetConfigUiState(
    val appWidgetId: Int,
    val selectedLanguage: WidgetLanguage = WidgetLanguage.INDONESIAN,
    val selectedTheme: WidgetTheme = WidgetTheme.AUTO,
    val isSaving: Boolean = false
)

/**
 * User-initiated actions on the widget configuration screen following Unidirectional Data Flow (UDF).
 */
sealed interface WidgetConfigUiAction {

    /**
     * The user selected a translation language option.
     *
     * @property language The chosen [WidgetLanguage].
     */
    data class SelectLanguage(val language: WidgetLanguage) : WidgetConfigUiAction

    /**
     * The user selected a visual color theme option.
     *
     * @property theme The chosen [WidgetTheme].
     */
    data class SelectTheme(val theme: WidgetTheme) : WidgetConfigUiAction
}


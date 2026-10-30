package com.uncaan.mengaji.widget.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Unit tests verifying [WidgetConfigViewModel] state mutations and action dispatches.
 */
class WidgetConfigViewModelTest {

    @Test
    fun initialState_defaultsToIndonesianAndAutoTheme() {
        val viewModel = WidgetConfigViewModel(appWidgetId = 42)

        val state = viewModel.uiState.value
        assertEquals(42, state.appWidgetId)
        assertEquals(WidgetLanguage.INDONESIAN, state.selectedLanguage)
        assertEquals(WidgetTheme.AUTO, state.selectedTheme)
        assertFalse(state.isSaving)
    }

    @Test
    fun initialState_initialConfigSupplied_populatesSelectedValues() {
        val customConfig = WidgetConfig(
            language = WidgetLanguage.ENGLISH,
            theme = WidgetTheme.DARK
        )
        val viewModel = WidgetConfigViewModel(
            appWidgetId = 101,
            initialConfig = customConfig
        )

        val state = viewModel.uiState.value
        assertEquals(101, state.appWidgetId)
        assertEquals(WidgetLanguage.ENGLISH, state.selectedLanguage)
        assertEquals(WidgetTheme.DARK, state.selectedTheme)
    }

    @Test
    fun onAction_selectLanguage_updatesSelectedLanguage() {
        val viewModel = WidgetConfigViewModel(appWidgetId = 42)

        viewModel.onAction(WidgetConfigUiAction.SelectLanguage(WidgetLanguage.ENGLISH))

        assertEquals(WidgetLanguage.ENGLISH, viewModel.uiState.value.selectedLanguage)
    }

    @Test
    fun onAction_selectTheme_updatesSelectedTheme() {
        val viewModel = WidgetConfigViewModel(appWidgetId = 42)

        viewModel.onAction(WidgetConfigUiAction.SelectTheme(WidgetTheme.LIGHT))

        assertEquals(WidgetTheme.LIGHT, viewModel.uiState.value.selectedTheme)
    }

    @Test
    fun getCurrentConfig_returnsCurrentStateSelections() {
        val viewModel = WidgetConfigViewModel(appWidgetId = 42)

        viewModel.onAction(WidgetConfigUiAction.SelectLanguage(WidgetLanguage.ENGLISH))
        viewModel.onAction(WidgetConfigUiAction.SelectTheme(WidgetTheme.DARK))

        val config = viewModel.getCurrentConfig()
        assertEquals(WidgetLanguage.ENGLISH, config.language)
        assertEquals(WidgetTheme.DARK, config.theme)
    }
}


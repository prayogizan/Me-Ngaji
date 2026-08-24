package com.uncaan.mengaji.feature.quran.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uncaan.mengaji.feature.quran.presentation.component.AyahCard
import com.uncaan.mengaji.feature.quran.presentation.component.EditionSelectorRow
import com.uncaan.mengaji.feature.quran.presentation.component.QuranEmptyState
import com.uncaan.mengaji.feature.quran.presentation.component.QuranErrorState
import com.uncaan.mengaji.feature.quran.presentation.component.QuranLoadingSkeleton
import com.uncaan.mengaji.feature.quran.presentation.component.QuranSearchBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AlQuranScreen(
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTranslation by viewModel.selectedTranslation.collectAsStateWithLifecycle()
    val selectedRecitation by viewModel.selectedRecitation.collectAsStateWithLifecycle()

    AlQuranContent(
        uiState = uiState,
        searchQuery = searchQuery,
        selectedTranslation = selectedTranslation,
        selectedRecitation = selectedRecitation,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun AlQuranContent(
    uiState: QuranUiState,
    searchQuery: String,
    selectedTranslation: String,
    selectedRecitation: String,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Search Input
        QuranSearchBar(
            query = searchQuery,
            onQueryChange = { onAction(QuranUiAction.UpdateSearchQuery(it)) },
            onSearch = { onAction(QuranUiAction.Search(it)) },
            onClear = { onAction(QuranUiAction.ClearSearchQuery) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Edition Presets Selector
        EditionSelectorRow(
            selectedTranslation = selectedTranslation,
            selectedRecitation = selectedRecitation,
            onSelectTranslation = { onAction(QuranUiAction.SelectTranslationEdition(it)) },
            onSelectRecitation = { onAction(QuranUiAction.SelectAudioEdition(it)) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic State Content
        when (uiState) {
            is QuranUiState.Initial -> {
                QuranEmptyState(
                    onSelectSuggestion = { suggestionRef ->
                        onAction(QuranUiAction.Search(suggestionRef))
                    }
                )
            }
            is QuranUiState.Loading -> {
                QuranLoadingSkeleton()
            }
            is QuranUiState.Success -> {
                AyahCard(
                    ayah = uiState.ayah,
                    translationEdition = uiState.selectedTranslationEdition,
                    recitationEdition = uiState.selectedAudioEdition
                )
            }
            is QuranUiState.Error -> {
                QuranErrorState(
                    message = uiState.message,
                    isNotFoundError = uiState.isNotFoundError,
                    isNetworkError = uiState.isNetworkError,
                    onRetry = { onAction(QuranUiAction.Retry) }
                )
            }
        }
    }
}

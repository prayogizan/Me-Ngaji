package com.uncaan.mengaji.feature.quran.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.feature.quran.presentation.component.AyahCard
import com.uncaan.mengaji.feature.quran.presentation.component.EditionSelectorRow
import com.uncaan.mengaji.feature.quran.presentation.component.QuranEmptyState
import com.uncaan.mengaji.feature.quran.presentation.component.QuranErrorState
import com.uncaan.mengaji.feature.quran.presentation.component.QuranLoadingSkeleton
import com.uncaan.mengaji.feature.quran.presentation.component.QuranSearchBar
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stateful root screen composable for the Al-Quran feature.
 *
 * Injects [QuranViewModel] via Koin, collects UI state, search query, edition presets,
 * and [AudioState] flows using [collectAsStateWithLifecycle], manages playback auto-stop
 * lifecycle with [DisposableEffect], presents error snackbars, and delegates rendering to [AlQuranContent].
 *
 * @param modifier The layout modifier for the root container.
 * @param viewModel The ViewModel instance providing UI state and action handling.
 * @see AlQuranContent
 * @see QuranViewModel
 * @see AudioState
 */
@Composable
fun AlQuranScreen(
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTranslation by viewModel.selectedTranslation.collectAsStateWithLifecycle()
    val selectedRecitation by viewModel.selectedRecitation.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-stop audio playback when screen leaves composition (e.g. tab switch)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onAction(QuranUiAction.StopAudio)
        }
    }

    // Display error snackbar when audio playback fails
    LaunchedEffect(audioState) {
        if (audioState is AudioState.Error) {
            val errorMessage = (audioState as AudioState.Error).message
            snackbarHostState.showSnackbar(
                message = errorMessage.ifBlank { "Failed to play recitation audio" }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        AlQuranContent(
            uiState = uiState,
            searchQuery = searchQuery,
            selectedTranslation = selectedTranslation,
            selectedRecitation = selectedRecitation,
            audioState = audioState,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Stateless content composable for the Al-Quran screen.
 *
 * Renders the search bar, edition selectors, dynamic state content
 * ([QuranEmptyState], [QuranLoadingSkeleton], [AyahCard], or [QuranErrorState]),
 * and wires audio recitation actions to the active [AyahCard].
 *
 * @param uiState Current immutable UI state.
 * @param searchQuery Current search query text.
 * @param selectedTranslation Currently selected translation edition identifier.
 * @param selectedRecitation Currently selected audio recitation edition identifier.
 * @param audioState Current audio player playback state.
 * @param onAction Action callback dispatched to the ViewModel.
 * @param modifier The layout modifier for the scrollable container.
 * @see QuranUiState
 * @see QuranUiAction
 * @see AudioState
 */
@Composable
fun AlQuranContent(
    uiState: QuranUiState,
    searchQuery: String,
    selectedTranslation: String,
    selectedRecitation: String,
    audioState: AudioState,
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
                    recitationEdition = uiState.selectedAudioEdition,
                    audioState = audioState,
                    onPlay = { url -> onAction(QuranUiAction.PlayAudio(url)) },
                    onPause = { onAction(QuranUiAction.PauseAudio) },
                    onResume = { onAction(QuranUiAction.ResumeAudio) },
                    onStop = { onAction(QuranUiAction.StopAudio) }
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


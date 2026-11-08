package com.uncaan.mengaji.feature.quran.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.core.ui.KeepScreenOn
import com.uncaan.mengaji.feature.quran.presentation.component.AyahCard
import com.uncaan.mengaji.feature.quran.presentation.component.EditionSelectorRow
import com.uncaan.mengaji.feature.quran.presentation.component.QuranErrorState
import com.uncaan.mengaji.feature.quran.presentation.component.QuranLoadingSkeleton
import com.uncaan.mengaji.feature.quran.presentation.component.QuranSearchBar
import com.uncaan.mengaji.feature.quran.presentation.component.SurahCatalogPane
import com.uncaan.mengaji.theme.PrimaryGreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stateful root screen composable for the Al-Quran feature.
 *
 * Injects [QuranViewModel] via Koin, collects UI state, catalog state, search query, edition presets,
 * and [AudioState] flows using [collectAsStateWithLifecycle], manages playback auto-stop
 * lifecycle with [DisposableEffect], presents error snackbars, auto-triggers search when
 * initialized with valid [initialSurahNumber] and [initialAyahNumber] parameters, and
 * delegates rendering to [AlQuranContent].
 *
 * @param initialSurahNumber Optional 1-indexed Surah number passed from deep-link navigation.
 * @param initialAyahNumber Optional 1-indexed Ayah number passed from deep-link navigation.
 * @param modifier The layout modifier for the root container.
 * @param viewModel The ViewModel instance providing UI state and action handling.
 * @see AlQuranContent
 * @see QuranViewModel
 * @see AudioState
 */
@Composable
fun AlQuranScreen(
    initialSurahNumber: Int? = null,
    initialAyahNumber: Int? = null,
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val catalogState by viewModel.catalogState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTranslation by viewModel.selectedTranslation.collectAsStateWithLifecycle()
    val selectedRecitation by viewModel.selectedRecitation.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-search if deep-link parameters are present and positive
    LaunchedEffect(initialSurahNumber, initialAyahNumber) {
        if (initialSurahNumber != null && initialAyahNumber != null &&
            initialSurahNumber > 0 && initialAyahNumber > 0
        ) {
            viewModel.searchAyah("$initialSurahNumber:$initialAyahNumber")
        }
    }

    // Auto-stop audio playback when screen leaves composition (e.g. tab switch)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onAction(QuranUiAction.StopAudio)
        }
    }

    // Keep screen awake while recitation audio is actively playing
    KeepScreenOn(enabled = audioState.isPlaying)

    // Display error snackbar when audio playback fails
    LaunchedEffect(audioState.error) {
        val errorMessage = audioState.error
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = errorMessage
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        AlQuranContent(
            uiState = uiState,
            catalogState = catalogState,
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
 * Displays the comprehensive [SurahCatalogPane] when no specific Ayah is active,
 * or switches to verse inspection ([AyahCard], [QuranLoadingSkeleton], [QuranErrorState])
 * with a quick shortcut to navigate back to the catalog.
 *
 * @param uiState Current immutable UI state for verse inspection.
 * @param catalogState Current catalog exploration state for Surahs, Juzs, and Bookmarks.
 * @param searchQuery Current search query text.
 * @param selectedTranslation Currently selected translation edition identifier.
 * @param selectedRecitation Currently selected audio recitation edition identifier.
 * @param audioState Current audio player playback state.
 * @param onAction Action callback dispatched to the ViewModel.
 * @param modifier The layout modifier for the container.
 * @see QuranUiState
 * @see QuranCatalogState
 * @see QuranUiAction
 * @see AudioState
 */
@Composable
fun AlQuranContent(
    uiState: QuranUiState,
    catalogState: QuranCatalogState,
    searchQuery: String,
    selectedTranslation: String,
    selectedRecitation: String,
    audioState: AudioState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState is QuranUiState.Initial) {
        // Primary Screen: Surah / Juz / Bookmarks Catalog Pane
        SurahCatalogPane(
            catalogState = catalogState,
            onAction = onAction,
            modifier = modifier
        )
    } else {
        // Verse Inspection View
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Return to Catalog Header
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .clickable { onAction(QuranUiAction.BackToCatalog) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "← Back to Catalog",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                is QuranUiState.Initial -> Unit
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
}

@Preview
@Composable
private fun AlQuranContentPreview() {
    com.uncaan.mengaji.theme.MeNgajiTheme {
        AlQuranContent(
            uiState = QuranUiState.Initial,
            catalogState = QuranCatalogState(),
            searchQuery = "",
            selectedTranslation = "en.sahih",
            selectedRecitation = "ar.alafasy",
            audioState = AudioState.IDLE,
            onAction = {}
        )
    }
}


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
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.presentation.component.AyahActionBottomSheet
import com.uncaan.mengaji.feature.quran.presentation.component.AyahCard
import com.uncaan.mengaji.feature.quran.presentation.component.EditionSelectorRow
import com.uncaan.mengaji.feature.quran.presentation.component.MushafPageReader
import com.uncaan.mengaji.feature.quran.presentation.component.QuickJumpBottomSheet
import com.uncaan.mengaji.feature.quran.presentation.component.QuranErrorState
import com.uncaan.mengaji.feature.quran.presentation.component.QuranLoadingSkeleton
import com.uncaan.mengaji.feature.quran.presentation.component.QuranSearchBar
import com.uncaan.mengaji.feature.quran.presentation.component.ReaderTopBar
import com.uncaan.mengaji.feature.quran.presentation.component.RecitationAudioDock
import com.uncaan.mengaji.feature.quran.presentation.component.SurahCatalogPane
import com.uncaan.mengaji.feature.quran.presentation.component.VerseListReader
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
    val readerState by viewModel.readerState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTranslation by viewModel.selectedTranslation.collectAsStateWithLifecycle()
    val selectedRecitation by viewModel.selectedRecitation.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-search and open reader if deep-link parameters are present and positive
    LaunchedEffect(initialSurahNumber, initialAyahNumber) {
        if (initialSurahNumber != null && initialAyahNumber != null &&
            initialSurahNumber > 0 && initialAyahNumber > 0
        ) {
            viewModel.onAction(QuranUiAction.OpenReader(initialSurahNumber, initialAyahNumber))
        }
    }

    // Auto-stop audio playback when screen leaves composition (e.g. tab switch)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onAction(QuranUiAction.StopAudio)
        }
    }

    // Keep screen awake while reader is active or recitation audio is actively playing
    KeepScreenOn(enabled = readerState.isReaderActive || audioState.isPlaying)

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
            readerState = readerState,
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
 * Renders the Dual-Mode Recitation Reader ([ReaderTopBar], [VerseListReader], [MushafPageReader],
 * [RecitationAudioDock], [AyahActionBottomSheet]) when active, or displays the comprehensive
 * [SurahCatalogPane] and single-verse exploration view.
 *
 * @param uiState Current immutable UI state for verse inspection.
 * @param catalogState Current catalog exploration state for Surahs, Juzs, and Bookmarks.
 * @param readerState Current dual-mode recitation reader state.
 * @param searchQuery Current search query text.
 * @param selectedTranslation Currently selected translation edition identifier.
 * @param selectedRecitation Currently selected audio recitation edition identifier.
 * @param audioState Current audio player playback state.
 * @param onAction Action callback dispatched to the ViewModel.
 * @param modifier The layout modifier for the container.
 * @see QuranUiState
 * @see QuranCatalogState
 * @see QuranReaderState
 * @see QuranUiAction
 * @see AudioState
 */
@Composable
fun AlQuranContent(
    uiState: QuranUiState,
    catalogState: QuranCatalogState,
    readerState: QuranReaderState,
    searchQuery: String,
    selectedTranslation: String,
    selectedRecitation: String,
    audioState: AudioState,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (readerState.isReaderActive) {
        // Dual-Mode Quran Recitation Reader View (CMP-605)
        Box(modifier = modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Navigation & Mode Switcher Bar
                ReaderTopBar(
                    readerState = readerState,
                    onAction = onAction
                )

                // Reader Canvas Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (readerState.readerMode) {
                        ReaderMode.VERSE_LIST -> {
                            if (readerState.isReaderLoading && readerState.surahDetail == null) {
                                QuranLoadingSkeleton(modifier = Modifier.padding(16.dp))
                            } else if (readerState.surahDetail != null) {
                                VerseListReader(
                                    readerState = readerState,
                                    audioState = audioState,
                                    onAction = onAction
                                )
                            } else if (readerState.readerError != null) {
                                QuranErrorState(
                                    message = readerState.readerError,
                                    isNotFoundError = false,
                                    isNetworkError = true,
                                    onRetry = {
                                        onAction(
                                            QuranUiAction.OpenReader(
                                                surahNumber = readerState.currentSurahNumber,
                                                ayahNumber = readerState.currentAyahNumber
                                            )
                                        )
                                    },
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        ReaderMode.MUSHAF_PAGE -> {
                            MushafPageReader(
                                readerState = readerState,
                                onAction = onAction
                            )
                        }
                    }
                }
            }

            // Floating Thumb-Zone Audio Transport Dock
            RecitationAudioDock(
                readerState = readerState,
                audioState = audioState,
                onAction = onAction,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Contextual Ayah Action Bottom Sheet
            if (readerState.actionSheetAyah != null) {
                AyahActionBottomSheet(
                    ayah = readerState.actionSheetAyah,
                    onAction = onAction,
                    onDismiss = { onAction(QuranUiAction.DismissAyahActionSheet) }
                )
            }

            // Quick Jump Scrubber Bottom Sheet
            QuickJumpBottomSheet(
                isVisible = catalogState.isQuickJumpSheetVisible,
                surahs = catalogState.surahs,
                activeTab = catalogState.activeQuickJumpTab,
                onSelectTab = { onAction(QuranUiAction.SelectQuickJumpTab(it)) },
                onJumpToAyah = { surah, ayah ->
                    onAction(QuranUiAction.JumpToSurah(surah, ayah))
                },
                onJumpToPage = { page ->
                    onAction(QuranUiAction.JumpToMushafPage(page))
                },
                onJumpToJuz = { juz ->
                    onAction(QuranUiAction.JumpToJuz(juz))
                },
                onDismiss = {
                    onAction(QuranUiAction.SetQuickJumpSheetVisible(false))
                }
            )
        }
    } else if (uiState is QuranUiState.Initial) {
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
            readerState = QuranReaderState(),
            searchQuery = "",
            selectedTranslation = "en.sahih",
            selectedRecitation = "ar.alafasy",
            audioState = AudioState.IDLE,
            onAction = {}
        )
    }
}


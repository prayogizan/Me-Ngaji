package com.uncaan.mengaji.feature.quran.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.uncaan.mengaji.core.ui.FOLDABLE_HINGE_GUTTER
import com.uncaan.mengaji.core.ui.LocalWindowWidthClass
import com.uncaan.mengaji.core.ui.MASTER_PANE_EXPANDED_WIDTH
import com.uncaan.mengaji.core.ui.MASTER_PANE_MIN_WIDTH
import com.uncaan.mengaji.core.ui.MeNgajiWindowWidthClass
import com.uncaan.mengaji.core.ui.VERSE_LIST_MAX_CONTENT_WIDTH
import com.uncaan.mengaji.core.ui.KeepScreenOn
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.presentation.component.AyahActionBottomSheet
import com.uncaan.mengaji.feature.quran.presentation.component.AyahCard
import com.uncaan.mengaji.feature.quran.presentation.component.DualMushafSpreadReader
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
import com.uncaan.mengaji.feature.quran.presentation.component.WideRecitationAudioDock
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.PrimaryGreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stateful root screen composable for the Al-Quran feature.
 *
 * Implements Multi-Factor Adaptive Layouts (CMP-606):
 * - Injects [QuranViewModel] via Koin and observes UI, catalog, reader, and audio state flows.
 * - Dynamically adapts across [MeNgajiWindowWidthClass.COMPACT] (`< 600.dp`), [MeNgajiWindowWidthClass.MEDIUM]
 *   (`600..839.dp` Split-Pane), and [MeNgajiWindowWidthClass.EXPANDED] (`>= 840.dp` Dual-Spread).
 * - Guarantees non-blank detail pane in medium and expanded modes by falling back to last read or Surah 1.
 * - Retains active audio playback, recitation progress, and scroll positions across orientation shifts.
 *
 * @param initialSurahNumber Optional 1-indexed Surah number passed from deep-link navigation.
 * @param initialAyahNumber Optional 1-indexed Ayah number passed from deep-link navigation.
 * @param modifier The layout modifier for the root container.
 * @param viewModel The ViewModel instance providing UI state and action handling.
 * @see AlQuranContent
 * @see QuranViewModel
 * @see AudioState
 * @see MeNgajiWindowWidthClass
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

    val windowWidthClass = LocalWindowWidthClass.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-search and open reader if deep-link parameters are present and positive
    LaunchedEffect(initialSurahNumber, initialAyahNumber) {
        if (initialSurahNumber != null && initialAyahNumber != null &&
            initialSurahNumber > 0 && initialAyahNumber > 0
        ) {
            viewModel.onAction(QuranUiAction.OpenReader(initialSurahNumber, initialAyahNumber))
        }
    }

    // Auto-populate detail pane in Medium and Expanded layouts so the detail pane is never blank
    LaunchedEffect(windowWidthClass, readerState.isReaderActive) {
        if (windowWidthClass != MeNgajiWindowWidthClass.COMPACT && !readerState.isReaderActive) {
            val progress = catalogState.lastReadProgress
            val targetSurah = progress?.surahNumber ?: 1
            val targetAyah = progress?.ayahNumberInSurah ?: 1
            val targetMode = progress?.preferredReaderMode ?: ReaderMode.VERSE_LIST
            viewModel.onAction(QuranUiAction.OpenReader(targetSurah, targetAyah, targetMode))
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
            snackbarHostState.showSnackbar(message = errorMessage)
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
            windowWidthClass = windowWidthClass,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Stateless content composable for the Al-Quran screen orchestrating responsive multi-factor layouts.
 *
 * - **[MeNgajiWindowWidthClass.COMPACT] (`< 600.dp`)**: Single-pane animated transition between [SurahCatalogPane]
 *   and reader using [AnimatedContent].
 * - **[MeNgajiWindowWidthClass.MEDIUM] (`600..839.dp`)**: Two-pane List-Detail split layout (35% master catalog,
 *   16dp hinge-safe divider, 65% active reader).
 * - **[MeNgajiWindowWidthClass.EXPANDED] (`>= 840.dp`)**: Collapsible 320.dp master sidebar and authentic
 *   dual-page RTL Medina Mushaf book spread ([DualMushafSpreadReader]) or centered verse list study canvas.
 *
 * @param uiState Current immutable UI state for single-verse inspection.
 * @param catalogState Current catalog exploration state for Surahs, Juzs, and Bookmarks.
 * @param readerState Current dual-mode recitation reader state.
 * @param searchQuery Current search query text.
 * @param selectedTranslation Currently selected translation edition identifier.
 * @param selectedRecitation Currently selected audio recitation edition identifier.
 * @param audioState Current audio player playback state.
 * @param windowWidthClass Active horizontal viewport class determining responsive layout structure.
 * @param onAction Action callback dispatched to the ViewModel.
 * @param modifier The layout modifier for the container.
 * @see QuranUiState
 * @see QuranCatalogState
 * @see QuranReaderState
 * @see QuranUiAction
 * @see AudioState
 * @see MeNgajiWindowWidthClass
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
    windowWidthClass: MeNgajiWindowWidthClass,
    onAction: (QuranUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (windowWidthClass) {
            MeNgajiWindowWidthClass.COMPACT -> {
                // =========================================================
                // 1. Compact Phone Layout (< 600.dp) - Single-Pane
                // =========================================================
                AnimatedContent(
                    targetState = readerState.isReaderActive to (uiState !is QuranUiState.Initial),
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    label = "CompactLayoutTransition"
                ) { (isReaderActive, isVerseInspection) ->
                    if (isReaderActive) {
                        // Compact Reader Mode (Verse List or Single-Page Mushaf Pager)
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                ReaderTopBar(
                                    readerState = readerState,
                                    onAction = onAction,
                                    showBackButton = true,
                                    showSidebarToggle = false
                                )

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
                        }
                    } else if (!isVerseInspection) {
                        // Master 114-Surah / 30-Juz / Bookmarks Catalog Pane
                        SurahCatalogPane(
                            catalogState = catalogState,
                            onAction = onAction,
                            isSplitPane = false
                        )
                    } else {
                        // Single Ayah Inspection View
                        SingleAyahInspectionView(
                            uiState = uiState,
                            searchQuery = searchQuery,
                            selectedTranslation = selectedTranslation,
                            selectedRecitation = selectedRecitation,
                            audioState = audioState,
                            onAction = onAction
                        )
                    }
                }
            }

            MeNgajiWindowWidthClass.MEDIUM -> {
                // =========================================================
                // 2. Medium Layout (600..839.dp) - Split-Pane Master-Detail
                // =========================================================
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Master Catalog Pane (35% width, min 280.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.35f)
                            .widthIn(min = MASTER_PANE_MIN_WIDTH)
                    ) {
                        SurahCatalogPane(
                            catalogState = catalogState,
                            onAction = onAction,
                            isSplitPane = true,
                            activeSurahNumber = readerState.currentSurahNumber
                        )
                    }

                    // Foldable Hinge Gutter (16.dp)
                    Box(
                        modifier = Modifier
                            .width(FOLDABLE_HINGE_GUTTER)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        VerticalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }

                    // Right Detail Reader Canvas (65% width)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.65f)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            ReaderTopBar(
                                readerState = readerState,
                                onAction = onAction,
                                showBackButton = false,
                                showSidebarToggle = false
                            )

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

                        // Wide Audio Transport Dock
                        WideRecitationAudioDock(
                            readerState = readerState,
                            audioState = audioState,
                            onAction = onAction,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }

            MeNgajiWindowWidthClass.EXPANDED -> {
                // =========================================================
                // 3. Expanded Layout (>= 840.dp) - Collapsible Sidebar + Dual Spread
                // =========================================================
                Row(modifier = Modifier.fillMaxSize()) {
                    // Collapsible Master Sidebar (320.dp)
                    if (readerState.isCatalogSidebarVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(MASTER_PANE_EXPANDED_WIDTH)
                        ) {
                            val facingSurahNumber = readerState.facingMushafPage?.surahs?.firstOrNull()?.number
                            SurahCatalogPane(
                                catalogState = catalogState,
                                onAction = onAction,
                                isSplitPane = true,
                                activeSurahNumber = readerState.currentSurahNumber,
                                facingSurahNumber = facingSurahNumber
                            )
                        }

                        VerticalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }

                    // Main Reader Detail Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            ReaderTopBar(
                                readerState = readerState,
                                onAction = onAction,
                                showBackButton = false,
                                showSidebarToggle = true,
                                isSidebarVisible = readerState.isCatalogSidebarVisible
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                when (readerState.readerMode) {
                                    ReaderMode.VERSE_LIST -> {
                                        // Centered max-width study column for optimal typographic ergonomics
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.TopCenter
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .widthIn(max = VERSE_LIST_MAX_CONTENT_WIDTH)
                                            ) {
                                                if (readerState.isReaderLoading && readerState.surahDetail == null) {
                                                    QuranLoadingSkeleton(modifier = Modifier.padding(16.dp))
                                                } else if (readerState.surahDetail != null) {
                                                    VerseListReader(
                                                        readerState = readerState,
                                                        audioState = audioState,
                                                        onAction = onAction
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    ReaderMode.MUSHAF_PAGE -> {
                                        // Dual-Page Authentic RTL Medina Mushaf Spread
                                        DualMushafSpreadReader(
                                            readerState = readerState,
                                            audioState = audioState,
                                            onAction = onAction
                                        )
                                    }
                                }
                            }
                        }

                        // Wide Audio Transport Dock
                        WideRecitationAudioDock(
                            readerState = readerState,
                            audioState = audioState,
                            onAction = onAction,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }

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
}

/**
 * Single-verse inspection view displayed when an explicit search query is triggered outside reader mode.
 */
@Composable
private fun SingleAyahInspectionView(
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
        // Return to Catalog Header
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.clickable { onAction(QuranUiAction.BackToCatalog) }
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

@Preview
@Composable
private fun AlQuranContentCompactPreview() {
    MeNgajiTheme {
        AlQuranContent(
            uiState = QuranUiState.Initial,
            catalogState = QuranCatalogState(),
            readerState = QuranReaderState(),
            searchQuery = "",
            selectedTranslation = "en.sahih",
            selectedRecitation = "ar.alafasy",
            audioState = AudioState.IDLE,
            windowWidthClass = MeNgajiWindowWidthClass.COMPACT,
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun AlQuranContentMediumPreview() {
    MeNgajiTheme {
        AlQuranContent(
            uiState = QuranUiState.Initial,
            catalogState = QuranCatalogState(),
            readerState = QuranReaderState(isReaderActive = true),
            searchQuery = "",
            selectedTranslation = "en.sahih",
            selectedRecitation = "ar.alafasy",
            audioState = AudioState.IDLE,
            windowWidthClass = MeNgajiWindowWidthClass.MEDIUM,
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun AlQuranContentExpandedPreview() {
    MeNgajiTheme {
        AlQuranContent(
            uiState = QuranUiState.Initial,
            catalogState = QuranCatalogState(),
            readerState = QuranReaderState(isReaderActive = true, readerMode = ReaderMode.MUSHAF_PAGE),
            searchQuery = "",
            selectedTranslation = "en.sahih",
            selectedRecitation = "ar.alafasy",
            audioState = AudioState.IDLE,
            windowWidthClass = MeNgajiWindowWidthClass.EXPANDED,
            onAction = {}
        )
    }
}

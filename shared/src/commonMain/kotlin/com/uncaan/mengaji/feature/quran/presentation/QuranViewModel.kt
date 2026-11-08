package com.uncaan.mengaji.feature.quran.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uncaan.mengaji.core.audio.AudioPlayer
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.JuzCatalogData
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetBookmarksUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahListUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing the business logic, UI state orchestration, catalog exploration,
 * and audio playback for the Al-Quran feature.
 *
 * Implements Unidirectional Data Flow (UDF) by exposing immutable [StateFlow] streams
 * and processing actions through [onAction].
 *
 * @property getAyahUseCase The use case for querying Ayah data.
 * @property audioPlayer The cross-platform audio player engine for streaming recitation audio.
 * @property getSurahListUseCase Optional use case for retrieving the complete 114-Surah catalog.
 * @property getBookmarksUseCase Optional use case for retrieving persisted bookmarks.
 * @property getLastReadUseCase Optional use case for retrieving saved recitation progress.
 * @property toggleBookmarkUseCase Optional use case for adding/removing bookmarks.
 * @see GetAyahUseCase
 * @see AudioPlayer
 * @see AudioState
 * @see QuranUiState
 * @see QuranCatalogState
 * @see QuranUiAction
 */
class QuranViewModel(
    private val getAyahUseCase: GetAyahUseCase,
    private val audioPlayer: AudioPlayer,
    private val getSurahListUseCase: GetSurahListUseCase? = null,
    private val getBookmarksUseCase: GetBookmarksUseCase? = null,
    private val getLastReadUseCase: GetLastReadUseCase? = null,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase? = null
) : ViewModel() {

    private val directAyahPattern = Regex("""^\s*(\d{1,3})\s*:\s*(\d{1,3})\s*$""")

    private val _uiState = MutableStateFlow<QuranUiState>(QuranUiState.Initial)
    /** Observable UI state emitted for single verse inspection. */
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private val _catalogState = MutableStateFlow(QuranCatalogState())
    /** Observable UI state for Surah, Juz, and Bookmarks catalog exploration. */
    val catalogState: StateFlow<QuranCatalogState> = _catalogState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    /** Observable search query input string. */
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTranslation = MutableStateFlow(QuranEditionPresets.DEFAULT_TRANSLATION)
    /** Observable active translation edition identifier. */
    val selectedTranslation: StateFlow<String> = _selectedTranslation.asStateFlow()

    private val _selectedRecitation = MutableStateFlow(QuranEditionPresets.DEFAULT_RECITATION)
    /** Observable active audio recitation edition identifier. */
    val selectedRecitation: StateFlow<String> = _selectedRecitation.asStateFlow()

    /** Observable audio playback state emitted from the cross-platform audio engine. */
    val audioState: StateFlow<AudioState> = audioPlayer.audioState

    private var lastSearchedReference: String = ""

    init {
        loadSurahCatalog(forceRefresh = false)
        loadLastRead()
        loadBookmarks()
    }

    /**
     * Single entry-point for dispatching user actions to the ViewModel.
     *
     * @param action The [QuranUiAction] intent to process.
     */
    fun onAction(action: QuranUiAction) {
        when (action) {
            is QuranUiAction.Search -> searchAyah(action.reference)
            is QuranUiAction.UpdateSearchQuery -> _searchQuery.value = action.query
            is QuranUiAction.ClearSearchQuery -> _searchQuery.value = ""
            is QuranUiAction.SelectTranslationEdition -> {
                _selectedTranslation.value = action.editionIdentifier
                if (lastSearchedReference.isNotBlank()) {
                    searchAyah(lastSearchedReference)
                }
            }
            is QuranUiAction.SelectAudioEdition -> {
                _selectedRecitation.value = action.editionIdentifier
                if (lastSearchedReference.isNotBlank()) {
                    searchAyah(lastSearchedReference)
                }
            }
            is QuranUiAction.PlayAudio -> audioPlayer.play(action.url)
            QuranUiAction.PauseAudio -> audioPlayer.pause()
            QuranUiAction.ResumeAudio -> audioPlayer.resume()
            QuranUiAction.StopAudio -> audioPlayer.stop()
            is QuranUiAction.Retry -> retry()

            // Catalog & Quick Jump Actions
            is QuranUiAction.SelectCatalogTab -> {
                _catalogState.update { it.copy(activeTab = action.tab) }
            }
            is QuranUiAction.UpdateCatalogSearch -> {
                updateCatalogSearch(action.query)
            }
            is QuranUiAction.ClearCatalogSearch -> {
                updateCatalogSearch("")
            }
            is QuranUiAction.SetQuickJumpSheetVisible -> {
                _catalogState.update { it.copy(isQuickJumpSheetVisible = action.visible) }
            }
            is QuranUiAction.SelectQuickJumpTab -> {
                _catalogState.update { it.copy(activeQuickJumpTab = action.tab) }
            }
            is QuranUiAction.JumpToSurah -> {
                _catalogState.update { it.copy(isQuickJumpSheetVisible = false) }
                searchAyah("${action.surahNumber}:${action.ayahNumber}")
            }
            is QuranUiAction.JumpToMushafPage -> {
                _catalogState.update { it.copy(isQuickJumpSheetVisible = false) }
                val targetJuz = JuzCatalogData.getJuzForPage(action.pageNumber)
                searchAyah("${targetJuz.startSurahNumber}:${targetJuz.startAyahNumberInSurah}")
            }
            is QuranUiAction.JumpToJuz -> {
                _catalogState.update { it.copy(isQuickJumpSheetVisible = false) }
                val targetJuz = JuzCatalogData.getJuzByNumber(action.juzNumber)
                if (targetJuz != null) {
                    searchAyah("${targetJuz.startSurahNumber}:${targetJuz.startAyahNumberInSurah}")
                }
            }
            is QuranUiAction.ResumeLastRead -> {
                searchAyah("${action.progress.surahNumber}:${action.progress.ayahNumberInSurah}")
            }
            is QuranUiAction.RemoveBookmark -> {
                removeBookmark(action.bookmark)
            }
            is QuranUiAction.RefreshCatalog -> {
                loadSurahCatalog(forceRefresh = true)
                loadLastRead()
                loadBookmarks()
            }
            is QuranUiAction.BackToCatalog -> {
                audioPlayer.stop()
                _uiState.value = QuranUiState.Initial
            }
        }
    }

    /**
     * Executes the Ayah search operation for a given reference query.
     *
     * Automatically stops any currently playing audio before initiating the search.
     *
     * @param reference The Ayah reference (e.g. "2:255"). Blank inputs are ignored.
     */
    fun searchAyah(reference: String) {
        val trimmed = reference.trim()
        if (trimmed.isBlank()) return

        audioPlayer.stop()
        lastSearchedReference = trimmed
        _searchQuery.value = trimmed

        viewModelScope.launch {
            _uiState.value = QuranUiState.Loading

            when (val result = getAyahUseCase(trimmed, _selectedTranslation.value, _selectedRecitation.value)) {
                is AppResult.Success -> {
                    _uiState.value = QuranUiState.Success(
                        ayah = result.data,
                        selectedTranslationEdition = _selectedTranslation.value,
                        selectedAudioEdition = _selectedRecitation.value
                    )
                }
                is AppResult.Error -> {
                    val message = result.message ?: "Failed to load Ayah"
                    val isNotFound = message.contains("not found", ignoreCase = true) ||
                            message.contains("404")
                    val isNetwork = message.contains("network", ignoreCase = true) ||
                            message.contains("internet", ignoreCase = true) ||
                            message.contains("timeout", ignoreCase = true) ||
                            message.contains("getaddrinfo", ignoreCase = true) ||
                            message.contains("hostname", ignoreCase = true) ||
                            message.contains("eai_nodata", ignoreCase = true) ||
                            message.contains("connection", ignoreCase = true) ||
                            message.contains("unresolved", ignoreCase = true)

                    _uiState.value = QuranUiState.Error(
                        message = message,
                        isNotFoundError = isNotFound,
                        isNetworkError = isNetwork
                    )
                }
                AppResult.Loading -> {
                    _uiState.value = QuranUiState.Loading
                }
            }
        }
    }

    /**
     * Retries fetching the last searched Ayah reference if one exists.
     */
    fun retry() {
        if (lastSearchedReference.isNotBlank()) {
            searchAyah(lastSearchedReference)
        }
    }

    /**
     * Loads the 114-Surah catalog list.
     *
     * @param forceRefresh When true, bypasses cache and queries remote API.
     */
    fun loadSurahCatalog(forceRefresh: Boolean = false) {
        val useCase = getSurahListUseCase ?: return
        viewModelScope.launch {
            _catalogState.update { it.copy(isCatalogLoading = true, catalogError = null) }
            when (val result = useCase(forceRefresh)) {
                is AppResult.Success -> {
                    _catalogState.update { current ->
                        current.copy(
                            surahs = result.data,
                            filteredSurahs = filterSurahs(result.data, current.searchQuery),
                            isCatalogLoading = false,
                            catalogError = null
                        )
                    }
                }
                is AppResult.Error -> {
                    _catalogState.update {
                        it.copy(
                            isCatalogLoading = false,
                            catalogError = result.message ?: "Failed to load Quran catalog"
                        )
                    }
                }
                AppResult.Loading -> {
                    _catalogState.update { it.copy(isCatalogLoading = true) }
                }
            }
        }
    }

    /**
     * Loads the user's latest recorded recitation progress checkpoint.
     */
    fun loadLastRead() {
        val useCase = getLastReadUseCase ?: return
        viewModelScope.launch {
            val progress = useCase()
            _catalogState.update { it.copy(lastReadProgress = progress) }
        }
    }

    /**
     * Loads all persisted Ayah bookmarks.
     */
    fun loadBookmarks() {
        val useCase = getBookmarksUseCase ?: return
        viewModelScope.launch {
            val bookmarks = useCase()
            _catalogState.update { it.copy(bookmarks = bookmarks) }
        }
    }

    private fun updateCatalogSearch(query: String) {
        val match = directAyahPattern.matchEntire(query)
        val directTarget = if (match != null) {
            val surah = match.groupValues[1].toIntOrNull()
            val ayah = match.groupValues[2].toIntOrNull()
            if (surah != null && ayah != null && surah in 1..114 && ayah > 0) {
                Pair(surah, ayah)
            } else {
                null
            }
        } else {
            null
        }

        _catalogState.update { current ->
            current.copy(
                searchQuery = query,
                filteredSurahs = filterSurahs(current.surahs, query),
                directJumpTarget = directTarget
            )
        }
    }

    private fun filterSurahs(surahs: List<SurahSummary>, query: String): List<SurahSummary> {
        if (query.isBlank()) return surahs
        val trimmed = query.trim()
        return surahs.filter { surah ->
            surah.englishName.contains(trimmed, ignoreCase = true) ||
                    surah.englishNameTranslation.contains(trimmed, ignoreCase = true) ||
                    surah.number.toString() == trimmed ||
                    surah.name.contains(trimmed)
        }
    }

    private fun removeBookmark(bookmark: AyahBookmark) {
        val useCase = toggleBookmarkUseCase ?: return
        viewModelScope.launch {
            useCase(bookmark)
            loadBookmarks()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}

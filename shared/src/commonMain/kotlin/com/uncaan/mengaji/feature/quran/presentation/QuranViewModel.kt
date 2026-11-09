package com.uncaan.mengaji.feature.quran.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uncaan.mengaji.core.audio.AudioPlayer
import com.uncaan.mengaji.core.audio.AudioRepeatMode
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.core.audio.AudioTrack
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.currentTimeMillis
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.currentTimeMillis
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.JuzCatalogData
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetBookmarksUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetMushafPageUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahDetailUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahListUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.SaveLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing the business logic, UI state orchestration, catalog exploration,
 * dual-mode recitation reader, and audio playback for the Al-Quran feature.
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
 * @property getSurahDetailUseCase Optional use case for retrieving complete Surah details with verses.
 * @property getMushafPageUseCase Optional use case for retrieving 604-page Medina Mushaf pages.
 * @property saveLastReadUseCase Optional use case for persisting recitation progress.
 * @see GetAyahUseCase
 * @see AudioPlayer
 * @see AudioState
 * @see QuranUiState
 * @see QuranCatalogState
 * @see QuranReaderState
 * @see QuranUiAction
 */
class QuranViewModel(
    private val getAyahUseCase: GetAyahUseCase,
    private val audioPlayer: AudioPlayer,
    private val getSurahListUseCase: GetSurahListUseCase? = null,
    private val getBookmarksUseCase: GetBookmarksUseCase? = null,
    private val getLastReadUseCase: GetLastReadUseCase? = null,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase? = null,
    private val getSurahDetailUseCase: GetSurahDetailUseCase? = null,
    private val getMushafPageUseCase: GetMushafPageUseCase? = null,
    private val saveLastReadUseCase: SaveLastReadUseCase? = null
) : ViewModel() {

    private val directAyahPattern = Regex("""^\s*(\d{1,3})\s*:\s*(\d{1,3})\s*$""")

    private val _uiState = MutableStateFlow<QuranUiState>(QuranUiState.Initial)
    /** Observable UI state emitted for single verse inspection. */
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private val _catalogState = MutableStateFlow(QuranCatalogState())
    /** Observable UI state for Surah, Juz, and Bookmarks catalog exploration. */
    val catalogState: StateFlow<QuranCatalogState> = _catalogState.asStateFlow()

    private val _readerState = MutableStateFlow(QuranReaderState())
    /** Observable UI state for the Dual-Mode Recitation Reader. */
    val readerState: StateFlow<QuranReaderState> = _readerState.asStateFlow()

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

        viewModelScope.launch {
            audioPlayer.audioState.collect { state ->
                val surah = state.currentSurahNumber
                val ayah = state.currentAyahNumberInSurah
                if (surah != null && ayah != null) {
                    _readerState.update { current ->
                        if (current.isReaderActive) {
                            val page = current.surahDetail?.ayahs?.find { it.numberInSurah == ayah }?.page
                                ?: current.currentPageNumber
                            current.copy(
                                currentSurahNumber = surah,
                                currentAyahNumber = ayah,
                                currentPageNumber = page
                            )
                        } else current
                    }
                }
            }
        }
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
                if (_readerState.value.isReaderActive) {
                    loadSurahDetail(_readerState.value.currentSurahNumber, _readerState.value.currentAyahNumber)
                    if (_readerState.value.readerMode == ReaderMode.MUSHAF_PAGE) {
                        loadMushafPage(_readerState.value.currentPageNumber)
                    }
                }
            }
            is QuranUiAction.SelectAudioEdition -> {
                _selectedRecitation.value = action.editionIdentifier
                if (lastSearchedReference.isNotBlank()) {
                    searchAyah(lastSearchedReference)
                }
                if (_readerState.value.isReaderActive) {
                    loadSurahDetail(_readerState.value.currentSurahNumber, _readerState.value.currentAyahNumber)
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
                openReader(action.surahNumber, action.ayahNumber, ReaderMode.VERSE_LIST)
            }
            is QuranUiAction.JumpToMushafPage -> {
                _catalogState.update { it.copy(isQuickJumpSheetVisible = false) }
                val targetJuz = JuzCatalogData.getJuzForPage(action.pageNumber)
                searchAyah("${targetJuz.startSurahNumber}:${targetJuz.startAyahNumberInSurah}")
                openReaderByPage(action.pageNumber)
            }
            is QuranUiAction.JumpToJuz -> {
                _catalogState.update { it.copy(isQuickJumpSheetVisible = false) }
                val targetJuz = JuzCatalogData.getJuzByNumber(action.juzNumber)
                if (targetJuz != null) {
                    searchAyah("${targetJuz.startSurahNumber}:${targetJuz.startAyahNumberInSurah}")
                    openReader(targetJuz.startSurahNumber, targetJuz.startAyahNumberInSurah, ReaderMode.VERSE_LIST)
                }
            }
            is QuranUiAction.ResumeLastRead -> {
                searchAyah("${action.progress.surahNumber}:${action.progress.ayahNumberInSurah}")
                openReader(action.progress.surahNumber, action.progress.ayahNumberInSurah, action.progress.preferredReaderMode)
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
                _readerState.update {
                    it.copy(
                        isReaderActive = false,
                        isKhushuMode = false,
                        actionSheetAyah = null
                    )
                }
            }

            // Reader & Recitation Actions (CMP-605)
            is QuranUiAction.OpenReader -> {
                openReader(action.surahNumber, action.ayahNumber, action.mode)
            }
            is QuranUiAction.SwitchReaderMode -> {
                switchReaderMode(action.mode)
            }
            is QuranUiAction.LoadMushafPage -> {
                loadMushafPage(action.pageNumber)
            }
            QuranUiAction.ToggleKhushuMode -> {
                _readerState.update { it.copy(isKhushuMode = !it.isKhushuMode) }
            }
            is QuranUiAction.ShowAyahActionSheet -> {
                _readerState.update { it.copy(actionSheetAyah = action.ayah) }
            }
            QuranUiAction.DismissAyahActionSheet -> {
                _readerState.update { it.copy(actionSheetAyah = null) }
            }
            is QuranUiAction.PlayFromAyah -> {
                playFromAyah(action.ayah)
            }
            is QuranUiAction.LoopAyah -> {
                loopAyah(action.ayah)
            }
            is QuranUiAction.SaveManualLastRead -> {
                saveManualLastRead(action.ayah)
            }
            is QuranUiAction.ToggleBookmarkForAyah -> {
                toggleBookmarkForAyah(action.ayah)
            }
            is QuranUiAction.SetPlaybackSpeed -> {
                audioPlayer.setPlaybackSpeed(action.speed)
            }
            QuranUiAction.CycleRepeatMode -> {
                cycleRepeatMode()
            }
            QuranUiAction.SkipToNextVerse -> {
                audioPlayer.skipToNext()
            }
            QuranUiAction.SkipToPreviousVerse -> {
                audioPlayer.skipToPrevious()
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

    /**
     * Activates the reader view for the given Surah and Ayah in the specified mode.
     *
     * @param surahNumber 1-indexed Surah number (`1..114`).
     * @param ayahNumber 1-indexed Ayah number within the Surah. Defaults to `1`.
     * @param mode The desired [ReaderMode] presentation layout.
     */
    fun openReader(
        surahNumber: Int,
        ayahNumber: Int = 1,
        mode: ReaderMode = ReaderMode.VERSE_LIST
    ) {
        val boundedSurah = surahNumber.coerceIn(1, 114)
        val boundedAyah = ayahNumber.coerceAtLeast(1)
        _readerState.update {
            it.copy(
                isReaderActive = true,
                readerMode = mode,
                currentSurahNumber = boundedSurah,
                currentAyahNumber = boundedAyah,
                isKhushuMode = false,
                actionSheetAyah = null,
                readerError = null
            )
        }
        loadSurahDetail(boundedSurah, boundedAyah)
    }

    /**
     * Activates the reader view at a specific Medina Mushaf page.
     *
     * @param pageNumber 1-indexed page number (`1..604`).
     */
    fun openReaderByPage(pageNumber: Int) {
        val boundedPage = pageNumber.coerceIn(1, 604)
        _readerState.update {
            it.copy(
                isReaderActive = true,
                readerMode = ReaderMode.MUSHAF_PAGE,
                currentPageNumber = boundedPage,
                isKhushuMode = false,
                actionSheetAyah = null,
                readerError = null
            )
        }
        loadMushafPage(boundedPage)
    }

    /**
     * Transitions between Verse-by-Verse and Mushaf Page modes while preserving reading position.
     *
     * @param mode Target [ReaderMode] layout.
     */
    fun switchReaderMode(mode: ReaderMode) {
        if (_readerState.value.readerMode == mode) return

        val currentState = _readerState.value
        if (mode == ReaderMode.MUSHAF_PAGE) {
            val currentAyah = currentState.surahDetail?.ayahs?.find {
                it.numberInSurah == currentState.currentAyahNumber
            } ?: currentState.surahDetail?.ayahs?.firstOrNull()
            val targetPage = currentAyah?.page ?: currentState.currentPageNumber
            _readerState.update {
                it.copy(
                    readerMode = ReaderMode.MUSHAF_PAGE,
                    currentPageNumber = targetPage
                )
            }
            loadMushafPage(targetPage)
        } else {
            val activeAyahOnPage = currentState.mushafPage?.ayahs?.find {
                it.number == audioState.value.currentGlobalAyahNumber
            } ?: currentState.mushafPage?.ayahs?.firstOrNull()

            val targetSurah = activeAyahOnPage?.surahNumber ?: currentState.currentSurahNumber
            val targetAyah = activeAyahOnPage?.numberInSurah ?: currentState.currentAyahNumber

            _readerState.update {
                it.copy(
                    readerMode = ReaderMode.VERSE_LIST,
                    currentSurahNumber = targetSurah,
                    currentAyahNumber = targetAyah
                )
            }
            if (currentState.surahDetail?.number != targetSurah) {
                loadSurahDetail(targetSurah, targetAyah)
            }
        }
    }

    /**
     * Loads complete Surah details including verse texts, translation, and recitation audio URLs.
     *
     * @param surahNumber 1-indexed Surah number (`1..114`).
     * @param targetAyahNumber Optional Ayah number to focus after loading.
     */
    fun loadSurahDetail(surahNumber: Int, targetAyahNumber: Int = 1) {
        val useCase = getSurahDetailUseCase ?: return
        viewModelScope.launch {
            _readerState.update { it.copy(isReaderLoading = true, readerError = null) }
            when (val result = useCase(surahNumber, _selectedTranslation.value, _selectedRecitation.value)) {
                is AppResult.Success -> {
                    val detail = result.data
                    val resolvedAyah = detail.ayahs.find { it.numberInSurah == targetAyahNumber } ?: detail.ayahs.firstOrNull()
                    val pageNumber = resolvedAyah?.page ?: 1
                    _readerState.update {
                        it.copy(
                            surahDetail = detail,
                            currentSurahNumber = surahNumber,
                            currentAyahNumber = resolvedAyah?.numberInSurah ?: targetAyahNumber,
                            currentPageNumber = pageNumber,
                            isReaderLoading = false
                        )
                    }
                    if (_readerState.value.readerMode == ReaderMode.MUSHAF_PAGE) {
                        loadMushafPage(pageNumber)
                    }
                }
                is AppResult.Error -> {
                    _readerState.update {
                        it.copy(
                            isReaderLoading = false,
                            readerError = result.message ?: "Failed to load Surah"
                        )
                    }
                }
                AppResult.Loading -> {
                    _readerState.update { it.copy(isReaderLoading = true) }
                }
            }
        }
    }

    /**
     * Loads a single 604-page Medina Mushaf page with Uthmani verses and translation text.
     *
     * @param pageNumber 1-indexed page number (`1..604`).
     */
    fun loadMushafPage(pageNumber: Int) {
        val useCase = getMushafPageUseCase ?: return
        val boundedPage = pageNumber.coerceIn(1, 604)
        viewModelScope.launch {
            _readerState.update { it.copy(isReaderLoading = true, readerError = null) }
            when (val result = useCase(boundedPage, _selectedTranslation.value)) {
                is AppResult.Success -> {
                    val page = result.data
                    val firstAyah = page.ayahs.firstOrNull()
                    _readerState.update {
                        it.copy(
                            mushafPage = page,
                            currentPageNumber = boundedPage,
                            currentSurahNumber = firstAyah?.surahNumber ?: it.currentSurahNumber,
                            currentAyahNumber = firstAyah?.numberInSurah ?: it.currentAyahNumber,
                            isReaderLoading = false
                        )
                    }
                }
                is AppResult.Error -> {
                    _readerState.update {
                        it.copy(
                            isReaderLoading = false,
                            readerError = result.message ?: "Failed to load Mushaf page"
                        )
                    }
                }
                AppResult.Loading -> {
                    _readerState.update { it.copy(isReaderLoading = true) }
                }
            }
        }
    }

    /**
     * Begins queue recitation playback starting from the specified Ayah through the end of the Surah.
     *
     * @param ayah The starting verse domain entity.
     */
    fun playFromAyah(ayah: Ayah) {
        val surahDetail = _readerState.value.surahDetail
        val ayahs = surahDetail?.ayahs ?: listOf(ayah)

        val tracks = ayahs.mapNotNull { verse ->
            val url = verse.audioUrl
            if (!url.isNullOrBlank()) {
                AudioTrack(
                    url = url,
                    surahNumber = verse.surahNumber,
                    ayahNumberInSurah = verse.numberInSurah,
                    globalAyahNumber = verse.number
                )
            } else null
        }

        if (tracks.isNotEmpty()) {
            val resolvedIndex = tracks.indexOfFirst { it.globalAyahNumber == ayah.number }.coerceAtLeast(0)
            audioPlayer.playQueue(tracks, resolvedIndex)
        } else if (!ayah.audioUrl.isNullOrBlank()) {
            audioPlayer.play(ayah.audioUrl)
        }
    }

    /**
     * Loops a single verse continuously using [AudioRepeatMode.REPEAT_AYAH].
     *
     * @param ayah The verse domain entity to loop.
     */
    fun loopAyah(ayah: Ayah) {
        audioPlayer.setRepeatMode(AudioRepeatMode.REPEAT_AYAH)
        val url = ayah.audioUrl
        if (!url.isNullOrBlank()) {
            val track = AudioTrack(
                url = url,
                surahNumber = ayah.surahNumber,
                ayahNumberInSurah = ayah.numberInSurah,
                globalAyahNumber = ayah.number
            )
            audioPlayer.playQueue(listOf(track), 0)
        }
    }

    /**
     * Cycles through available repeat modes: NONE -> REPEAT_AYAH -> REPEAT_SURAH -> NONE.
     */
    fun cycleRepeatMode() {
        val current = audioState.value.repeatMode
        val next = when (current) {
            AudioRepeatMode.NONE -> AudioRepeatMode.REPEAT_AYAH
            AudioRepeatMode.REPEAT_AYAH -> AudioRepeatMode.REPEAT_SURAH
            AudioRepeatMode.REPEAT_SURAH -> AudioRepeatMode.NONE
        }
        audioPlayer.setRepeatMode(next)
    }

    /**
     * Manually persists recitation progress at the specified verse checkpoint.
     *
     * @param ayah The verse domain entity where progress was recorded.
     */
    fun saveManualLastRead(ayah: Ayah) {
        val useCase = saveLastReadUseCase ?: return
        viewModelScope.launch {
            val progress = LastReadProgress(
                surahNumber = ayah.surahNumber,
                surahNameEnglish = ayah.surahName,
                surahNameArabic = ayah.surahArabicName,
                ayahNumberInSurah = ayah.numberInSurah,
                pageNumber = ayah.page,
                juzNumber = ayah.juz,
                preferredReaderMode = _readerState.value.readerMode,
                updatedAtEpochMillis = currentTimeMillis()
            )
            useCase(progress)
            loadLastRead()
        }
    }

    /**
     * Automatically records recitation progress triggered by debounced scroll observation.
     *
     * @param ayah The visible verse domain entity to record.
     */
    fun saveDebouncedLastRead(ayah: Ayah) {
        val useCase = saveLastReadUseCase ?: return
        viewModelScope.launch {
            val progress = LastReadProgress(
                surahNumber = ayah.surahNumber,
                surahNameEnglish = ayah.surahName,
                surahNameArabic = ayah.surahArabicName,
                ayahNumberInSurah = ayah.numberInSurah,
                pageNumber = ayah.page,
                juzNumber = ayah.juz,
                preferredReaderMode = _readerState.value.readerMode,
                updatedAtEpochMillis = currentTimeMillis()
            )
            useCase(progress)
            _catalogState.update { it.copy(lastReadProgress = progress) }
        }
    }

    /**
     * Toggles the bookmark status for a specific verse.
     *
     * @param ayah The verse domain entity to bookmark or unbookmark.
     */
    fun toggleBookmarkForAyah(ayah: Ayah) {
        val useCase = toggleBookmarkUseCase ?: return
        viewModelScope.launch {
            val bookmark = AyahBookmark(
                globalAyahNumber = ayah.number,
                surahNumber = ayah.surahNumber,
                surahNameEnglish = ayah.surahName,
                surahNameArabic = ayah.surahArabicName,
                ayahNumberInSurah = ayah.numberInSurah,
                pageNumber = ayah.page,
                arabicSnippet = ayah.text,
                savedAtEpochMillis = currentTimeMillis()
            )
            useCase(bookmark)
            loadBookmarks()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}

package com.uncaan.mengaji.feature.quran.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uncaan.mengaji.core.audio.AudioPlayer
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the business logic, UI state orchestration, and audio playback for the Al-Quran feature.
 *
 * Implements Unidirectional Data Flow (UDF) by exposing immutable [StateFlow] streams
 * and processing actions through [onAction].
 *
 * @property getAyahUseCase The use case for querying Ayah data.
 * @property audioPlayer The cross-platform audio player engine for streaming recitation audio.
 * @see GetAyahUseCase
 * @see AudioPlayer
 * @see AudioState
 * @see QuranUiState
 * @see QuranUiAction
 */
class QuranViewModel(
    private val getAyahUseCase: GetAyahUseCase,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuranUiState>(QuranUiState.Initial)
    /** Observable UI state emitted to the presentation layer. */
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

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

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}

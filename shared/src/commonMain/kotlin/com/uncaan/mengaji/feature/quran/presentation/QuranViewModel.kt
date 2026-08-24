package com.uncaan.mengaji.feature.quran.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuranViewModel(
    private val getAyahUseCase: GetAyahUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuranUiState>(QuranUiState.Initial)
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTranslation = MutableStateFlow(QuranEditionPresets.DEFAULT_TRANSLATION)
    val selectedTranslation: StateFlow<String> = _selectedTranslation.asStateFlow()

    private val _selectedRecitation = MutableStateFlow(QuranEditionPresets.DEFAULT_RECITATION)
    val selectedRecitation: StateFlow<String> = _selectedRecitation.asStateFlow()

    private var lastSearchedReference: String = ""

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
            is QuranUiAction.Retry -> retry()
        }
    }

    fun searchAyah(reference: String) {
        val trimmed = reference.trim()
        if (trimmed.isBlank()) return

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

    fun retry() {
        if (lastSearchedReference.isNotBlank()) {
            searchAyah(lastSearchedReference)
        }
    }
}

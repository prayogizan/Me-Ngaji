package com.uncaan.mengaji.feature.quran.presentation

import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets

sealed interface QuranUiState {
    data object Initial : QuranUiState
    data object Loading : QuranUiState
    data class Success(
        val ayah: Ayah,
        val selectedTranslationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        val selectedAudioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ) : QuranUiState
    data class Error(
        val message: String,
        val isNotFoundError: Boolean = false,
        val isNetworkError: Boolean = false
    ) : QuranUiState
}

sealed interface QuranUiAction {
    data class Search(val reference: String) : QuranUiAction
    data class UpdateSearchQuery(val query: String) : QuranUiAction
    data object ClearSearchQuery : QuranUiAction
    data class SelectTranslationEdition(val editionIdentifier: String) : QuranUiAction
    data class SelectAudioEdition(val editionIdentifier: String) : QuranUiAction
    data object Retry : QuranUiAction
}

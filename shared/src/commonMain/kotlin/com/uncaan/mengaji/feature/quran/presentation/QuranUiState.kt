package com.uncaan.mengaji.feature.quran.presentation

import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets

/**
 * Sealed hierarchy representing all possible UI states for the Al-Quran feature.
 */
sealed interface QuranUiState {

    /** Initial state before any Ayah search has been performed. */
    data object Initial : QuranUiState

    /** In-flight network request fetching Ayah data. */
    data object Loading : QuranUiState

    /**
     * Successful retrieval of an Ayah.
     *
     * @property ayah The retrieved domain [Ayah] model.
     * @property selectedTranslationEdition The active translation edition identifier.
     * @property selectedAudioEdition The active audio recitation edition identifier.
     */
    data class Success(
        val ayah: Ayah,
        val selectedTranslationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        val selectedAudioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ) : QuranUiState

    /**
     * Failure state when fetching an Ayah fails.
     *
     * @property message Descriptive error message for display.
     * @property isNotFoundError True if the error is due to an invalid/non-existent Ayah reference (404).
     * @property isNetworkError True if the error is due to connectivity or network timeouts.
     */
    data class Error(
        val message: String,
        val isNotFoundError: Boolean = false,
        val isNetworkError: Boolean = false
    ) : QuranUiState
}

/**
 * Sealed hierarchy representing user-initiated actions and intents on the Al-Quran screen.
 */
sealed interface QuranUiAction {

    /**
     * Triggers a search for an Ayah reference.
     *
     * @property reference Surah:Ayah or global verse index.
     */
    data class Search(val reference: String) : QuranUiAction

    /**
     * Updates the search query text field.
     *
     * @property query The new text input.
     */
    data class UpdateSearchQuery(val query: String) : QuranUiAction

    /** Clears the current search text input. */
    data object ClearSearchQuery : QuranUiAction

    /**
     * Selects a translation edition preset and re-triggers active search if present.
     *
     * @property editionIdentifier The selected translation edition identifier.
     */
    data class SelectTranslationEdition(val editionIdentifier: String) : QuranUiAction

    /**
     * Selects an audio recitation edition preset and re-triggers active search if present.
     *
     * @property editionIdentifier The selected audio recitation edition identifier.
     */
    data class SelectAudioEdition(val editionIdentifier: String) : QuranUiAction

    /** Re-executes the last searched Ayah query. */
    data object Retry : QuranUiAction
}

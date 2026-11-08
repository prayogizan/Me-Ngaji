package com.uncaan.mengaji.feature.quran.presentation

import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.JuzCatalogData
import com.uncaan.mengaji.feature.quran.domain.model.JuzSummary
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.presentation.model.CatalogTab
import com.uncaan.mengaji.feature.quran.presentation.model.QuickJumpTab

/**
 * Sealed hierarchy representing all possible UI states for single-verse Ayah inspection.
 */
sealed interface QuranUiState {

    /** Initial state before any specific Ayah search has been performed. */
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
 * Immutable UI state model for the Quran Catalog Pane and Quick Jump bottom sheet.
 *
 * @property surahs The complete 114-Surah catalog list.
 * @property filteredSurahs Filtered Surah list matching the active [searchQuery].
 * @property juzs The 30-Juz catalog list provided by [JuzCatalogData.allJuz].
 * @property bookmarks The user's persisted bookmarks ordered newest first.
 * @property lastReadProgress The latest recorded recitation progress, or `null` if none exists.
 * @property activeTab The active segmented filter tab ([CatalogTab.SURAH], [CatalogTab.JUZ], [CatalogTab.BOOKMARKS]).
 * @property searchQuery The current catalog filter query string.
 * @property directJumpTarget Parsed `(surahNumber, ayahNumber)` pair when [searchQuery] matches `surah:ayah` notation.
 * @property isCatalogLoading True if the 114-Surah catalog is actively being fetched.
 * @property catalogError Error message if loading the Surah catalog fails.
 * @property isQuickJumpSheetVisible True if the Quick Jump Scrubber bottom sheet is currently displayed.
 * @property activeQuickJumpTab The active sub-mode tab within the Quick Jump bottom sheet.
 */
data class QuranCatalogState(
    val surahs: List<SurahSummary> = emptyList(),
    val filteredSurahs: List<SurahSummary> = emptyList(),
    val juzs: List<JuzSummary> = JuzCatalogData.allJuz,
    val bookmarks: List<AyahBookmark> = emptyList(),
    val lastReadProgress: LastReadProgress? = null,
    val activeTab: CatalogTab = CatalogTab.SURAH,
    val searchQuery: String = "",
    val directJumpTarget: Pair<Int, Int>? = null,
    val isCatalogLoading: Boolean = false,
    val catalogError: String? = null,
    val isQuickJumpSheetVisible: Boolean = false,
    val activeQuickJumpTab: QuickJumpTab = QuickJumpTab.SURAH_AYAH
)

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

    /**
     * Begins or replaces audio playback with the given audio stream URL.
     *
     * @property url The HTTPS URL of the audio file to stream.
     */
    data class PlayAudio(val url: String) : QuranUiAction

    /** Pauses the active audio recitation. */
    data object PauseAudio : QuranUiAction

    /** Resumes the currently paused audio recitation. */
    data object ResumeAudio : QuranUiAction

    /** Stops audio playback and resets the player to idle state. */
    data object StopAudio : QuranUiAction

    /** Re-executes the last searched Ayah query. */
    data object Retry : QuranUiAction

    // ==========================================
    // Catalog & Quick Jump Actions (CMP-604)
    // ==========================================

    /**
     * Switches the active catalog segmented filter tab.
     *
     * @property tab The target [CatalogTab] to display.
     */
    data class SelectCatalogTab(val tab: CatalogTab) : QuranUiAction

    /**
     * Updates the real-time catalog search filter query.
     *
     * @property query The text query filtering Surahs or indicating `surah:ayah` jump.
     */
    data class UpdateCatalogSearch(val query: String) : QuranUiAction

    /** Clears the catalog search input query. */
    data object ClearCatalogSearch : QuranUiAction

    /**
     * Toggles visibility of the Quick Jump Scrubber bottom sheet.
     *
     * @property visible True to show the bottom sheet, false to dismiss.
     */
    data class SetQuickJumpSheetVisible(val visible: Boolean) : QuranUiAction

    /**
     * Switches the active mode tab within the Quick Jump bottom sheet.
     *
     * @property tab The target [QuickJumpTab] mode.
     */
    data class SelectQuickJumpTab(val tab: QuickJumpTab) : QuranUiAction

    /**
     * Triggers navigation / focus to a specific Surah and Ayah.
     *
     * @property surahNumber 1-indexed Surah number (`1..114`).
     * @property ayahNumber 1-indexed Ayah number within the Surah. Defaults to `1`.
     */
    data class JumpToSurah(val surahNumber: Int, val ayahNumber: Int = 1) : QuranUiAction

    /**
     * Triggers navigation / focus to a specific Medina Mushaf page.
     *
     * @property pageNumber 1-indexed page number (`1..604`).
     */
    data class JumpToMushafPage(val pageNumber: Int) : QuranUiAction

    /**
     * Triggers navigation / focus to the beginning of a specific Juz.
     *
     * @property juzNumber 1-indexed Juz number (`1..30`).
     */
    data class JumpToJuz(val juzNumber: Int) : QuranUiAction

    /**
     * Resumes recitation from the persisted [LastReadProgress] checkpoint.
     *
     * @property progress The saved recitation progress entity.
     */
    data class ResumeLastRead(val progress: LastReadProgress) : QuranUiAction

    /**
     * Removes an existing bookmark from local storage.
     *
     * @property bookmark The [AyahBookmark] entity to delete.
     */
    data class RemoveBookmark(val bookmark: AyahBookmark) : QuranUiAction

    /** Manually reloads the 114-Surah catalog bypassing cache. */
    data object RefreshCatalog : QuranUiAction

    /** Returns from single Ayah detail view back to the catalog pane. */
    data object BackToCatalog : QuranUiAction
}

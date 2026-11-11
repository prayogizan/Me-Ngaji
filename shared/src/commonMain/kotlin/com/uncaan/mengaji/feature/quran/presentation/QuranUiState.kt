package com.uncaan.mengaji.feature.quran.presentation

import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.JuzCatalogData
import com.uncaan.mengaji.feature.quran.domain.model.JuzSummary
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
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
 * Immutable UI state model for the Dual-Mode Recitation Reader (CMP-605).
 *
 * Encapsulates the active reader presentation mode, loaded Surah and Mushaf page entities,
 * active reading coordinates, distraction-free Khushu' mode status, and contextual Ayah action sheet state.
 *
 * @property isReaderActive True if the reader interface is displayed, false when exploring the catalog.
 * @property readerMode The active presentation layout ([ReaderMode.VERSE_LIST] or [ReaderMode.MUSHAF_PAGE]).
 * @property surahDetail The loaded Surah entity with full verses, or `null` if loading/not loaded.
 * @property mushafPage The loaded Medina Mushaf page entity (`1..604`), or `null` if loading/not loaded. On dual spreads, represents the right odd leaf ($2k+1$).
 * @property facingMushafPage The loaded facing Medina Mushaf page entity (`1..604`) for dual spreads, or `null`. Represents the left even leaf ($2k+2$).
 * @property isFacingPageLoading True if facing page data is actively buffering in dual-spread mode.
 * @property isCatalogSidebarVisible True if the master catalog sidebar/drawer is visible on medium and expanded displays.
 * @property currentSurahNumber The 1-indexed active Surah ordinal (`1..114`). Defaults to `1`.
 * @property currentAyahNumber The 1-indexed active Ayah ordinal within the Surah. Defaults to `1`.
 * @property currentPageNumber The 1-indexed active Medina Mushaf page ordinal (`1..604`). Defaults to `1`.
 * @property highlightedAyahNumber The 1-indexed ordinal position of the Ayah focused and highlighted via deep link, or `null`.
 * @property isKhushuMode True if distraction-free immersive reading mode is toggled (hides top bar and bottom dock).
 * @property actionSheetAyah The [Ayah] currently selected for contextual operations in [AyahActionBottomSheet], or `null` if closed.
 * @property isReaderLoading True if Surah detail or Mushaf page data is actively being fetched.
 * @property readerError Error message if loading the Surah or Mushaf page fails, or `null`.
 * @see ReaderMode
 * @see SurahDetail
 * @see MushafPage
 * @see Ayah
 */
data class QuranReaderState(
    val isReaderActive: Boolean = false,
    val readerMode: ReaderMode = ReaderMode.VERSE_LIST,
    val surahDetail: SurahDetail? = null,
    val mushafPage: MushafPage? = null,
    val facingMushafPage: MushafPage? = null,
    val isFacingPageLoading: Boolean = false,
    val isCatalogSidebarVisible: Boolean = true,
    val currentSurahNumber: Int = 1,
    val currentAyahNumber: Int = 1,
    val currentPageNumber: Int = 1,
    val highlightedAyahNumber: Int? = null,
    val isKhushuMode: Boolean = false,
    val actionSheetAyah: Ayah? = null,
    val isReaderLoading: Boolean = false,
    val readerError: String? = null
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

    /** Returns from single Ayah detail view or reader mode back to the catalog pane. */
    data object BackToCatalog : QuranUiAction

    /**
     * Closes the active recitation reader and returns smoothly to the catalog pane.
     *
     * Halts active recitation audio and resets Khushu' mode, highlighted verse, and reader states.
     * Dispatched by the system Back button on compact devices and the reader top-bar back icon.
     *
     * @see BackToCatalog
     */
    data object CloseReaderToCatalog : QuranUiAction

    // ==========================================
    // Reader & Recitation Actions (CMP-605, CMP-607)
    // ==========================================

    /**
     * Activates the reader directly for a specific Surah, scrolling to and highlighting the target Ayah.
     *
     * Used by widget deep-link intents (CMP-504, CMP-607) and catalog item selections.
     *
     * @property surahNumber 1-indexed Surah number (`1..114`).
     * @property scrollToAyah 1-indexed Ayah number within the Surah to scroll to and highlight. Defaults to `1`.
     * @see OpenReader
     */
    data class OpenSurah(
        val surahNumber: Int,
        val scrollToAyah: Int = 1
    ) : QuranUiAction

    /**
     * Activates the reader view for a specific Surah, Ayah, and presentation mode.
     *
     * @property surahNumber 1-indexed Surah number (`1..114`).
     * @property ayahNumber 1-indexed Ayah number within the Surah. Defaults to `1`.
     * @property mode Preferred initial reader mode. Defaults to [ReaderMode.VERSE_LIST].
     */
    data class OpenReader(
        val surahNumber: Int,
        val ayahNumber: Int = 1,
        val mode: ReaderMode = ReaderMode.VERSE_LIST
    ) : QuranUiAction

    /**
     * Switches between Verse-by-Verse List and RTL Mushaf Page presentation modes.
     *
     * @property mode The target [ReaderMode] layout.
     */
    data class SwitchReaderMode(val mode: ReaderMode) : QuranUiAction

    /**
     * Loads and displays a specific Medina Mushaf page.
     *
     * @property pageNumber 1-indexed page number (`1..604`).
     */
    data class LoadMushafPage(val pageNumber: Int) : QuranUiAction

    /**
     * Loads both the odd right leaf ($2k+1$) and even left leaf ($2k+2$) for an authentic dual-page spread.
     *
     * @property spreadIndex 0-indexed Medina Mushaf spread ordinal (`0..301`).
     */
    data class LoadMushafSpread(val spreadIndex: Int) : QuranUiAction

    /**
     * Toggles visibility of the master catalog sidebar/drawer in medium and expanded layouts.
     */
    data object ToggleCatalogSidebar : QuranUiAction

    /**
     * Selects a verse within a dual-page spread, noting whether it originates from the facing page.
     *
     * @property ayah The selected [Ayah] entity.
     * @property isFacingPage True if the verse belongs to the left facing page, false if right page.
     */
    data class SelectSpreadAyah(val ayah: Ayah, val isFacingPage: Boolean) : QuranUiAction

    /**
     * Toggles distraction-free Khushu' immersive mode on or off.
     */
    data object ToggleKhushuMode : QuranUiAction

    /**
     * Opens the Ayah action bottom sheet for the specified verse.
     *
     * @property ayah The target [Ayah] domain entity.
     */
    data class ShowAyahActionSheet(val ayah: Ayah) : QuranUiAction

    /** Dismisses the active Ayah action bottom sheet. */
    data object DismissAyahActionSheet : QuranUiAction

    /**
     * Begins continuous recitation playback from the specified verse through the end of the Surah.
     *
     * @property ayah The starting [Ayah] entity.
     */
    data class PlayFromAyah(val ayah: Ayah) : QuranUiAction

    /**
     * Sets repeat mode to single-verse looping and begins playback for the specified verse.
     *
     * @property ayah The [Ayah] entity to loop.
     */
    data class LoopAyah(val ayah: Ayah) : QuranUiAction

    /**
     * Manually records and persists recitation progress at the specified verse checkpoint.
     *
     * @property ayah The [Ayah] checkpoint to persist.
     */
    data class SaveManualLastRead(val ayah: Ayah) : QuranUiAction

    /**
     * Toggles the bookmark status for the specified verse.
     *
     * @property ayah The [Ayah] to bookmark or unbookmark.
     */
    data class ToggleBookmarkForAyah(val ayah: Ayah) : QuranUiAction

    /**
     * Configures the audio recitation playback speed.
     *
     * @property speed The playback speed multiplier (`0.75f`, `1.0f`, `1.25f`, `1.5f`).
     */
    data class SetPlaybackSpeed(val speed: Float) : QuranUiAction

    /** Cycles through available audio repeat modes: NONE -> REPEAT_AYAH -> REPEAT_SURAH -> NONE. */
    data object CycleRepeatMode : QuranUiAction

    /** Skips audio playback to the next verse in the active queue. */
    data object SkipToNextVerse : QuranUiAction

    /** Skips audio playback to the previous verse in the active queue. */
    data object SkipToPreviousVerse : QuranUiAction
}

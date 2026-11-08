package com.uncaan.mengaji.feature.quran.presentation

import com.uncaan.mengaji.core.audio.AudioPlayer
import com.uncaan.mengaji.core.audio.AudioRepeatMode
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.core.audio.AudioTrack
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.ReaderMode
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetBookmarksUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahListUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.ToggleBookmarkUseCase
import com.uncaan.mengaji.feature.quran.presentation.model.CatalogTab
import com.uncaan.mengaji.feature.quran.presentation.model.QuickJumpTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeCatalogRepository : QuranRepository {
    var surahListToReturn: List<SurahSummary> = listOf(
        SurahSummary(
            number = 1,
            name = "ٱلْفَاتِحَة",
            englishName = "Al-Fatihah",
            englishNameTranslation = "The Opening",
            numberOfAyahs = 7,
            revelationType = RevelationType.MECCAN
        ),
        SurahSummary(
            number = 2,
            name = "ٱلْبَقَرَة",
            englishName = "Al-Baqarah",
            englishNameTranslation = "The Cow",
            numberOfAyahs = 286,
            revelationType = RevelationType.MEDINAN
        ),
        SurahSummary(
            number = 18,
            name = "ٱلْكَهْف",
            englishName = "Al-Kahf",
            englishNameTranslation = "The Cave",
            numberOfAyahs = 110,
            revelationType = RevelationType.MECCAN
        )
    )

    var lastReadToReturn: LastReadProgress? = LastReadProgress(
        surahNumber = 2,
        surahNameEnglish = "Al-Baqarah",
        surahNameArabic = "سُورَةُ البَقَرَةِ",
        ayahNumberInSurah = 255,
        pageNumber = 42,
        juzNumber = 3,
        preferredReaderMode = ReaderMode.VERSE_LIST,
        updatedAtEpochMillis = 1000L
    )

    var bookmarksToReturn: MutableList<AyahBookmark> = mutableListOf(
        AyahBookmark(
            globalAyahNumber = 262,
            surahNumber = 2,
            surahNameEnglish = "Al-Baqarah",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            arabicSnippet = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
            savedAtEpochMillis = 2000L
        )
    )

    var lastRequestedReference: String? = null

    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AppResult<Ayah> {
        lastRequestedReference = reference
        return AppResult.Success(
            Ayah(
                number = 262,
                text = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
                translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of existence.",
                audioUrl = "https://example.com/audio.mp3",
                surahNumber = 2,
                surahName = "Al-Baqarah",
                surahArabicName = "سورة البقرة",
                englishNameTranslation = "The Cow",
                numberInSurah = 255,
                juz = 3
            )
        )
    }

    override suspend fun getSurahList(forceRefresh: Boolean): AppResult<List<SurahSummary>> {
        return AppResult.Success(surahListToReturn)
    }

    override suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String,
        audioEdition: String
    ): AppResult<SurahDetail> {
        return AppResult.Error(Exception("Not implemented"))
    }

    override suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String
    ): AppResult<MushafPage> {
        return AppResult.Error(Exception("Not implemented"))
    }

    override suspend fun getLastReadProgress(): LastReadProgress? {
        return lastReadToReturn
    }

    override suspend fun saveLastReadProgress(progress: LastReadProgress) {
        lastReadToReturn = progress
    }

    override suspend fun getBookmarks(): List<AyahBookmark> {
        return bookmarksToReturn.toList()
    }

    override suspend fun toggleBookmark(bookmark: AyahBookmark): Boolean {
        val existing = bookmarksToReturn.find { it.globalAyahNumber == bookmark.globalAyahNumber }
        return if (existing != null) {
            bookmarksToReturn.remove(existing)
            false
        } else {
            bookmarksToReturn.add(bookmark)
            true
        }
    }
}

private class FakeAudioPlayerStub : AudioPlayer {
    private val _audioState = MutableStateFlow(AudioState.IDLE)
    override val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    override fun play(url: String) {}
    override fun playQueue(tracks: List<AudioTrack>, startIndex: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun stop() {}
    override fun skipToNext() {}
    override fun skipToPrevious() {}
    override fun setPlaybackSpeed(speed: Float) {}
    override fun setRepeatMode(mode: AudioRepeatMode) {}
    override fun release() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class QuranViewModelCatalogTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeCatalogRepository
    private lateinit var fakeAudioPlayer: FakeAudioPlayerStub
    private lateinit var viewModel: QuranViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeCatalogRepository()
        fakeAudioPlayer = FakeAudioPlayerStub()

        viewModel = QuranViewModel(
            getAyahUseCase = GetAyahUseCase(fakeRepository),
            audioPlayer = fakeAudioPlayer,
            getSurahListUseCase = GetSurahListUseCase(fakeRepository),
            getBookmarksUseCase = GetBookmarksUseCase(fakeRepository),
            getLastReadUseCase = GetLastReadUseCase(fakeRepository),
            toggleBookmarkUseCase = ToggleBookmarkUseCase(fakeRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsSurahsBookmarksAndLastRead() = runTest(testDispatcher) {
        advanceUntilIdle()

        val state = viewModel.catalogState.value
        assertEquals(3, state.surahs.size)
        assertEquals(3, state.filteredSurahs.size)
        assertEquals(30, state.juzs.size)
        assertEquals(1, state.bookmarks.size)
        assertNotNull(state.lastReadProgress)
        assertEquals("Al-Baqarah", state.lastReadProgress.surahNameEnglish)
        assertFalse(state.isCatalogLoading)
        assertNull(state.catalogError)
    }

    @Test
    fun updateCatalogSearch_filtersSurahsByEnglishName() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.UpdateCatalogSearch("Kahf"))

        val state = viewModel.catalogState.value
        assertEquals("Kahf", state.searchQuery)
        assertEquals(1, state.filteredSurahs.size)
        assertEquals("Al-Kahf", state.filteredSurahs.first().englishName)
    }

    @Test
    fun updateCatalogSearch_filtersSurahsByTranslation() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.UpdateCatalogSearch("Opening"))

        val state = viewModel.catalogState.value
        assertEquals(1, state.filteredSurahs.size)
        assertEquals("Al-Fatihah", state.filteredSurahs.first().englishName)
    }

    @Test
    fun updateCatalogSearch_filtersSurahsByNumber() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.UpdateCatalogSearch("18"))

        val state = viewModel.catalogState.value
        assertEquals(1, state.filteredSurahs.size)
        assertEquals(18, state.filteredSurahs.first().number)
    }

    @Test
    fun updateCatalogSearch_detectsDirectSurahAyahPattern() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.UpdateCatalogSearch("2:255"))

        val state = viewModel.catalogState.value
        assertNotNull(state.directJumpTarget)
        assertEquals(2, state.directJumpTarget.first)
        assertEquals(255, state.directJumpTarget.second)
    }

    @Test
    fun selectCatalogTab_switchesActiveTab() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.SelectCatalogTab(CatalogTab.JUZ))
        assertEquals(CatalogTab.JUZ, viewModel.catalogState.value.activeTab)

        viewModel.onAction(QuranUiAction.SelectCatalogTab(CatalogTab.BOOKMARKS))
        assertEquals(CatalogTab.BOOKMARKS, viewModel.catalogState.value.activeTab)
    }

    @Test
    fun quickJumpSheet_togglesVisibilityAndTabs() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.SetQuickJumpSheetVisible(true))
        assertTrue(viewModel.catalogState.value.isQuickJumpSheetVisible)

        viewModel.onAction(QuranUiAction.SelectQuickJumpTab(QuickJumpTab.MUSHAF_PAGE))
        assertEquals(QuickJumpTab.MUSHAF_PAGE, viewModel.catalogState.value.activeQuickJumpTab)

        viewModel.onAction(QuranUiAction.SetQuickJumpSheetVisible(false))
        assertFalse(viewModel.catalogState.value.isQuickJumpSheetVisible)
    }

    @Test
    fun jumpToSurah_dismissesSheetAndSearchesAyah() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.SetQuickJumpSheetVisible(true))
        viewModel.onAction(QuranUiAction.JumpToSurah(surahNumber = 18, ayahNumber = 10))
        advanceUntilIdle()

        assertFalse(viewModel.catalogState.value.isQuickJumpSheetVisible)
        assertEquals("18:10", fakeRepository.lastRequestedReference)
        assertTrue(viewModel.uiState.value is QuranUiState.Success)
    }

    @Test
    fun jumpToJuz_resolvesStartingAyahAndSearches() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.JumpToJuz(2))
        advanceUntilIdle()

        // Juz 2 starts at Al-Baqarah (2) Ayah 142
        assertEquals("2:142", fakeRepository.lastRequestedReference)
    }

    @Test
    fun resumeLastRead_triggersSearchForRecordedAyah() = runTest(testDispatcher) {
        advanceUntilIdle()

        val progress = viewModel.catalogState.value.lastReadProgress
        assertNotNull(progress)

        viewModel.onAction(QuranUiAction.ResumeLastRead(progress))
        advanceUntilIdle()

        assertEquals("2:255", fakeRepository.lastRequestedReference)
    }

    @Test
    fun removeBookmark_deletesFromRepositoryAndUpdatesState() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertEquals(1, viewModel.catalogState.value.bookmarks.size)

        val bookmark = viewModel.catalogState.value.bookmarks.first()
        viewModel.onAction(QuranUiAction.RemoveBookmark(bookmark))
        advanceUntilIdle()

        assertEquals(0, viewModel.catalogState.value.bookmarks.size)
    }

    @Test
    fun backToCatalog_resetsUiStateToInitial() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.searchAyah("2:255")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is QuranUiState.Success)

        viewModel.onAction(QuranUiAction.BackToCatalog)
        assertEquals(QuranUiState.Initial, viewModel.uiState.value)
    }
}


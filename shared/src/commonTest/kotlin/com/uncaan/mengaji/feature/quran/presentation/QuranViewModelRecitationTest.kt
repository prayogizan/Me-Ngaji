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
import com.uncaan.mengaji.feature.quran.domain.usecase.GetMushafPageUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahDetailUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahListUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.SaveLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.Dispatchers
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

/**
 * In-memory repository fake tailored for recitation presentation testing.
 */
private class FakeRecitationRepository : QuranRepository {
    var surahsList: List<SurahSummary> = listOf(
        SurahSummary(
            number = 1,
            name = "سُورَةُ ٱلْفَاتِحَةِ",
            englishName = "Al-Faatiha",
            englishNameTranslation = "The Opening",
            numberOfAyahs = 7,
            revelationType = RevelationType.MECCAN
        ),
        SurahSummary(
            number = 2,
            name = "سُورَةُ البَقَرَةِ",
            englishName = "Al-Baqara",
            englishNameTranslation = "The Cow",
            numberOfAyahs = 286,
            revelationType = RevelationType.MEDINAN
        ),
        SurahSummary(
            number = 114,
            name = "سُورَةُ النَّاسِ",
            englishName = "An-Naas",
            englishNameTranslation = "Mankind",
            numberOfAyahs = 6,
            revelationType = RevelationType.MECCAN
        )
    )

    var lastRead: LastReadProgress? = LastReadProgress(
        surahNumber = 1,
        surahNameEnglish = "Al-Faatiha",
        surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
        ayahNumberInSurah = 1,
        pageNumber = 1,
        juzNumber = 1,
        preferredReaderMode = ReaderMode.VERSE_LIST,
        updatedAtEpochMillis = 1730000000000L
    )

    val bookmarks: MutableList<AyahBookmark> = mutableListOf(
        AyahBookmark(
            globalAyahNumber = 1,
            surahNumber = 1,
            surahNameEnglish = "Al-Faatiha",
            surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
            ayahNumberInSurah = 1,
            pageNumber = 1,
            arabicSnippet = "بِسْمِ ٱللَّهِ",
            savedAtEpochMillis = 1730000000000L
        )
    )

    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AppResult<Ayah> = AppResult.Success(
        Ayah(
            number = 1,
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            translation = "In the name of Allah",
            audioUrl = "https://example.com/1.mp3",
            surahNumber = 1,
            surahName = "Al-Faatiha",
            numberInSurah = 1,
            juz = 1,
            page = 1,
            hizbQuarter = 1
        )
    )

    override suspend fun getSurahList(forceRefresh: Boolean): AppResult<List<SurahSummary>> =
        AppResult.Success(surahsList)

    override suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String,
        audioEdition: String
    ): AppResult<SurahDetail> {
        val summary = surahsList.firstOrNull { it.number == surahNumber }
            ?: surahsList.first()
        val ayahs = (1..summary.numberOfAyahs).map { numInSurah ->
            Ayah(
                number = numInSurah,
                text = "Arabic text $numInSurah",
                translation = "Translation text $numInSurah",
                audioUrl = "https://example.com/audio/$numInSurah.mp3",
                surahNumber = surahNumber,
                surahName = summary.englishName,
                surahArabicName = summary.name,
                englishNameTranslation = summary.englishNameTranslation,
                numberInSurah = numInSurah,
                juz = 1,
                page = 1,
                hizbQuarter = 1
            )
        }
        return AppResult.Success(
            SurahDetail(
                number = summary.number,
                name = summary.name,
                englishName = summary.englishName,
                englishNameTranslation = summary.englishNameTranslation,
                revelationType = summary.revelationType,
                numberOfAyahs = summary.numberOfAyahs,
                ayahs = ayahs,
                translationEdition = translationEdition,
                audioEdition = audioEdition
            )
        )
    }

    override suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String
    ): AppResult<MushafPage> = AppResult.Success(
        MushafPage(
            pageNumber = pageNumber,
            ayahs = listOf(
                Ayah(
                    number = 1,
                    text = "Page $pageNumber Arabic text",
                    translation = "Page $pageNumber translation",
                    audioUrl = null,
                    surahNumber = 1,
                    surahName = "Al-Faatiha",
                    surahArabicName = "سُورَةُ ٱلْفَاتِحَةِ",
                    englishNameTranslation = "The Opening",
                    numberInSurah = 1,
                    juz = 1,
                    page = pageNumber,
                    hizbQuarter = 1
                )
            ),
            surahs = listOf(surahsList.first()),
            juzNumber = 1,
            hizbQuarter = 1,
            translationEdition = translationEdition
        )
    )

    override suspend fun getLastReadProgress(): LastReadProgress? = lastRead

    override suspend fun saveLastReadProgress(progress: LastReadProgress) {
        lastRead = progress
    }

    override suspend fun getBookmarks(): List<AyahBookmark> = bookmarks.toList()

    override suspend fun toggleBookmark(bookmark: AyahBookmark): Boolean {
        val index = bookmarks.indexOfFirst { it.globalAyahNumber == bookmark.globalAyahNumber }
        return if (index >= 0) {
            bookmarks.removeAt(index)
            false
        } else {
            bookmarks.add(bookmark)
            true
        }
    }
}

/**
 * In-memory audio player fake tailored for recitation presentation testing.
 */
private class FakeRecitationAudioPlayer : AudioPlayer {
    private val _audioState = MutableStateFlow(AudioState.IDLE)
    override val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    var lastPlayQueueTracks: List<AudioTrack>? = null
    var lastPlayQueueStartIndex: Int? = null
    var stopCallCount: Int = 0

    fun emitState(state: AudioState) {
        _audioState.value = state
    }

    override fun play(url: String) {
        _audioState.value = AudioState(isPlaying = true, currentUrl = url)
    }

    override fun playQueue(tracks: List<AudioTrack>, startIndex: Int) {
        lastPlayQueueTracks = tracks
        lastPlayQueueStartIndex = startIndex
        val track = tracks.getOrNull(startIndex)
        _audioState.value = AudioState(
            isPlaying = true,
            currentUrl = track?.url,
            currentSurahNumber = track?.surahNumber,
            currentAyahNumberInSurah = track?.ayahNumberInSurah,
            currentGlobalAyahNumber = track?.globalAyahNumber
        )
    }

    override fun pause() {
        _audioState.value = _audioState.value.copy(isPlaying = false)
    }

    override fun resume() {
        _audioState.value = _audioState.value.copy(isPlaying = true)
    }

    override fun stop() {
        stopCallCount++
        _audioState.value = AudioState.IDLE
    }

    override fun skipToNext() {}
    override fun skipToPrevious() {}
    override fun setPlaybackSpeed(speed: Float) {}
    override fun setRepeatMode(mode: AudioRepeatMode) {}
    override fun release() {}
}

/**
 * Comprehensive automated unit test suite for Quran recitation presentation flows (CMP-608).
 *
 * Verifies:
 * - Catalog loading & initialization
 * - Search filtering & direct `surah:ayah` parsing
 * - `OpenSurah` deep linking with verse highlighting
 * - Dual-mode reader switching with coordinate preservation
 * - Continuous audio queue playback
 * - Ayah bookmarking & Last Read resumption
 * - In-screen back navigation & audio cancellation
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class QuranViewModelRecitationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeRecitationRepository
    private lateinit var fakeAudioPlayer: FakeRecitationAudioPlayer
    private lateinit var viewModel: QuranViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeRecitationRepository()
        fakeAudioPlayer = FakeRecitationAudioPlayer()

        viewModel = QuranViewModel(
            getAyahUseCase = GetAyahUseCase(fakeRepository),
            audioPlayer = fakeAudioPlayer,
            getSurahListUseCase = GetSurahListUseCase(fakeRepository),
            getBookmarksUseCase = GetBookmarksUseCase(fakeRepository),
            getLastReadUseCase = GetLastReadUseCase(fakeRepository),
            toggleBookmarkUseCase = ToggleBookmarkUseCase(fakeRepository),
            getSurahDetailUseCase = GetSurahDetailUseCase(fakeRepository),
            getMushafPageUseCase = GetMushafPageUseCase(fakeRepository),
            saveLastReadUseCase = SaveLastReadUseCase(fakeRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsSurahsBookmarksAndLastRead() = runTest(testDispatcher) {
        advanceUntilIdle()

        val catalog = viewModel.catalogState.value
        assertEquals(3, catalog.surahs.size)
        assertEquals(3, catalog.filteredSurahs.size)
        assertEquals(1, catalog.bookmarks.size)
        val progress = catalog.lastReadProgress
        assertNotNull(progress)
        assertEquals("Al-Faatiha", progress.surahNameEnglish)
        assertFalse(catalog.isCatalogLoading)
        assertNull(catalog.catalogError)
    }

    @Test
    fun searchFiltering_filtersByEnglishNameAndSurahNumber() = runTest(testDispatcher) {
        advanceUntilIdle()

        // Filter by English name
        viewModel.onAction(QuranUiAction.UpdateCatalogSearch("Baqara"))
        advanceUntilIdle()
        val baqaraFiltered = viewModel.catalogState.value.filteredSurahs
        assertEquals(1, baqaraFiltered.size)
        assertEquals("Al-Baqara", baqaraFiltered.first().englishName)

        // Filter by number
        viewModel.onAction(QuranUiAction.UpdateCatalogSearch("114"))
        advanceUntilIdle()
        val numFiltered = viewModel.catalogState.value.filteredSurahs
        assertEquals(1, numFiltered.size)
        assertEquals("An-Naas", numFiltered.first().englishName)

        // Clear query resets filtered list
        viewModel.onAction(QuranUiAction.UpdateCatalogSearch(""))
        advanceUntilIdle()
        assertEquals(3, viewModel.catalogState.value.filteredSurahs.size)
    }

    @Test
    fun directAyahLookup_matchesPatternAndSetsJumpTarget() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.UpdateCatalogSearch("2:255"))
        advanceUntilIdle()

        val jumpTarget = viewModel.catalogState.value.directJumpTarget
        assertNotNull(jumpTarget)
        assertEquals(2, jumpTarget.first)
        assertEquals(255, jumpTarget.second)
    }

    @Test
    fun openSurah_withScrollToAyah_activatesReaderAndSetsHighlight() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.OpenSurah(surahNumber = 2, scrollToAyah = 255))
        advanceUntilIdle()

        val reader = viewModel.readerState.value
        assertTrue(reader.isReaderActive)
        assertEquals(ReaderMode.VERSE_LIST, reader.readerMode)
        assertEquals(2, reader.surahDetail?.number)
        assertEquals(255, reader.highlightedAyahNumber)
    }

    @Test
    fun switchReaderMode_preservesCoordinatesBidirectionally() = runTest(testDispatcher) {
        advanceUntilIdle()

        // Open in Verse List mode
        viewModel.onAction(QuranUiAction.OpenSurah(surahNumber = 1, scrollToAyah = 1))
        advanceUntilIdle()
        assertEquals(ReaderMode.VERSE_LIST, viewModel.readerState.value.readerMode)

        // Switch to Mushaf Page mode
        viewModel.onAction(QuranUiAction.SwitchReaderMode(ReaderMode.MUSHAF_PAGE))
        advanceUntilIdle()
        val mushafState = viewModel.readerState.value
        assertEquals(ReaderMode.MUSHAF_PAGE, mushafState.readerMode)
        val page = mushafState.mushafPage
        assertNotNull(page)
        assertEquals(1, page.pageNumber)

        // Switch back to Verse List mode
        viewModel.onAction(QuranUiAction.SwitchReaderMode(ReaderMode.VERSE_LIST))
        advanceUntilIdle()
        val verseListState = viewModel.readerState.value
        assertEquals(ReaderMode.VERSE_LIST, verseListState.readerMode)
        assertEquals(1, verseListState.surahDetail?.number)
    }

    @Test
    fun playAyahQueue_dispatchesToAudioPlayerAndUpdatesActiveRecitingAyah() = runTest(testDispatcher) {
        advanceUntilIdle()

        // Open Surah 1 (7 verses)
        viewModel.onAction(QuranUiAction.OpenSurah(surahNumber = 1))
        advanceUntilIdle()

        val ayahToPlay = viewModel.readerState.value.surahDetail?.ayahs?.getOrNull(2)
        assertNotNull(ayahToPlay)

        // Play from Ayah 3
        viewModel.onAction(QuranUiAction.PlayFromAyah(ayahToPlay))
        advanceUntilIdle()

        val tracks = fakeAudioPlayer.lastPlayQueueTracks
        assertNotNull(tracks)
        assertEquals(7, tracks.size) // complete Surah tracks queued
        assertEquals(2, fakeAudioPlayer.lastPlayQueueStartIndex) // 0-indexed position of Ayah 3 is 2

        // Simulate audio engine progressing to verse 4
        fakeAudioPlayer.emitState(
            AudioState(
                isPlaying = true,
                currentSurahNumber = 1,
                currentAyahNumberInSurah = 4,
                currentGlobalAyahNumber = 4
            )
        )
        advanceUntilIdle()

        assertEquals(4, viewModel.readerState.value.currentAyahNumber)
    }

    @Test
    fun toggleBookmark_updatesBookmarksListReactively() = runTest(testDispatcher) {
        advanceUntilIdle()

        val ayah = Ayah(
            number = 262,
            text = "اللَّهُ لَا إِلَٰهَ",
            translation = "Allah - there is no deity except Him",
            audioUrl = null,
            surahNumber = 2,
            surahName = "Al-Baqara",
            surahArabicName = "سُورَةُ البَقَرَةِ",
            englishNameTranslation = "The Cow",
            numberInSurah = 255,
            juz = 3,
            page = 42,
            hizbQuarter = 17
        )

        // Add bookmark
        viewModel.onAction(QuranUiAction.ToggleBookmarkForAyah(ayah))
        advanceUntilIdle()

        val bookmarks = viewModel.catalogState.value.bookmarks
        assertEquals(2, bookmarks.size)
        assertTrue(bookmarks.any { it.globalAyahNumber == 262 })

        // Toggle bookmark off
        viewModel.onAction(QuranUiAction.ToggleBookmarkForAyah(ayah))
        advanceUntilIdle()

        val bookmarksAfterRemove = viewModel.catalogState.value.bookmarks
        assertEquals(1, bookmarksAfterRemove.size)
        assertFalse(bookmarksAfterRemove.any { it.globalAyahNumber == 262 })
    }

    @Test
    fun resumeLastRead_activatesReaderInSavedModeAndCoordinates() = runTest(testDispatcher) {
        advanceUntilIdle()

        val progress = LastReadProgress(
            surahNumber = 2,
            surahNameEnglish = "Al-Baqara",
            surahNameArabic = "سُورَةُ البَقَرَةِ",
            ayahNumberInSurah = 255,
            pageNumber = 42,
            juzNumber = 3,
            preferredReaderMode = ReaderMode.VERSE_LIST,
            updatedAtEpochMillis = 1730000000000L
        )

        viewModel.onAction(QuranUiAction.ResumeLastRead(progress))
        advanceUntilIdle()

        val reader = viewModel.readerState.value
        assertTrue(reader.isReaderActive)
        assertEquals(ReaderMode.VERSE_LIST, reader.readerMode)
        assertEquals(2, reader.surahDetail?.number)
        assertEquals(255, reader.highlightedAyahNumber)
    }

    @Test
    fun closeReaderToCatalog_resetsReaderAndStopsAudio() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.OpenSurah(surahNumber = 1, scrollToAyah = 3))
        advanceUntilIdle()
        assertTrue(viewModel.readerState.value.isReaderActive)

        viewModel.onAction(QuranUiAction.CloseReaderToCatalog)
        advanceUntilIdle()

        val reader = viewModel.readerState.value
        assertFalse(reader.isReaderActive)
        assertNull(reader.highlightedAyahNumber)
        assertTrue(fakeAudioPlayer.stopCallCount >= 1)
    }
}

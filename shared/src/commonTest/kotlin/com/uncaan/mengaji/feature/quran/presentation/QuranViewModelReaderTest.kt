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
import com.uncaan.mengaji.feature.quran.presentation.component.stripLeadingBismillah
import com.uncaan.mengaji.feature.quran.presentation.component.toArabicIndic
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

private class FakeReaderRepository : QuranRepository {
    var lastRequestedReference: String? = null
    var lastRequestedSurahNumber: Int? = null
    var lastRequestedPageNumber: Int? = null
    var savedProgress: LastReadProgress? = null
    var toggledBookmark: AyahBookmark? = null

    val sampleAyahs = listOf(
        Ayah(
            number = 1,
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
            audioUrl = "https://example.com/audio/1.mp3",
            surahNumber = 1,
            surahName = "Al-Fatihah",
            surahArabicName = "ٱلْفَاتِحَة",
            englishNameTranslation = "The Opening",
            numberInSurah = 1,
            juz = 1,
            page = 1
        ),
        Ayah(
            number = 2,
            text = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
            translation = "[All] praise is [due] to Allah, Lord of the worlds.",
            audioUrl = "https://example.com/audio/2.mp3",
            surahNumber = 1,
            surahName = "Al-Fatihah",
            surahArabicName = "ٱلْفَاتِحَة",
            englishNameTranslation = "The Opening",
            numberInSurah = 2,
            juz = 1,
            page = 1
        ),
        Ayah(
            number = 262,
            text = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
            translation = "Allah! There is no deity except Him, the Ever-Living, the Sustainer of existence.",
            audioUrl = "https://example.com/audio/262.mp3",
            surahNumber = 2,
            surahName = "Al-Baqarah",
            surahArabicName = "ٱلْبَقَرَة",
            englishNameTranslation = "The Cow",
            numberInSurah = 255,
            juz = 3,
            page = 42
        )
    )

    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AppResult<Ayah> {
        lastRequestedReference = reference
        return AppResult.Success(sampleAyahs.first())
    }

    override suspend fun getSurahList(forceRefresh: Boolean): AppResult<List<SurahSummary>> {
        return AppResult.Success(emptyList())
    }

    override suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String,
        audioEdition: String
    ): AppResult<SurahDetail> {
        lastRequestedSurahNumber = surahNumber
        val filtered = sampleAyahs.filter { it.surahNumber == surahNumber }.ifEmpty { sampleAyahs }
        return AppResult.Success(
            SurahDetail(
                number = surahNumber,
                name = if (surahNumber == 2) "سُورَةُ ٱلْبَقَرَةِ" else "سُورَةُ ٱلْفَاتِحَةِ",
                englishName = if (surahNumber == 2) "Al-Baqarah" else "Al-Fatihah",
                englishNameTranslation = if (surahNumber == 2) "The Cow" else "The Opening",
                revelationType = if (surahNumber == 2) RevelationType.MEDINAN else RevelationType.MECCAN,
                numberOfAyahs = filtered.size,
                ayahs = filtered
            )
        )
    }

    override suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String
    ): AppResult<MushafPage> {
        lastRequestedPageNumber = pageNumber
        val pageAyahs = sampleAyahs.filter { it.page == pageNumber }.ifEmpty { sampleAyahs }
        return AppResult.Success(
            MushafPage(
                pageNumber = pageNumber,
                ayahs = pageAyahs,
                surahs = listOf(
                    SurahSummary(
                        number = 1,
                        name = "ٱلْفَاتِحَة",
                        englishName = "Al-Fatihah",
                        englishNameTranslation = "The Opening",
                        numberOfAyahs = 7,
                        revelationType = RevelationType.MECCAN
                    )
                ),
                juzNumber = pageAyahs.firstOrNull()?.juz ?: 1,
                hizbQuarter = 1
            )
        )
    }

    override suspend fun getLastReadProgress(): LastReadProgress? = savedProgress

    override suspend fun saveLastReadProgress(progress: LastReadProgress) {
        savedProgress = progress
    }

    override suspend fun getBookmarks(): List<AyahBookmark> = emptyList()

    override suspend fun toggleBookmark(bookmark: AyahBookmark): Boolean {
        toggledBookmark = bookmark
        return true
    }
}

private class FakeReaderAudioPlayer : AudioPlayer {
    val stateFlow = MutableStateFlow(AudioState.IDLE)
    override val audioState: StateFlow<AudioState> = stateFlow.asStateFlow()

    var lastPlayedUrl: String? = null
    var lastQueueTracks: List<AudioTrack>? = null
    var lastQueueStartIndex: Int? = null
    var pauseCount: Int = 0
    var resumeCount: Int = 0
    var stopCount: Int = 0
    var nextCount: Int = 0
    var prevCount: Int = 0
    var speedConfigured: Float? = null
    var repeatModeConfigured: AudioRepeatMode? = null

    override fun play(url: String) {
        lastPlayedUrl = url
        stateFlow.value = AudioState(isPlaying = true, currentUrl = url)
    }

    override fun playQueue(tracks: List<AudioTrack>, startIndex: Int) {
        lastQueueTracks = tracks
        lastQueueStartIndex = startIndex
        val track = tracks.getOrNull(startIndex)
        stateFlow.value = AudioState(
            isPlaying = true,
            currentUrl = track?.url,
            currentSurahNumber = track?.surahNumber,
            currentAyahNumberInSurah = track?.ayahNumberInSurah,
            currentGlobalAyahNumber = track?.globalAyahNumber
        )
    }

    override fun pause() {
        pauseCount++
        stateFlow.value = stateFlow.value.copy(isPlaying = false)
    }

    override fun resume() {
        resumeCount++
        stateFlow.value = stateFlow.value.copy(isPlaying = true)
    }

    override fun stop() {
        stopCount++
        stateFlow.value = AudioState.IDLE
    }

    override fun skipToNext() {
        nextCount++
    }

    override fun skipToPrevious() {
        prevCount++
    }

    override fun setPlaybackSpeed(speed: Float) {
        speedConfigured = speed
        stateFlow.value = stateFlow.value.copy(playbackSpeed = speed)
    }

    override fun setRepeatMode(mode: AudioRepeatMode) {
        repeatModeConfigured = mode
        stateFlow.value = stateFlow.value.copy(repeatMode = mode)
    }

    override fun release() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class QuranViewModelReaderTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeReaderRepository
    private lateinit var fakeAudioPlayer: FakeReaderAudioPlayer
    private lateinit var viewModel: QuranViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeReaderRepository()
        fakeAudioPlayer = FakeReaderAudioPlayer()

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
    fun openReader_loadsSurahDetailAndActivatesVerseListMode() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.OpenReader(surahNumber = 1, ayahNumber = 2))
        advanceUntilIdle()

        val readerState = viewModel.readerState.value
        assertTrue(readerState.isReaderActive)
        assertEquals(ReaderMode.VERSE_LIST, readerState.readerMode)
        assertEquals(1, readerState.currentSurahNumber)
        assertEquals(2, readerState.currentAyahNumber)
        assertNotNull(readerState.surahDetail)
        assertEquals("Al-Fatihah", readerState.surahDetail.englishName)
        assertEquals(1, fakeRepository.lastRequestedSurahNumber)
    }

    @Test
    fun switchReaderMode_fromVerseListToMushafPage_preservesPageCoordinate() = runTest(testDispatcher) {
        advanceUntilIdle()

        // Open Surah 2 at Ayah 255 (which resides on Mushaf Page 42)
        viewModel.onAction(QuranUiAction.OpenReader(surahNumber = 2, ayahNumber = 255))
        advanceUntilIdle()

        // Switch to MUSHAF_PAGE mode
        viewModel.onAction(QuranUiAction.SwitchReaderMode(ReaderMode.MUSHAF_PAGE))
        advanceUntilIdle()

        val readerState = viewModel.readerState.value
        assertEquals(ReaderMode.MUSHAF_PAGE, readerState.readerMode)
        assertEquals(42, readerState.currentPageNumber)
        assertEquals(42, fakeRepository.lastRequestedPageNumber)
    }

    @Test
    fun switchReaderMode_fromMushafPageToVerseList_preservesSurahAndAyah() = runTest(testDispatcher) {
        advanceUntilIdle()

        // Start on Mushaf Page 1
        viewModel.onAction(QuranUiAction.LoadMushafPage(1))
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.SwitchReaderMode(ReaderMode.VERSE_LIST))
        advanceUntilIdle()

        val readerState = viewModel.readerState.value
        assertEquals(ReaderMode.VERSE_LIST, readerState.readerMode)
        assertEquals(1, readerState.currentSurahNumber)
        assertEquals(1, readerState.currentAyahNumber)
    }

    @Test
    fun loadMushafPage_loadsRequestedPageAndUpdatesCoordinates() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.LoadMushafPage(2))
        advanceUntilIdle()

        val readerState = viewModel.readerState.value
        assertEquals(2, readerState.currentPageNumber)
        assertNotNull(readerState.mushafPage)
        assertEquals(2, fakeRepository.lastRequestedPageNumber)
    }

    @Test
    fun toggleKhushuMode_togglesImmersiveState() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertFalse(viewModel.readerState.value.isKhushuMode)

        viewModel.onAction(QuranUiAction.ToggleKhushuMode)
        assertTrue(viewModel.readerState.value.isKhushuMode)

        viewModel.onAction(QuranUiAction.ToggleKhushuMode)
        assertFalse(viewModel.readerState.value.isKhushuMode)
    }

    @Test
    fun showAndDismissAyahActionSheet_updatesSelectedAyah() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertNull(viewModel.readerState.value.actionSheetAyah)

        val targetAyah = fakeRepository.sampleAyahs.first()
        viewModel.onAction(QuranUiAction.ShowAyahActionSheet(targetAyah))
        assertEquals(targetAyah, viewModel.readerState.value.actionSheetAyah)

        viewModel.onAction(QuranUiAction.DismissAyahActionSheet)
        assertNull(viewModel.readerState.value.actionSheetAyah)
    }

    @Test
    fun playFromAyah_constructsAudioQueueAndStartsRecitation() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.OpenReader(surahNumber = 1, ayahNumber = 1))
        advanceUntilIdle()

        val startingAyah = fakeRepository.sampleAyahs[1] // Ayah 2
        viewModel.onAction(QuranUiAction.PlayFromAyah(startingAyah))
        advanceUntilIdle()

        assertNotNull(fakeAudioPlayer.lastQueueTracks)
        assertEquals(1, fakeAudioPlayer.lastQueueStartIndex) // Ayah 2 is index 1
        assertTrue(viewModel.audioState.value.isPlaying)
    }

    @Test
    fun loopAyah_configuresRepeatAyahAndPlaysTrack() = runTest(testDispatcher) {
        advanceUntilIdle()

        val targetAyah = fakeRepository.sampleAyahs.first()
        viewModel.onAction(QuranUiAction.LoopAyah(targetAyah))
        advanceUntilIdle()

        assertEquals(AudioRepeatMode.REPEAT_AYAH, fakeAudioPlayer.repeatModeConfigured)
        assertNotNull(fakeAudioPlayer.lastQueueTracks)
        assertEquals(1, fakeAudioPlayer.lastQueueTracks?.size)
    }

    @Test
    fun saveManualLastRead_delegatesToSaveLastReadUseCase() = runTest(testDispatcher) {
        advanceUntilIdle()

        val ayah = fakeRepository.sampleAyahs[2] // Ayah 2:255 (page 42, juz 3)
        viewModel.onAction(QuranUiAction.SaveManualLastRead(ayah))
        advanceUntilIdle()

        assertNotNull(fakeRepository.savedProgress)
        assertEquals(2, fakeRepository.savedProgress?.surahNumber)
        assertEquals(255, fakeRepository.savedProgress?.ayahNumberInSurah)
        assertEquals(42, fakeRepository.savedProgress?.pageNumber)
        assertEquals(3, fakeRepository.savedProgress?.juzNumber)
    }

    @Test
    fun toggleBookmarkForAyah_delegatesToToggleBookmarkUseCase() = runTest(testDispatcher) {
        advanceUntilIdle()

        val ayah = fakeRepository.sampleAyahs.first()
        viewModel.onAction(QuranUiAction.ToggleBookmarkForAyah(ayah))
        advanceUntilIdle()

        assertNotNull(fakeRepository.toggledBookmark)
        assertEquals(ayah.number, fakeRepository.toggledBookmark?.globalAyahNumber)
    }

    @Test
    fun audioStateTransition_synchronizesActiveAyahInReader() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.OpenReader(surahNumber = 1, ayahNumber = 1))
        advanceUntilIdle()

        // Simulate audio engine advancing to verse 2 of Surah 1
        fakeAudioPlayer.stateFlow.value = AudioState(
            isPlaying = true,
            currentSurahNumber = 1,
            currentAyahNumberInSurah = 2,
            currentGlobalAyahNumber = 2
        )
        advanceUntilIdle()

        val readerState = viewModel.readerState.value
        assertEquals(1, readerState.currentSurahNumber)
        assertEquals(2, readerState.currentAyahNumber)
    }

    @Test
    fun transportControls_speedAndRepeatModes() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.SetPlaybackSpeed(1.25f))
        assertEquals(1.25f, fakeAudioPlayer.speedConfigured)

        viewModel.onAction(QuranUiAction.CycleRepeatMode)
        assertEquals(AudioRepeatMode.REPEAT_AYAH, fakeAudioPlayer.repeatModeConfigured)

        viewModel.onAction(QuranUiAction.SkipToNextVerse)
        assertEquals(1, fakeAudioPlayer.nextCount)

        viewModel.onAction(QuranUiAction.SkipToPreviousVerse)
        assertEquals(1, fakeAudioPlayer.prevCount)
    }

    @Test
    fun backToCatalog_deactivatesReaderAndStopsAudio() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onAction(QuranUiAction.OpenReader(surahNumber = 1, ayahNumber = 1))
        viewModel.onAction(QuranUiAction.ToggleKhushuMode)
        advanceUntilIdle()
        assertTrue(viewModel.readerState.value.isReaderActive)
        assertTrue(viewModel.readerState.value.isKhushuMode)

        viewModel.onAction(QuranUiAction.BackToCatalog)
        advanceUntilIdle()

        assertFalse(viewModel.readerState.value.isReaderActive)
        assertFalse(viewModel.readerState.value.isKhushuMode)
        assertEquals(QuranUiState.Initial, viewModel.uiState.value)
        assertTrue(fakeAudioPlayer.stopCount > 0)
    }

    @Test
    fun bismillahRuleHelpers_enforcesCanonicalPreservation() {
        // Surah 1: Bismillah itself is Ayah 1, should not be stripped
        val fatihahText = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
        assertEquals(fatihahText, stripLeadingBismillah(fatihahText, surahNumber = 1, ayahNumberInSurah = 1))

        // Surah 9: No Bismillah banner or prefix
        val tawbahText = "بَرَاءَةٌ مِّنَ اللَّهِ وَرَسُولِهِ"
        assertEquals(tawbahText, stripLeadingBismillah(tawbahText, surahNumber = 9, ayahNumberInSurah = 1))

        // Surah 2 Ayah 1: Strip leading Bismillah prefix
        val baqarahText = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ الٓمٓ"
        assertEquals("الٓمٓ", stripLeadingBismillah(baqarahText, surahNumber = 2, ayahNumberInSurah = 1))

        // Eastern Arabic-Indic numerals
        assertEquals("١", toArabicIndic(1))
        assertEquals("٢", toArabicIndic(2))
        assertEquals("٢٥٥", toArabicIndic(255))
        assertEquals("٦٠٤", toArabicIndic(604))
    }
}

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

private class FakeAdaptiveQuranRepository : QuranRepository {
    val requestedPages = mutableListOf<Int>()

    val pageOneAyahs = listOf(
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
        )
    )

    val pageTwoAyahs = listOf(
        Ayah(
            number = 8,
            text = "الٓمٓ",
            translation = "Alif, Lam, Meem.",
            audioUrl = "https://example.com/audio/8.mp3",
            surahNumber = 2,
            surahName = "Al-Baqarah",
            surahArabicName = "ٱلْبَقَرَة",
            englishNameTranslation = "The Cow",
            numberInSurah = 1,
            juz = 1,
            page = 2
        )
    )

    override suspend fun getAyah(reference: String, translationEdition: String, recitationEdition: String): AppResult<Ayah> {
        return AppResult.Success(pageOneAyahs.first())
    }

    override suspend fun getSurahList(forceRefresh: Boolean): AppResult<List<SurahSummary>> = AppResult.Success(emptyList())

    override suspend fun getSurahDetail(surahNumber: Int, translationEdition: String, audioEdition: String): AppResult<SurahDetail> {
        return AppResult.Success(
            SurahDetail(
                number = surahNumber,
                name = "Surah $surahNumber",
                englishName = "Surah $surahNumber",
                englishNameTranslation = "Translation",
                revelationType = RevelationType.MECCAN,
                numberOfAyahs = 7,
                ayahs = pageOneAyahs
            )
        )
    }

    override suspend fun getMushafPage(pageNumber: Int, translationEdition: String): AppResult<MushafPage> {
        requestedPages.add(pageNumber)
        val ayahs = if (pageNumber == 1) pageOneAyahs else pageTwoAyahs
        return AppResult.Success(
            MushafPage(
                pageNumber = pageNumber,
                ayahs = ayahs,
                surahs = listOf(
                    SurahSummary(
                        number = ayahs.first().surahNumber,
                        name = ayahs.first().surahArabicName,
                        englishName = ayahs.first().surahName,
                        englishNameTranslation = ayahs.first().englishNameTranslation,
                        numberOfAyahs = ayahs.size,
                        revelationType = RevelationType.MECCAN
                    )
                ),
                juzNumber = 1,
                hizbQuarter = 1
            )
        )
    }

    override suspend fun getLastReadProgress(): LastReadProgress? = null
    override suspend fun saveLastReadProgress(progress: LastReadProgress) {}
    override suspend fun getBookmarks(): List<AyahBookmark> = emptyList()
    override suspend fun toggleBookmark(bookmark: AyahBookmark): Boolean = true
}

private class FakeAdaptiveAudioPlayer : AudioPlayer {
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
class QuranViewModelAdaptiveTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeAdaptiveQuranRepository
    private lateinit var audioPlayer: FakeAdaptiveAudioPlayer
    private lateinit var viewModel: QuranViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeAdaptiveQuranRepository()
        audioPlayer = FakeAdaptiveAudioPlayer()

        viewModel = QuranViewModel(
            getAyahUseCase = GetAyahUseCase(repository),
            audioPlayer = audioPlayer,
            getSurahListUseCase = GetSurahListUseCase(repository),
            getBookmarksUseCase = GetBookmarksUseCase(repository),
            getLastReadUseCase = GetLastReadUseCase(repository),
            toggleBookmarkUseCase = ToggleBookmarkUseCase(repository),
            getSurahDetailUseCase = GetSurahDetailUseCase(repository),
            getMushafPageUseCase = GetMushafPageUseCase(repository),
            saveLastReadUseCase = SaveLastReadUseCase(repository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadMushafSpread_fetchesOddAndEvenPagesConcurrently() = runTest(testDispatcher) {
        viewModel.onAction(QuranUiAction.LoadMushafSpread(spreadIndex = 0))
        advanceUntilIdle()

        val state = viewModel.readerState.value
        assertFalse(state.isReaderLoading)
        assertFalse(state.isFacingPageLoading)
        assertNull(state.readerError)

        assertNotNull(state.mushafPage)
        assertEquals(1, state.mushafPage.pageNumber)

        assertNotNull(state.facingMushafPage)
        assertEquals(2, state.facingMushafPage.pageNumber)

        assertEquals(1, state.currentPageNumber)
        assertTrue(repository.requestedPages.contains(1))
        assertTrue(repository.requestedPages.contains(2))
    }

    @Test
    fun toggleCatalogSidebar_togglesVisibilityFlag() = runTest(testDispatcher) {
        assertTrue(viewModel.readerState.value.isCatalogSidebarVisible)

        viewModel.onAction(QuranUiAction.ToggleCatalogSidebar)
        assertFalse(viewModel.readerState.value.isCatalogSidebarVisible)

        viewModel.onAction(QuranUiAction.ToggleCatalogSidebar)
        assertTrue(viewModel.readerState.value.isCatalogSidebarVisible)
    }

    @Test
    fun selectSpreadAyah_updatesActionSheetAndCoordinates() = runTest(testDispatcher) {
        val testAyah = repository.pageTwoAyahs.first()

        viewModel.onAction(QuranUiAction.SelectSpreadAyah(testAyah, isFacingPage = true))

        val state = viewModel.readerState.value
        assertEquals(testAyah, state.actionSheetAyah)
        assertEquals(testAyah.surahNumber, state.currentSurahNumber)
        assertEquals(testAyah.numberInSurah, state.currentAyahNumber)
    }
}

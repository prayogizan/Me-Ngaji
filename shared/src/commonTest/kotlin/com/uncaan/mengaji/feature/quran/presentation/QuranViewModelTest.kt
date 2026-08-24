package com.uncaan.mengaji.feature.quran.presentation

import com.uncaan.mengaji.core.audio.AudioPlayer
import com.uncaan.mengaji.core.audio.AudioState
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
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
import kotlin.test.assertTrue

private class FakeQuranRepository : QuranRepository {
    var responseToReturn: AppResult<Ayah>? = null
    var lastReferenceReceived: String? = null
    var lastTranslationReceived: String? = null
    var lastRecitationReceived: String? = null

    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AppResult<Ayah> {
        lastReferenceReceived = reference
        lastTranslationReceived = translationEdition
        lastRecitationReceived = recitationEdition
        return responseToReturn ?: AppResult.Success(
            Ayah(
                number = 262,
                text = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
                translation = "Allah! There is no deity except Him, the Ever-Living, the Sustainer of existence.",
                audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3",
                surahNumber = 2,
                surahName = "Al-Baqara",
                surahArabicName = "سورة البقرة",
                englishNameTranslation = "The Cow",
                numberInSurah = 255,
                juz = 3
            )
        )
    }
}

private class FakeAudioPlayer : AudioPlayer {
    private val _audioState = MutableStateFlow<AudioState>(AudioState.Idle)
    override val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    var lastPlayedUrl: String? = null
    var pauseCount: Int = 0
    var resumeCount: Int = 0
    var stopCount: Int = 0

    override fun play(url: String) {
        lastPlayedUrl = url
        _audioState.value = AudioState.Playing
    }

    override fun pause() {
        pauseCount++
        _audioState.value = AudioState.Paused
    }

    override fun resume() {
        resumeCount++
        _audioState.value = AudioState.Playing
    }

    override fun stop() {
        stopCount++
        _audioState.value = AudioState.Idle
    }

    override fun release() {
        stop()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class QuranViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeQuranRepository
    private lateinit var fakeAudioPlayer: FakeAudioPlayer
    private lateinit var getAyahUseCase: GetAyahUseCase
    private lateinit var viewModel: QuranViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeQuranRepository()
        fakeAudioPlayer = FakeAudioPlayer()
        getAyahUseCase = GetAyahUseCase(fakeRepository)
        viewModel = QuranViewModel(getAyahUseCase, fakeAudioPlayer)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isInitial() {
        assertEquals(QuranUiState.Initial, viewModel.uiState.value)
        assertEquals("", viewModel.searchQuery.value)
        assertEquals(QuranEditionPresets.DEFAULT_TRANSLATION, viewModel.selectedTranslation.value)
        assertEquals(QuranEditionPresets.DEFAULT_RECITATION, viewModel.selectedRecitation.value)
        assertEquals(AudioState.Idle, viewModel.audioState.value)
    }

    @Test
    fun searchAyah_validReference_emitsSuccess() = runTest {
        viewModel.searchAyah("2:255")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is QuranUiState.Success)
        assertEquals("Al-Baqara", state.ayah.surahName)
        assertEquals(255, state.ayah.numberInSurah)
        assertEquals("2:255", viewModel.searchQuery.value)
        assertEquals("2:255", fakeRepository.lastReferenceReceived)
    }

    @Test
    fun searchAyah_blankReference_doesNotTriggerSearch() = runTest {
        viewModel.searchAyah("   ")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(QuranUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun searchAyah_notFound_emitsErrorWithIsNotFoundFlag() = runTest {
        fakeRepository.responseToReturn = AppResult.Error(
            exception = Exception("Requested resource not found"),
            message = "Requested resource not found"
        )

        viewModel.searchAyah("999:999")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is QuranUiState.Error)
        assertTrue(state.isNotFoundError)
        assertFalse(state.isNetworkError)
    }

    @Test
    fun searchAyah_networkError_emitsErrorWithIsNetworkFlag() = runTest {
        fakeRepository.responseToReturn = AppResult.Error(
            exception = Exception("Network connection timeout"),
            message = "No internet connection or network timeout."
        )

        viewModel.searchAyah("1:1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is QuranUiState.Error)
        assertTrue(state.isNetworkError)
        assertFalse(state.isNotFoundError)
    }

    @Test
    fun retry_repeatsLastSearchedReference() = runTest {
        fakeRepository.responseToReturn = AppResult.Error(
            exception = Exception("Network timeout"),
            message = "No internet connection or network timeout."
        )

        viewModel.searchAyah("2:255")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is QuranUiState.Error)

        // Now repository recovers
        fakeRepository.responseToReturn = null
        viewModel.onAction(QuranUiAction.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is QuranUiState.Success)
        assertEquals(255, state.ayah.numberInSurah)
    }

    @Test
    fun updateQuery_and_clearQuery_updatesSearchQueryFlow() {
        viewModel.onAction(QuranUiAction.UpdateSearchQuery("112:1"))
        assertEquals("112:1", viewModel.searchQuery.value)

        viewModel.onAction(QuranUiAction.ClearSearchQuery)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun selectTranslationEdition_reTriggersSearch_ifSearchExists() = runTest {
        viewModel.searchAyah("2:255")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("en.sahih", fakeRepository.lastTranslationReceived)

        viewModel.onAction(QuranUiAction.SelectTranslationEdition("id.indonesian"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("id.indonesian", viewModel.selectedTranslation.value)
        assertEquals("id.indonesian", fakeRepository.lastTranslationReceived)
    }

    @Test
    fun selectAudioEdition_reTriggersSearch_ifSearchExists() = runTest {
        viewModel.searchAyah("2:255")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("ar.alafasy", fakeRepository.lastRecitationReceived)

        viewModel.onAction(QuranUiAction.SelectAudioEdition("ar.abdulbasitmurattal"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("ar.abdulbasitmurattal", viewModel.selectedRecitation.value)
        assertEquals("ar.abdulbasitmurattal", fakeRepository.lastRecitationReceived)
        val state = viewModel.uiState.value
        assertTrue(state is QuranUiState.Success)
        assertEquals("ar.abdulbasitmurattal", state.selectedAudioEdition)
    }

    @Test
    fun playAudio_action_triggersAudioPlayerPlay() {
        val audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3"
        viewModel.onAction(QuranUiAction.PlayAudio(audioUrl))

        assertEquals(audioUrl, fakeAudioPlayer.lastPlayedUrl)
        assertEquals(AudioState.Playing, viewModel.audioState.value)
    }

    @Test
    fun pauseAudio_action_triggersAudioPlayerPause() {
        viewModel.onAction(QuranUiAction.PlayAudio("https://example.com/audio.mp3"))
        viewModel.onAction(QuranUiAction.PauseAudio)

        assertEquals(1, fakeAudioPlayer.pauseCount)
        assertEquals(AudioState.Paused, viewModel.audioState.value)
    }

    @Test
    fun resumeAudio_action_triggersAudioPlayerResume() {
        viewModel.onAction(QuranUiAction.PlayAudio("https://example.com/audio.mp3"))
        viewModel.onAction(QuranUiAction.PauseAudio)
        viewModel.onAction(QuranUiAction.ResumeAudio)

        assertEquals(1, fakeAudioPlayer.resumeCount)
        assertEquals(AudioState.Playing, viewModel.audioState.value)
    }

    @Test
    fun stopAudio_action_triggersAudioPlayerStop() {
        viewModel.onAction(QuranUiAction.PlayAudio("https://example.com/audio.mp3"))
        viewModel.onAction(QuranUiAction.StopAudio)

        assertEquals(1, fakeAudioPlayer.stopCount)
        assertEquals(AudioState.Idle, viewModel.audioState.value)
    }

    @Test
    fun searchAyah_automaticallyStopsAudio() = runTest {
        viewModel.onAction(QuranUiAction.PlayAudio("https://example.com/audio.mp3"))
        assertEquals(AudioState.Playing, viewModel.audioState.value)

        viewModel.searchAyah("1:1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeAudioPlayer.stopCount >= 1)
        assertEquals(AudioState.Idle, viewModel.audioState.value)
    }
}

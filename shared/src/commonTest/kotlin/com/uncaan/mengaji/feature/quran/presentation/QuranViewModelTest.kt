package com.uncaan.mengaji.feature.quran.presentation

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class QuranViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeQuranRepository
    private lateinit var getAyahUseCase: GetAyahUseCase
    private lateinit var viewModel: QuranViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeQuranRepository()
        getAyahUseCase = GetAyahUseCase(fakeRepository)
        viewModel = QuranViewModel(getAyahUseCase)
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
}

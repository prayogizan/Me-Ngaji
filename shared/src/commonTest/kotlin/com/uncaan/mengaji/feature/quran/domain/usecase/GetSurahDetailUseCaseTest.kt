package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSurahDetailUseCaseTest {

    @Test
    fun invoke_surahNumberBelowMinimum_returnsIllegalArgumentExceptionWithoutCallingRepo() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetSurahDetailUseCase(repo)

        val resultZero = useCase(surahNumber = 0)
        val resultNegative = useCase(surahNumber = -5)

        assertTrue(resultZero is AppResult.Error)
        assertTrue(resultZero.exception is IllegalArgumentException)
        assertTrue(resultNegative is AppResult.Error)
        assertTrue(resultNegative.exception is IllegalArgumentException)
        assertEquals(0, repo.getSurahDetailCallCount)
    }

    @Test
    fun invoke_surahNumberAboveMaximum_returnsIllegalArgumentExceptionWithoutCallingRepo() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetSurahDetailUseCase(repo)

        val result = useCase(surahNumber = 115)

        assertTrue(result is AppResult.Error)
        assertTrue(result.exception is IllegalArgumentException)
        assertEquals(0, repo.getSurahDetailCallCount)
    }

    @Test
    fun invoke_boundarySurahNumbers1And114_callsRepositoryAndReturnsSuccess() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetSurahDetailUseCase(repo)

        val resultFirst = useCase(
            surahNumber = 1,
            translationEdition = "id.indonesian",
            audioEdition = "ar.alafasy"
        )
        assertTrue(resultFirst is AppResult.Success)
        assertEquals(1, resultFirst.data.number)
        assertEquals("id.indonesian", repo.lastTranslationEdition)
        assertEquals("ar.alafasy", repo.lastAudioEdition)

        val resultLast = useCase(surahNumber = 114)
        assertTrue(resultLast is AppResult.Success)
        assertEquals(114, resultLast.data.number)
        assertEquals(2, repo.getSurahDetailCallCount)
    }
}


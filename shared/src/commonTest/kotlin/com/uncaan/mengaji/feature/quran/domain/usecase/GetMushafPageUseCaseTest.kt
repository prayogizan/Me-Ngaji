package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMushafPageUseCaseTest {

    @Test
    fun invoke_pageNumberBelowMinimum_returnsIllegalArgumentExceptionWithoutCallingRepo() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetMushafPageUseCase(repo)

        val resultZero = useCase(pageNumber = 0)
        val resultNegative = useCase(pageNumber = -1)

        assertTrue(resultZero is AppResult.Error)
        assertTrue(resultZero.exception is IllegalArgumentException)
        assertTrue(resultNegative is AppResult.Error)
        assertTrue(resultNegative.exception is IllegalArgumentException)
        assertEquals(0, repo.getMushafPageCallCount)
    }

    @Test
    fun invoke_pageNumberAboveMaximum_returnsIllegalArgumentExceptionWithoutCallingRepo() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetMushafPageUseCase(repo)

        val result = useCase(pageNumber = 605)

        assertTrue(result is AppResult.Error)
        assertTrue(result.exception is IllegalArgumentException)
        assertEquals(0, repo.getMushafPageCallCount)
    }

    @Test
    fun invoke_boundaryPageNumbers1And604_callsRepositoryAndReturnsSuccess() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetMushafPageUseCase(repo)

        val resultFirst = useCase(pageNumber = 1, translationEdition = "id.indonesian")
        assertTrue(resultFirst is AppResult.Success)
        assertEquals(1, resultFirst.data.pageNumber)
        assertEquals("id.indonesian", repo.lastTranslationEdition)

        val resultLast = useCase(pageNumber = 604)
        assertTrue(resultLast is AppResult.Success)
        assertEquals(604, resultLast.data.pageNumber)
        assertEquals(2, repo.getMushafPageCallCount)
    }
}


package com.uncaan.mengaji.feature.quran.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.RevelationType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetSurahListUseCaseTest {

    @Test
    fun invoke_defaultForceRefreshFalse_returnsCatalogFromRepository() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetSurahListUseCase(repo)

        val result = useCase()

        assertTrue(result is AppResult.Success)
        assertEquals(1, repo.getSurahListCallCount)
        assertFalse(repo.lastForceRefresh ?: true)
        assertEquals(2, result.data.size)
        assertEquals("Al-Faatiha", result.data[0].englishName)
        assertEquals(RevelationType.MECCAN, result.data[0].revelationType)
        assertEquals(RevelationType.MEDINAN, result.data[1].revelationType)
    }

    @Test
    fun invoke_forceRefreshTrue_passesFlagToRepository() = runTest {
        val repo = FakeQuranRepository()
        val useCase = GetSurahListUseCase(repo)

        val result = useCase(forceRefresh = true)

        assertTrue(result is AppResult.Success)
        assertEquals(1, repo.getSurahListCallCount)
        assertTrue(repo.lastForceRefresh ?: false)
    }

    @Test
    fun invoke_repositoryError_propagatesErrorResult() = runTest {
        val repo = FakeQuranRepository(shouldReturnError = true)
        val useCase = GetSurahListUseCase(repo)

        val result = useCase()

        assertTrue(result is AppResult.Error)
        assertEquals("Network failure", result.message)
    }
}


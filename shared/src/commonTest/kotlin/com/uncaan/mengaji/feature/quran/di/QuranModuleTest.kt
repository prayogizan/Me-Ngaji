package com.uncaan.mengaji.feature.quran.di

import com.uncaan.mengaji.feature.quran.data.local.LocalRecitationCache
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiService
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetBookmarksUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetMushafPageUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahDetailUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahListUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.SaveLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.ToggleBookmarkUseCase
import com.uncaan.mengaji.testutil.FakeLocalRecitationCache
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Unit tests verifying Koin dependency injection configuration for [quranModule].
 *
 * Ensures that [QuranApiService], [QuranRepository], and all domain Use Cases
 * can be resolved cleanly from the Koin container.
 */
class QuranModuleTest {

    private val testCoreModule = module {
        single { HttpClient() }
        single<CoroutineDispatcher> { Dispatchers.Default }
        single<LocalRecitationCache> { FakeLocalRecitationCache() }
    }

    @Test
    fun quranModule_resolvesApiServiceAndRepository() {
        val koin = koinApplication {
            modules(testCoreModule, quranModule)
        }.koin

        val apiService: QuranApiService = koin.get()
        val repository: QuranRepository = koin.get()

        assertNotNull(apiService)
        assertNotNull(repository)
    }

    @Test
    fun quranModule_resolvesAllDomainUseCases() {
        val koin = koinApplication {
            modules(testCoreModule, quranModule)
        }.koin

        val getAyahUseCase: GetAyahUseCase = koin.get()
        val getSurahListUseCase: GetSurahListUseCase = koin.get()
        val getSurahDetailUseCase: GetSurahDetailUseCase = koin.get()
        val getMushafPageUseCase: GetMushafPageUseCase = koin.get()
        val getLastReadUseCase: GetLastReadUseCase = koin.get()
        val saveLastReadUseCase: SaveLastReadUseCase = koin.get()
        val getBookmarksUseCase: GetBookmarksUseCase = koin.get()
        val toggleBookmarkUseCase: ToggleBookmarkUseCase = koin.get()

        assertNotNull(getAyahUseCase)
        assertNotNull(getSurahListUseCase)
        assertNotNull(getSurahDetailUseCase)
        assertNotNull(getMushafPageUseCase)
        assertNotNull(getLastReadUseCase)
        assertNotNull(saveLastReadUseCase)
        assertNotNull(getBookmarksUseCase)
        assertNotNull(toggleBookmarkUseCase)
    }
}

package com.uncaan.mengaji.feature.quran.di

import com.uncaan.mengaji.feature.quran.data.remote.QuranApiService
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetMushafPageUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahDetailUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahListUseCase
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
 * Ensures that [QuranApiService], [QuranRepository], [GetAyahUseCase],
 * [GetSurahListUseCase], [GetSurahDetailUseCase], and [GetMushafPageUseCase]
 * can be resolved cleanly from the Koin container.
 */
class QuranModuleTest {

    private val testCoreModule = module {
        single { HttpClient() }
        single<CoroutineDispatcher> { Dispatchers.Default }
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

        assertNotNull(getAyahUseCase)
        assertNotNull(getSurahListUseCase)
        assertNotNull(getSurahDetailUseCase)
        assertNotNull(getMushafPageUseCase)
    }
}


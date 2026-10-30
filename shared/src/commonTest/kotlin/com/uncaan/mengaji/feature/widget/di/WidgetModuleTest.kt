package com.uncaan.mengaji.feature.widget.di

import com.uncaan.mengaji.feature.widget.data.local.LocalAyahCache
import com.uncaan.mengaji.feature.widget.data.remote.DailyAyahApiService
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.feature.widget.domain.repository.DailyAyahRepository
import com.uncaan.mengaji.feature.widget.domain.usecase.GetDailyAyahUseCase
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Unit tests verifying Koin dependency injection configuration for [widgetModule].
 *
 * Ensures that [DailyAyahApiService], [DailyAyahRepository], and [GetDailyAyahUseCase]
 * can be resolved without dependency mismatch or missing parameter errors (such as
 * unhandled function parameters with default arguments).
 */
class WidgetModuleTest {

    private val testModule = module {
        single { HttpClient() }
        single<CoroutineDispatcher> { Dispatchers.Default }
        single<LocalAyahCache> {
            object : LocalAyahCache {
                override fun getCachedAyah(): DailyAyah? = null
                override fun cacheAyah(ayah: DailyAyah) {}
                override fun getCacheDate(): String? = null
            }
        }
    }

    @Test
    fun widgetModule_resolvesDailyAyahApiService() {
        val koin = koinApplication {
            modules(testModule, widgetModule)
        }.koin

        val service: DailyAyahApiService = koin.get()
        assertNotNull(service)
    }

    @Test
    fun widgetModule_resolvesDailyAyahRepository() {
        val koin = koinApplication {
            modules(testModule, widgetModule)
        }.koin

        val repository: DailyAyahRepository = koin.get()
        assertNotNull(repository)
    }

    @Test
    fun widgetModule_resolvesGetDailyAyahUseCase() {
        val koin = koinApplication {
            modules(testModule, widgetModule)
        }.koin

        val useCase: GetDailyAyahUseCase = koin.get()
        assertNotNull(useCase)
    }
}

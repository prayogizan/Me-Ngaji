package com.uncaan.mengaji.feature.widget.di

import com.uncaan.mengaji.feature.widget.data.remote.DailyAyahApiService
import com.uncaan.mengaji.feature.widget.data.remote.DailyAyahApiServiceImpl
import com.uncaan.mengaji.feature.widget.data.repository.DailyAyahRepositoryImpl
import com.uncaan.mengaji.feature.widget.domain.repository.DailyAyahRepository
import com.uncaan.mengaji.feature.widget.domain.usecase.GetDailyAyahUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin dependency injection module for the Daily Ayah Widget feature.
 *
 * Configures the data source, repository, and domain use case:
 * - [DailyAyahApiService] -> [DailyAyahApiServiceImpl] (Singleton)
 * - [DailyAyahRepository] -> [DailyAyahRepositoryImpl] (Singleton)
 * - [GetDailyAyahUseCase] -> Stateless Factory
 *
 * Note: Platform-specific [com.uncaan.mengaji.feature.widget.data.local.LocalAyahCache] is registered in
 * [com.uncaan.mengaji.core.di.platformModule].
 *
 * @see com.uncaan.mengaji.core.di.initKoin
 * @see com.uncaan.mengaji.core.di.platformModule
 */
val widgetModule = module {
    // Remote API Service
    singleOf(::DailyAyahApiServiceImpl) bind DailyAyahApiService::class

    // Repository
    single<DailyAyahRepository> {
        DailyAyahRepositoryImpl(
            apiService = get(),
            ioDispatcher = get()
        )
    }

    // Domain Use Case
    factory {
        GetDailyAyahUseCase(
            repository = get(),
            cache = get()
        )
    }
}

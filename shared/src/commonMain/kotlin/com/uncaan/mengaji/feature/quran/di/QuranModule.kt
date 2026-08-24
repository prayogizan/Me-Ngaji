package com.uncaan.mengaji.feature.quran.di

import com.uncaan.mengaji.feature.quran.data.remote.QuranApiService
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiServiceImpl
import com.uncaan.mengaji.feature.quran.data.repository.QuranRepositoryImpl
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import com.uncaan.mengaji.feature.quran.presentation.QuranViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val quranModule = module {
    // Data Sources & Services
    singleOf(::QuranApiServiceImpl) bind QuranApiService::class

    // Repositories
    singleOf(::QuranRepositoryImpl) bind QuranRepository::class

    // Use Cases
    factoryOf(::GetAyahUseCase)

    // ViewModels
    viewModelOf(::QuranViewModel)
}

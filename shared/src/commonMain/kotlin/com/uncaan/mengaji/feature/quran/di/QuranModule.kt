package com.uncaan.mengaji.feature.quran.di

import com.uncaan.mengaji.feature.quran.data.remote.QuranApiService
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiServiceImpl
import com.uncaan.mengaji.feature.quran.data.repository.QuranRepositoryImpl
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import com.uncaan.mengaji.feature.quran.domain.usecase.GetAyahUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetBookmarksUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetMushafPageUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahDetailUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.GetSurahListUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.SaveLastReadUseCase
import com.uncaan.mengaji.feature.quran.domain.usecase.ToggleBookmarkUseCase
import com.uncaan.mengaji.feature.quran.presentation.QuranViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin dependency injection module for the Quran feature.
 *
 * Binds:
 * - [QuranApiService] to [QuranApiServiceImpl] as a singleton.
 * - [QuranRepository] to [QuranRepositoryImpl] as a singleton.
 * - [GetAyahUseCase], [GetSurahListUseCase], [GetSurahDetailUseCase], and [GetMushafPageUseCase] as factories.
 * - [GetLastReadUseCase], [SaveLastReadUseCase], [GetBookmarksUseCase], and [ToggleBookmarkUseCase] as factories.
 * - [QuranViewModel] as a lifecycle-aware ViewModel.
 *
 * @see QuranApiService
 * @see QuranRepository
 * @see GetAyahUseCase
 * @see GetSurahListUseCase
 * @see GetSurahDetailUseCase
 * @see GetMushafPageUseCase
 * @see GetLastReadUseCase
 * @see SaveLastReadUseCase
 * @see GetBookmarksUseCase
 * @see ToggleBookmarkUseCase
 * @see QuranViewModel
 */
val quranModule = module {
    // Data Sources & Services
    singleOf(::QuranApiServiceImpl) bind QuranApiService::class

    // Repositories
    singleOf(::QuranRepositoryImpl) bind QuranRepository::class

    // Use Cases
    factoryOf(::GetAyahUseCase)
    factoryOf(::GetSurahListUseCase)
    factoryOf(::GetSurahDetailUseCase)
    factoryOf(::GetMushafPageUseCase)
    factoryOf(::GetLastReadUseCase)
    factoryOf(::SaveLastReadUseCase)
    factoryOf(::GetBookmarksUseCase)
    factoryOf(::ToggleBookmarkUseCase)

    // ViewModels
    viewModelOf(::QuranViewModel)
}

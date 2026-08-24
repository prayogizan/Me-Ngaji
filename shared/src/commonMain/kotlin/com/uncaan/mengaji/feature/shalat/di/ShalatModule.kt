package com.uncaan.mengaji.feature.shalat.di

import com.uncaan.mengaji.feature.shalat.presentation.ShalatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin dependency injection module for the Shalat Schedule feature.
 *
 * Registers the presentation layer [ShalatViewModel] for the Shalat Schedule screen.
 *
 * @see com.uncaan.mengaji.core.di.initKoin
 */
val shalatModule = module {
    viewModelOf(::ShalatViewModel)
}

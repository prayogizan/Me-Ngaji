package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.feature.quran.di.quranModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            platformModule,
            coreModule,
            quranModule
        )
    }
}

package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.domain.model.AppConfig
import com.uncaan.mengaji.feature.quran.di.quranModule
import com.uncaan.mengaji.feature.shalat.di.shalatModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin dependency injection across target platforms.
 *
 * Configures the platform module, core module with [AppConfig], and feature modules. Supports optional
 * platform-specific app declarations (such as Android Context binding).
 *
 * @param appConfig Application configuration and environment properties. Defaults to [AppConfig.prod].
 * @param appDeclaration Optional lambda for platform-specific Koin configurations.
 * @see AppConfig
 * @see platformModule
 * @see coreModule
 * @see quranModule
 * @see shalatModule
 */
fun initKoin(
    appConfig: AppConfig = AppConfig.prod(),
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(
            platformModule,
            coreModule(appConfig),
            quranModule,
            shalatModule
        )
    }
}


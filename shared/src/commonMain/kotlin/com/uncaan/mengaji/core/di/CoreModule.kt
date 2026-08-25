package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.data.network.HttpClientFactory
import com.uncaan.mengaji.core.domain.model.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Core Koin dependency injection module factory.
 *
 * Registers shared singleton components such as [AppConfig], [HttpClientFactory],
 * and the I/O coroutine dispatcher.
 *
 * @param appConfig The application configuration and environment properties. Defaults to [AppConfig.prod].
 * @return Configured Koin [Module].
 * @see AppConfig
 * @see HttpClientFactory
 */
fun coreModule(appConfig: AppConfig = AppConfig.prod()): Module = module {
    single { appConfig }
    single {
        HttpClientFactory.create(
            baseUrl = get<AppConfig>().baseUrl,
            isDebug = get<AppConfig>().isDebug
        )
    }
    single { Dispatchers.IO }
}

/**
 * Default core Koin dependency injection module using standard production configuration.
 */
val coreModule: Module = coreModule()


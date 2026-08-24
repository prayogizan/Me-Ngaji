package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.data.network.HttpClientFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

/**
 * Core Koin dependency injection module.
 *
 * Registers shared singleton components such as the [HttpClientFactory] and the I/O
 * coroutine dispatcher.
 *
 * @see HttpClientFactory
 */
val coreModule = module {
    single { HttpClientFactory.create() }
    single { Dispatchers.IO }
}

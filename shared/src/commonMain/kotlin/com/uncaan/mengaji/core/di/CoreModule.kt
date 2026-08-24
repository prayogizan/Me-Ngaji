package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.data.network.HttpClientFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val coreModule = module {
    single { HttpClientFactory.create() }
    single { Dispatchers.IO }
}

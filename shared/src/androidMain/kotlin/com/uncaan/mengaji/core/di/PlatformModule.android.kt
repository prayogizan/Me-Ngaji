package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.audio.AyahAudioPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin dependency injection module.
 *
 * Registers native Android dependencies and platform bindings.
 */
actual val platformModule = module {
    single { AyahAudioPlayer(androidContext()) }
}

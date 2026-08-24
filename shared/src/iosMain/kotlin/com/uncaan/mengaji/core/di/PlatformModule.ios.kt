package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.audio.AyahAudioPlayer
import org.koin.dsl.module

/**
 * iOS-specific Koin dependency injection module.
 *
 * Registers native iOS dependencies and platform bindings.
 */
actual val platformModule = module {
    single { AyahAudioPlayer() }
}

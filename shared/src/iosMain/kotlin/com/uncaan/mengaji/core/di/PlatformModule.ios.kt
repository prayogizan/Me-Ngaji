package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.audio.AudioPlayer
import com.uncaan.mengaji.core.audio.AyahAudioPlayer
import com.uncaan.mengaji.feature.widget.data.local.LocalAyahCache
import com.uncaan.mengaji.feature.widget.data.local.PlatformLocalAyahCache
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * iOS-specific Koin dependency injection module.
 *
 * Registers native iOS dependencies and platform bindings.
 */
actual val platformModule = module {
    single { AyahAudioPlayer() } bind AudioPlayer::class
    single { PlatformLocalAyahCache() } bind LocalAyahCache::class
}


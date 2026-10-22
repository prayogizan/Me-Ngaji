package com.uncaan.mengaji.core.di

import com.uncaan.mengaji.core.audio.AudioPlayer
import com.uncaan.mengaji.core.audio.AyahAudioPlayer
import com.uncaan.mengaji.feature.widget.data.local.LocalAyahCache
import com.uncaan.mengaji.feature.widget.data.local.PlatformLocalAyahCache
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Android-specific Koin dependency injection module.
 *
 * Registers native Android dependencies and platform bindings.
 */
actual val platformModule = module {
    single { AyahAudioPlayer(androidContext()) } bind AudioPlayer::class
    single { PlatformLocalAyahCache(androidContext()) } bind LocalAyahCache::class
}


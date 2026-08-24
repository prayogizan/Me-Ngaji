package com.uncaan.mengaji

import android.os.Build

/**
 * Android-specific implementation of the [Platform] abstraction.
 *
 * Resolves the Android OS version using [Build.VERSION.SDK_INT].
 *
 * @property name Android SDK level descriptor (e.g. "Android 34").
 */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

/**
 * Creates and returns the [AndroidPlatform] instance for the Android target.
 *
 * @return The [AndroidPlatform] instance.
 */
actual fun getPlatform(): Platform = AndroidPlatform()
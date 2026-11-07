package com.uncaan.mengaji.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.UIKit.UIApplication

/**
 * iOS implementation of [KeepScreenOn] utilizing [UIApplication.sharedApplication].
 *
 * Sets `idleTimerDisabled` to [enabled] within a lifecycle-aware [DisposableEffect]
 * and restores the previous timer configuration when disposed.
 *
 * @param enabled Whether screen lock and dimming prevention should be active.
 * @see KeepScreenOn
 */
@Composable
actual fun KeepScreenOn(enabled: Boolean) {
    DisposableEffect(enabled) {
        val application = UIApplication.sharedApplication
        val previousState = application.idleTimerDisabled
        application.idleTimerDisabled = enabled
        onDispose {
            application.idleTimerDisabled = previousState
        }
    }
}


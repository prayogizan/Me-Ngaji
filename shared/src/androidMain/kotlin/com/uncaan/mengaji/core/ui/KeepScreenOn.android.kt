package com.uncaan.mengaji.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/**
 * Android implementation of [KeepScreenOn] utilizing [LocalView.current].
 *
 * Toggles `view.keepScreenOn` within a lifecycle-aware [DisposableEffect] and
 * reliably restores the previous state upon disposal.
 *
 * @param enabled Whether screen lock and dimming prevention should be active.
 * @see KeepScreenOn
 */
@Composable
actual fun KeepScreenOn(enabled: Boolean) {
    val view = LocalView.current
    DisposableEffect(enabled) {
        val previousState = view.keepScreenOn
        view.keepScreenOn = enabled
        onDispose {
            view.keepScreenOn = previousState
        }
    }
}


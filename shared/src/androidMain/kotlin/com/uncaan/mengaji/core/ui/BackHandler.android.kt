package com.uncaan.mengaji.core.ui

import androidx.activity.compose.BackHandler as AndroidXBackHandler
import androidx.compose.runtime.Composable

/**
 * Android implementation of [BackHandler] utilizing [AndroidXBackHandler].
 *
 * Intercepts physical back button presses and predictive back gestures in Android.
 *
 * @param enabled Whether this back handler should intercept back events.
 * @param onBack Callback invoked when a back event is triggered.
 * @see BackHandler
 */
@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    AndroidXBackHandler(enabled = enabled, onBack = onBack)
}


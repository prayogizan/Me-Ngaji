package com.uncaan.mengaji.core.ui

import androidx.compose.runtime.Composable

/**
 * Cross-platform composable for intercepting back-navigation events.
 *
 * On Android, intercepts system back button presses and predictive back gestures via
 * `androidx.activity.compose.BackHandler`.
 * On iOS and other platforms, in-screen navigation is primarily driven by UI action controls
 * (such as the [com.uncaan.mengaji.feature.quran.presentation.component.ReaderTopBar] back icon).
 *
 * @param enabled Whether this back handler should intercept back events. Defaults to `true`.
 * @param onBack Callback invoked when a back event is triggered.
 */
@Composable
expect fun BackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)


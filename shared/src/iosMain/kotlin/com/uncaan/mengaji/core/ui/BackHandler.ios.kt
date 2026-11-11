package com.uncaan.mengaji.core.ui

import androidx.compose.runtime.Composable

/**
 * iOS implementation of [BackHandler].
 *
 * In-screen sub-pane navigation on iOS is driven by explicit UI controls (such as the
 * [com.uncaan.mengaji.feature.quran.presentation.component.ReaderTopBar] back icon), while
 * system navigation and interactive edge-swipe dismissals are managed at the root view controller level.
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
    // No hardware back button on iOS; in-screen reader dismissal is driven by ReaderTopBar
}


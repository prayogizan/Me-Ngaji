package com.uncaan.mengaji.core.ui

import androidx.compose.runtime.Composable

/**
 * Cross-platform composable that manages device display sleep and screen dimming timers.
 *
 * When [enabled] is `true`, keeps the device screen illuminated and awake, preventing sleep
 * during active Quran recitation or long reading sessions. When this composable leaves
 * composition or [enabled] transitions to `false`, the platform's previous screen timer state
 * is safely restored.
 *
 * **Platform implementations:**
 * - **Android:** Configures `LocalView.current.keepScreenOn` in a lifecycle-safe `DisposableEffect`.
 * - **iOS:** Configures `UIApplication.sharedApplication.idleTimerDisabled` in a lifecycle-safe `DisposableEffect`.
 *
 * @param enabled Whether screen lock and dimming prevention should be active. Defaults to `true`.
 */
@Composable
expect fun KeepScreenOn(enabled: Boolean = true)


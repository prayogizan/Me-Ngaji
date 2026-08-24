package com.uncaan.mengaji

import androidx.compose.ui.window.ComposeUIViewController
import com.uncaan.mengaji.core.di.initKoin

/**
 * iOS UIViewController factory for the MeNgaji Compose Multiplatform application.
 *
 * Initializes Koin dependency injection via [initKoin] on first launch and embeds
 * the root [App] composable within a [ComposeUIViewController].
 *
 * @return The configured [UIViewController] to be presented by the iOS host application.
 * @see initKoin
 * @see App
 */
fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
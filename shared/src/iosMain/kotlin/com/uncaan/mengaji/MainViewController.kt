package com.uncaan.mengaji

import androidx.compose.ui.window.ComposeUIViewController
import com.uncaan.mengaji.core.di.initKoin
import com.uncaan.mengaji.core.domain.model.AppConfig
import platform.UIKit.UIViewController

/**
 * iOS UIViewController factory for the MeNgaji Compose Multiplatform application.
 *
 * Initializes Koin dependency injection via [initKoin] on first launch and embeds
 * the root [App] composable within a [ComposeUIViewController].
 *
 * @param appConfig The application configuration and environment properties. Defaults to [AppConfig.prod].
 * @return The configured [UIViewController] to be presented by the iOS host application.
 * @see initKoin
 * @see AppConfig
 * @see App
 */
fun MainViewController(appConfig: AppConfig = AppConfig.prod()): UIViewController = ComposeUIViewController {
    initKoin(appConfig = appConfig)
    App()
}
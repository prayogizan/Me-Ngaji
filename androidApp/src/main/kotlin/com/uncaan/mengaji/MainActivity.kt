package com.uncaan.mengaji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.uncaan.mengaji.core.di.initKoin
import com.uncaan.mengaji.core.domain.model.AppConfig
import com.uncaan.mengaji.core.domain.model.AppEnvironment
import org.koin.android.ext.koin.androidContext

/**
 * Main Android entry-point activity for the MeNgaji application.
 *
 * Enables edge-to-edge system bars, initializes Koin dependency injection with
 * flavor-aware [AppConfig] via [initKoin], and sets the Compose content hierarchy to [App].
 *
 * @see initKoin
 * @see AppConfig
 * @see App
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val environment = when (BuildConfig.FLAVOR.lowercase()) {
            "dev" -> AppEnvironment.DEV
            else -> AppEnvironment.PROD
        }

        val appConfig = AppConfig(
            environment = environment,
            appVersion = BuildConfig.VERSION_NAME,
            buildNumber = BuildConfig.VERSION_CODE,
            isDebug = BuildConfig.DEBUG
        )

        initKoin(appConfig = appConfig) {
            androidContext(this@MainActivity.applicationContext)
        }

        setContent {
            App()
        }
    }
}

/**
 * Android Studio Compose preview entry point for [App].
 */
@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
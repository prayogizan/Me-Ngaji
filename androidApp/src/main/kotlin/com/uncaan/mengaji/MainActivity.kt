package com.uncaan.mengaji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.uncaan.mengaji.core.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Main Android entry-point activity for the MeNgaji application.
 *
 * Enables edge-to-edge system bars, initializes Koin dependency injection via [initKoin],
 * and sets the Compose content hierarchy to [App].
 *
 * @see initKoin
 * @see App
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initKoin {
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
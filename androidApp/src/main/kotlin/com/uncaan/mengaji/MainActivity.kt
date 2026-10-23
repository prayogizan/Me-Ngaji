package com.uncaan.mengaji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Main Android entry-point activity for the MeNgaji application.
 *
 * Enables edge-to-edge system bars and sets the Compose content hierarchy to [App].
 * Global dependency injection is initialized upfront in [MeNgajiApplication].
 *
 * @see MeNgajiApplication
 * @see App
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
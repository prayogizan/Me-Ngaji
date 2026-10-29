package com.uncaan.mengaji

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.uncaan.mengaji.navigation.AyahDeepLink
import com.uncaan.mengaji.navigation.DeepLinkConstants

/**
 * Main Android entry-point activity for the MeNgaji application.
 *
 * Enables edge-to-edge system bars, sets the Compose content hierarchy to [App], and parses
 * incoming deep links from home screen widgets (CMP-504) or notifications across cold starts
 * ([onCreate]) and warm starts ([onNewIntent]).
 *
 * Global dependency injection is initialized upfront in [MeNgajiApplication].
 *
 * @see MeNgajiApplication
 * @see App
 * @see AyahDeepLink
 */
class MainActivity : ComponentActivity() {

    private var deepLinkState by mutableStateOf<AyahDeepLink?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        extractDeepLink(intent)

        setContent {
            App(
                deepLink = deepLinkState,
                onDeepLinkConsumed = { deepLinkState = null }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractDeepLink(intent)
    }

    /**
     * Extracts and validates deep-link parameters from the provided [intent] extras.
     *
     * Sets [deepLinkState] if both [DeepLinkConstants.EXTRA_SURAH_NUMBER] and
     * [DeepLinkConstants.EXTRA_AYAH_NUMBER] are positive integers; otherwise, does not alter state.
     *
     * @param intent The incoming intent to parse, or `null`.
     */
    private fun extractDeepLink(intent: Intent?) {
        val surahNumber = intent?.getIntExtra(DeepLinkConstants.EXTRA_SURAH_NUMBER, -1) ?: -1
        val ayahNumber = intent?.getIntExtra(DeepLinkConstants.EXTRA_AYAH_NUMBER, -1) ?: -1

        if (surahNumber > 0 && ayahNumber > 0) {
            deepLinkState = AyahDeepLink(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber
            )
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

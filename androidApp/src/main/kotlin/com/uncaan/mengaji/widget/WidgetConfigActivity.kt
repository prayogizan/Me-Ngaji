package com.uncaan.mengaji.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.widget.config.DailyAyahWidgetConfigManager
import com.uncaan.mengaji.widget.config.WidgetConfigContent
import com.uncaan.mengaji.widget.config.WidgetConfigViewModel
import kotlinx.coroutines.launch

/**
 * Activity presented when placing or reconfiguring a MeNgaji Daily Ayah home screen widget.
 *
 * Implements the standard Android App Widget configuration contract:
 * 1. Default cancellation safety: Sets [RESULT_CANCELED] immediately in [onCreate] so that backing
 *    out without confirmation cleanly aborts widget placement.
 * 2. Real-time preview: Displays user's selected translation language and color theme in a live preview card.
 * 3. Instance persistence: On confirm, persists settings to [android.content.SharedPreferences] scoped to
 *    [appWidgetId] via [DailyAyahWidgetConfigManager].
 * 4. Immediate update: Dispatches a forced render cycle to the newly created widget instance via [DailyAyahWidgetProvider].
 * 5. Completion contract: Packages [AppWidgetManager.EXTRA_APPWIDGET_ID] into the result intent, sets
 *    [RESULT_OK], and finishes.
 *
 * @see DailyAyahWidgetProvider
 * @see DailyAyahWidgetConfigManager
 * @see WidgetConfigViewModel
 */
class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    private val viewModel: WidgetConfigViewModel by viewModels {
        viewModelFactory {
            initializer {
                val initialConfig = DailyAyahWidgetConfigManager.loadConfig(
                    context = applicationContext,
                    appWidgetId = appWidgetId
                )
                WidgetConfigViewModel(
                    appWidgetId = appWidgetId,
                    initialConfig = initialConfig
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android AppWidget Configure Contract Rule 1: Set RESULT_CANCELED immediately
        setResult(RESULT_CANCELED)

        // Extract appWidgetId from launching Intent extras
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // If launched without a valid ID, abort gracefully
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            MeNgajiTheme {
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

                WidgetConfigContent(
                    uiState = uiState,
                    onAction = viewModel::onAction,
                    onConfirm = ::confirmWidgetPlacement,
                    onCancel = ::finish
                )
            }
        }
    }

    /**
     * Confirms the widget configuration, persists the preferences, triggers widget rendering,
     * sets [RESULT_OK], and finishes the activity.
     */
    private fun confirmWidgetPlacement() {
        val config = viewModel.getCurrentConfig()

        // Persist preferences for this specific widget instance
        DailyAyahWidgetConfigManager.saveConfig(
            context = applicationContext,
            appWidgetId = appWidgetId,
            config = config
        )

        // Trigger immediate widget update for this instance
        lifecycleScope.launch {
            try {
                val glanceManager = GlanceAppWidgetManager(applicationContext)
                val glanceId = glanceManager.getGlanceIdBy(appWidgetId)
                DailyAyahWidgetProvider().update(applicationContext, glanceId)
            } catch (_: Exception) {
                // Fallback to updateAll if glanceId lookup fails
                DailyAyahWidgetProvider().updateAll(applicationContext)
            }

            // Return RESULT_OK with appWidgetId extra to host launcher
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}


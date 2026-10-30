package com.uncaan.mengaji.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.feature.widget.domain.usecase.GetDailyAyahUseCase
import com.uncaan.mengaji.widget.config.DailyAyahWidgetConfigManager
import com.uncaan.mengaji.widget.ui.DailyAyahCompactLayout
import com.uncaan.mengaji.widget.ui.DailyAyahFallbackLayout
import com.uncaan.mengaji.widget.ui.DailyAyahFullLayout
import com.uncaan.mengaji.widget.ui.DailyAyahSmallLayout
import com.uncaan.mengaji.widget.ui.theme.DailyAyahColorTokens
import com.uncaan.mengaji.widget.ui.theme.DailyAyahWidgetColors
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Main Jetpack Glance App Widget provider for displaying the MeNgaji daily Quran verse on the Android home screen.
 *
 * Implements [SizeMode.Responsive] with three defined size breakpoints matching common launcher grid cells:
 * - **Full (4x2):** Minimum 260dp width x 140dp height — displays complete Arabic text and Indonesian translation.
 * - **Compact (4x1):** Minimum 260dp width x 60dp height — single-line horizontal preview.
 * - **Small (2x2):** Minimum 110dp width x 110dp height — compact square presentation.
 *
 * Fetches data via [GetDailyAyahUseCase] inside [provideGlance] using a cache-first strategy with network fallback,
 * honoring per-instance configuration (translation edition and color theme) persisted by [DailyAyahWidgetConfigManager].
 *
 * @see GlanceAppWidget
 * @see SizeMode.Responsive
 * @see GetDailyAyahUseCase
 * @see DailyAyahWidgetConfigManager
 */
class DailyAyahWidgetProvider : GlanceAppWidget(), KoinComponent {

    companion object {
        /** Minimum breakpoint for small 2x2 square widget. */
        val SMALL_SQUARE = DpSize(110.dp, 110.dp)

        /** Minimum breakpoint for compact 4x1 horizontal banner widget. */
        val COMPACT_HORIZONTAL = DpSize(260.dp, 60.dp)

        /** Minimum breakpoint for full 4x2 detailed widget. */
        val FULL_LARGE = DpSize(260.dp, 140.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL_SQUARE, COMPACT_HORIZONTAL, FULL_LARGE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = try {
            GlanceAppWidgetManager(context).getAppWidgetId(id)
        } catch (_: Exception) {
            AppWidgetManager.INVALID_APPWIDGET_ID
        }

        val config = DailyAyahWidgetConfigManager.loadConfig(context, appWidgetId)
        val colors = DailyAyahWidgetColors.colorsForTheme(config.theme)

        val result: AppResult<DailyAyah> = try {
            val getDailyAyahUseCase: GetDailyAyahUseCase by inject()
            getDailyAyahUseCase(edition = config.language.editionKey)
        } catch (e: Exception) {
            AppResult.Error(exception = e, message = e.message ?: "Gagal memuat ayat")
        }

        provideContent {
            DailyAyahContent(result = result, colors = colors)
        }
    }

    /**
     * Internal composable switching between layouts based on current [AppResult], [LocalSize], and [DailyAyahColorTokens].
     */
    @Composable
    private fun DailyAyahContent(
        result: AppResult<DailyAyah>,
        colors: DailyAyahColorTokens
    ) {
        when (result) {
            is AppResult.Success -> {
                val ayah = result.data
                val size = LocalSize.current
                when {
                    size.width >= 260.dp && size.height >= 140.dp -> {
                        DailyAyahFullLayout(ayah = ayah, colors = colors)
                    }
                    size.width >= 260.dp -> {
                        DailyAyahCompactLayout(ayah = ayah, colors = colors)
                    }
                    else -> {
                        DailyAyahSmallLayout(ayah = ayah, colors = colors)
                    }
                }
            }
            is AppResult.Error -> {
                DailyAyahFallbackLayout(
                    message = result.message.orEmpty().ifBlank { "Gagal memuat ayat hari ini." },
                    isError = true,
                    colors = colors
                )
            }
            is AppResult.Loading -> {
                DailyAyahFallbackLayout(
                    message = "Memuat ayat hari ini…",
                    isError = false,
                    colors = colors
                )
            }
        }
    }
}

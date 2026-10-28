package com.uncaan.mengaji.widget.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.widget.domain.usecase.GetDailyAyahUseCase
import com.uncaan.mengaji.widget.DailyAyahWidgetProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android WorkManager [CoroutineWorker] responsible for background synchronization of the Daily Ayah quote.
 *
 * Runs on a periodic schedule (approximately every 24 hours) to fetch a fresh Quran verse from the remote API,
 * persist it into local cache, and update all active [DailyAyahWidgetProvider] widget instances on the home screen.
 *
 * Implements [KoinComponent] to resolve domain use cases via Koin dependency injection without requiring
 * UI components or activities to be active.
 *
 * Error handling & retry policy:
 * - On network or transient failure: returns [Result.retry] with exponential backoff if [runAttemptCount] is
 *   below [MAX_RETRIES].
 * - When max retries are exhausted: returns [Result.failure], leaving the existing cached Ayah on display
 *   (graceful degradation).
 * - When no widget instances are placed: completes data refresh and cache persistence, performing a graceful
 *   no-op on widget rendering.
 *
 * @param context Android Application Context passed by WorkManager runtime.
 * @param params Execution parameters supplied by WorkManager.
 * @see CoroutineWorker
 * @see KoinComponent
 * @see GetDailyAyahUseCase
 * @see DailyAyahWidgetProvider
 * @see DailyAyahWorkScheduler
 */
class DailyAyahRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val getDailyAyahUseCase: GetDailyAyahUseCase by inject()

    /**
     * Executes background synchronization of the daily Ayah quote.
     *
     * @return [Result.success] on successful fetch and widget update, [Result.retry] on transient failures,
     *         or [Result.failure] when maximum retries have been exhausted.
     */
    override suspend fun doWork(): Result {
        return try {
            when (getDailyAyahUseCase(forceRefresh = true)) {
                is AppResult.Success -> {
                    updateActiveWidgets()
                    Result.success()
                }
                is AppResult.Error, is AppResult.Loading -> {
                    if (runAttemptCount < MAX_RETRIES) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            }
        } catch (_: Exception) {
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Re-renders all active [DailyAyahWidgetProvider] widget instances if any are currently placed on the home screen.
     *
     * If no widgets exist, this operation safely no-ops without throwing exceptions.
     */
    private suspend fun updateActiveWidgets() {
        try {
            val glanceManager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = glanceManager.getGlanceIds(DailyAyahWidgetProvider::class.java)
            if (glanceIds.isNotEmpty()) {
                DailyAyahWidgetProvider().updateAll(applicationContext)
            }
        } catch (_: Exception) {
            // Non-fatal: caching succeeded even if widget rendering dispatch fails
        }
    }

    companion object {
        /** Tag identifier for logging and filtering worker instances. */
        const val TAG: String = "DailyAyahRefreshWorker"

        /** Maximum retry attempts before giving up and preserving stale cache. */
        const val MAX_RETRIES: Int = 3
    }
}

package com.uncaan.mengaji.widget.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

/**
 * Utility orchestrator for scheduling and managing background periodic execution of [DailyAyahRefreshWorker].
 *
 * Configures a [PeriodicWorkRequestBuilder] that runs approximately every 24 hours to keep the home screen
 * Daily Ayah quote synchronized without draining device resources or battery.
 *
 * Scheduling constraints & behavior:
 * - **Network constraint:** Requires [NetworkType.CONNECTED] so work is only executed when an internet connection
 *   is present.
 * - **Doze mode awareness:** Relies on standard WorkManager maintenance windows; does not acquire aggressive wake
 *   locks.
 * - **Exponential backoff:** Configured with [BackoffPolicy.EXPONENTIAL] and [WorkRequest.MIN_BACKOFF_MILLIS] to
 *   progressively delay retries on transient connection dropouts.
 * - **Deduplication:** Uses [ExistingPeriodicWorkPolicy.KEEP] to ensure subsequent schedule invocations (such as
 *   app restarts or new widget placements) do not restart or duplicate the active periodic schedule.
 *
 * @see DailyAyahRefreshWorker
 * @see WorkManager
 * @see ExistingPeriodicWorkPolicy.KEEP
 */
object DailyAyahWorkScheduler {

    /** Unique periodic work tag name recognized by [WorkManager]. */
    const val WORK_NAME: String = "DailyAyahPeriodicRefreshWork"

    /** Default repeat interval in hours between background verse refreshes. */
    const val REPEAT_INTERVAL_HOURS: Long = 24L

    /**
     * Enqueues or retains the unique 24-hour periodic work request for [DailyAyahRefreshWorker].
     *
     * Calling this function is completely idempotent due to [ExistingPeriodicWorkPolicy.KEEP].
     * Safe to invoke from application startup or widget lifecycle callbacks.
     *
     * @param context Android [Context] used to acquire the [WorkManager] instance.
     */
    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshRequest = PeriodicWorkRequestBuilder<DailyAyahRefreshWorker>(
            REPEAT_INTERVAL_HOURS,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshRequest
        )
    }

    /**
     * Cancels any pending or active periodic work associated with [WORK_NAME].
     *
     * Useful for diagnostics, automated testing resets, or if background updates need to be disabled.
     *
     * @param context Android [Context] used to acquire the [WorkManager] instance.
     */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}

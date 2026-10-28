package com.uncaan.mengaji.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.uncaan.mengaji.feature.widget.domain.usecase.GetDailyAyahUseCase
import com.uncaan.mengaji.widget.DailyAyahWidgetProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Glance [ActionCallback] invoked when the user taps the refresh icon button on any Daily Ayah widget.
 *
 * Forces an update by:
 * 1. Invoking [GetDailyAyahUseCase] with `forceRefresh = true` to bypass the cached date, fetch the latest quote,
 *    and update local persistence.
 * 2. Requesting Glance to re-render all active widget instances via [DailyAyahWidgetProvider.updateAll].
 *
 * Implements [KoinComponent] to resolve domain dependencies in background widget receiver threads.
 *
 * @see ActionCallback
 * @see DailyAyahWidgetProvider
 * @see GetDailyAyahUseCase
 */
class RefreshAyahAction : ActionCallback, KoinComponent {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            // Execute use case with forceRefresh to hit remote repository and persist fresh verse
            val getDailyAyahUseCase: GetDailyAyahUseCase by inject()
            getDailyAyahUseCase(forceRefresh = true)

            // Re-render all active widget instances
            DailyAyahWidgetProvider().updateAll(context)
        } catch (_: Exception) {
            // Safe fallback: still attempt to refresh widget view with available state
            DailyAyahWidgetProvider().update(context, glanceId)
        }
    }
}

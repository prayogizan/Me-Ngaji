package com.uncaan.mengaji.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.uncaan.mengaji.widget.worker.DailyAyahWorkScheduler

/**
 * Android broadcast receiver handling widget lifecycle callbacks for [DailyAyahWidgetProvider].
 *
 * Registered in `AndroidManifest.xml` with the standard `android.appwidget.action.APPWIDGET_UPDATE`
 * action filter and links to `@xml/daily_ayah_widget_info`.
 *
 * Delegates all widget creation, configuration, and update cycles to [DailyAyahWidgetProvider].
 * When the first widget instance is placed on the home screen, [onEnabled] ensures that the 24-hour
 * background refresh worker is enqueued via [DailyAyahWorkScheduler].
 *
 * @see GlanceAppWidgetReceiver
 * @see DailyAyahWidgetProvider
 * @see DailyAyahWorkScheduler
 */
class DailyAyahWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = DailyAyahWidgetProvider()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        DailyAyahWorkScheduler.schedule(context)
    }
}

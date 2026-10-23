package com.uncaan.mengaji.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Android broadcast receiver handling widget lifecycle callbacks for [DailyAyahWidgetProvider].
 *
 * Registered in `AndroidManifest.xml` with the standard `android.appwidget.action.APPWIDGET_UPDATE`
 * action filter and links to `@xml/daily_ayah_widget_info`.
 *
 * Delegates all widget creation, configuration, and update cycles to [DailyAyahWidgetProvider].
 *
 * @see GlanceAppWidgetReceiver
 * @see DailyAyahWidgetProvider
 */
class DailyAyahWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = DailyAyahWidgetProvider()
}

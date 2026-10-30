package com.uncaan.mengaji.widget.config

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages persistence and retrieval of per-instance configuration for the Daily Ayah home screen widget.
 *
 * Each placed widget on the Android home screen has a unique `appWidgetId`. Users can configure
 * translation language and visual theme independently for each instance. Preferences are stored in
 * [SharedPreferences] using instance-scoped keys:
 * - `"widget_config_${appWidgetId}_language"`: Translation edition identifier (e.g. `"id.indonesian"`).
 * - `"widget_config_${appWidgetId}_theme"`: Color theme mode (e.g. `"light"`, `"dark"`, `"auto"`).
 *
 * If no configuration exists for a given widget ID, operations gracefully fall back to the defaults
 * ([WidgetLanguage.INDONESIAN] and [WidgetTheme.AUTO]).
 *
 * @see WidgetConfig
 * @see WidgetLanguage
 * @see WidgetTheme
 */
object DailyAyahWidgetConfigManager {

    /** Name of the SharedPreferences file dedicated to widget instance settings. */
    const val PREFS_NAME: String = "daily_ayah_widget_preferences"

    /** Key prefix template for storing the widget translation language. */
    const val KEY_PREFIX_LANGUAGE: String = "widget_config_%d_language"

    /** Key prefix template for storing the widget color theme. */
    const val KEY_PREFIX_THEME: String = "widget_config_%d_theme"

    /**
     * Obtains the shared preferences instance dedicated to widget configurations.
     *
     * @param context Application or activity context.
     * @return [SharedPreferences] instance in private mode.
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Persists the given [WidgetConfig] for the specified [appWidgetId].
     *
     * @param context Context used to access [SharedPreferences].
     * @param appWidgetId The unique ID of the target widget instance.
     * @param config The [WidgetConfig] settings to store.
     */
    fun saveConfig(context: Context, appWidgetId: Int, config: WidgetConfig) {
        val prefs = getPreferences(context)
        prefs.edit()
            .putString(KEY_PREFIX_LANGUAGE.format(appWidgetId), config.language.editionKey)
            .putString(KEY_PREFIX_THEME.format(appWidgetId), config.theme.storageKey)
            .apply()
    }

    /**
     * Loads the persisted [WidgetConfig] for the specified [appWidgetId].
     *
     * Falls back to [WidgetConfig] defaults (Indonesian language and Auto theme) if no
     * preferences have been saved yet for this widget instance.
     *
     * @param context Context used to access [SharedPreferences].
     * @param appWidgetId The unique ID of the target widget instance.
     * @return The persisted [WidgetConfig], or default settings if unconfigured.
     */
    fun loadConfig(context: Context, appWidgetId: Int): WidgetConfig {
        val prefs = getPreferences(context)
        val languageKey = prefs.getString(KEY_PREFIX_LANGUAGE.format(appWidgetId), null)
        val themeKey = prefs.getString(KEY_PREFIX_THEME.format(appWidgetId), null)

        val language = WidgetLanguage.fromEditionKey(languageKey)
        val theme = WidgetTheme.fromStorageKey(themeKey)

        return WidgetConfig(language = language, theme = theme)
    }

    /**
     * Removes persisted configuration entries for a single [appWidgetId].
     *
     * Should be invoked when a widget instance is permanently removed from the home screen.
     *
     * @param context Context used to access [SharedPreferences].
     * @param appWidgetId The ID of the removed widget instance.
     */
    fun removeConfig(context: Context, appWidgetId: Int) {
        val prefs = getPreferences(context)
        prefs.edit()
            .remove(KEY_PREFIX_LANGUAGE.format(appWidgetId))
            .remove(KEY_PREFIX_THEME.format(appWidgetId))
            .apply()
    }

    /**
     * Removes persisted configuration entries for multiple [appWidgetIds] in a single transaction.
     *
     * Typically called from [android.appwidget.AppWidgetProvider.onDeleted].
     *
     * @param context Context used to access [SharedPreferences].
     * @param appWidgetIds Array of widget IDs to remove.
     */
    fun removeConfigs(context: Context, appWidgetIds: IntArray) {
        if (appWidgetIds.isEmpty()) return
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        for (id in appWidgetIds) {
            editor.remove(KEY_PREFIX_LANGUAGE.format(id))
            editor.remove(KEY_PREFIX_THEME.format(id))
        }
        editor.apply()
    }
}


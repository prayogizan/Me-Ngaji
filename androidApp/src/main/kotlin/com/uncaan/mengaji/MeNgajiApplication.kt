package com.uncaan.mengaji

import android.app.Application
import com.uncaan.mengaji.core.di.initKoin
import com.uncaan.mengaji.core.domain.model.AppConfig
import com.uncaan.mengaji.core.domain.model.AppEnvironment
import org.koin.android.ext.koin.androidContext

/**
 * Android [Application] subclass for the MeNgaji application.
 *
 * Initializes the global Koin dependency injection container during process startup.
 * Initializing Koin here guarantees that background components—such as Glance widget receivers,
 * action callbacks, and broadcast handlers—can safely resolve dependencies without requiring
 * [MainActivity] to be launched first.
 *
 * @see initKoin
 * @see AppConfig
 */
class MeNgajiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val environment = when (BuildConfig.FLAVOR.lowercase()) {
            "dev" -> AppEnvironment.DEV
            else -> AppEnvironment.PROD
        }

        val appConfig = AppConfig(
            environment = environment,
            appVersion = BuildConfig.VERSION_NAME,
            buildNumber = BuildConfig.VERSION_CODE,
            isDebug = BuildConfig.DEBUG
        )

        initKoin(appConfig = appConfig) {
            androidContext(this@MeNgajiApplication)
        }
    }
}

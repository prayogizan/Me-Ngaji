package com.uncaan.mengaji.core.domain.model

/**
 * Immutable application configuration holding build metadata and environment parameters.
 *
 * @property environment The active runtime environment ([AppEnvironment.DEV], [AppEnvironment.STAGING], or [AppEnvironment.PROD]).
 * @property appVersion The semantic version name of the application (e.g. `"0.0.1"` or `"0.0.1-dev"`).
 * @property buildNumber The incremental build version code.
 * @property baseUrl The base URL used for API networking.
 * @property isDebug Whether the application is running in debug mode.
 */
data class AppConfig(
    val environment: AppEnvironment,
    val appVersion: String,
    val buildNumber: Int,
    val baseUrl: String = "https://api.alquran.cloud/v1/",
    val isDebug: Boolean = false
) {
    companion object {
        /**
         * Returns default configuration tailored for development.
         *
         * @param appVersion Semantic version string. Defaults to `"0.0.1-dev"`.
         * @param buildNumber Build number integer. Defaults to `1`.
         * @param baseUrl Base endpoint URL. Defaults to Al-Quran Cloud API endpoint.
         * @return [AppConfig] initialized with [AppEnvironment.DEV].
         */
        fun dev(
            appVersion: String = "0.0.1-dev",
            buildNumber: Int = 1,
            baseUrl: String = "https://api.alquran.cloud/v1/"
        ): AppConfig = AppConfig(
            environment = AppEnvironment.DEV,
            appVersion = appVersion,
            buildNumber = buildNumber,
            baseUrl = baseUrl,
            isDebug = true
        )

        /**
         * Returns default configuration tailored for production.
         *
         * @param appVersion Semantic version string. Defaults to `"0.0.1"`.
         * @param buildNumber Build number integer. Defaults to `1`.
         * @param baseUrl Base endpoint URL. Defaults to Al-Quran Cloud API endpoint.
         * @return [AppConfig] initialized with [AppEnvironment.PROD].
         */
        fun prod(
            appVersion: String = "0.0.1",
            buildNumber: Int = 1,
            baseUrl: String = "https://api.alquran.cloud/v1/"
        ): AppConfig = AppConfig(
            environment = AppEnvironment.PROD,
            appVersion = appVersion,
            buildNumber = buildNumber,
            baseUrl = baseUrl,
            isDebug = false
        )
    }
}

package com.uncaan.mengaji.core.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [AppConfig] and [AppEnvironment] model initialization.
 */
class AppConfigTest {

    @Test
    fun testDevConfigurationDefaults() {
        val devConfig = AppConfig.dev()

        assertEquals(AppEnvironment.DEV, devConfig.environment)
        assertEquals("0.0.1-dev", devConfig.appVersion)
        assertEquals(1, devConfig.buildNumber)
        assertEquals("https://api.alquran.cloud/v1/", devConfig.baseUrl)
        assertTrue(devConfig.isDebug)
    }

    @Test
    fun testProdConfigurationDefaults() {
        val prodConfig = AppConfig.prod()

        assertEquals(AppEnvironment.PROD, prodConfig.environment)
        assertEquals("0.0.1", prodConfig.appVersion)
        assertEquals(1, prodConfig.buildNumber)
        assertEquals("https://api.alquran.cloud/v1/", prodConfig.baseUrl)
        assertFalse(prodConfig.isDebug)
    }

    @Test
    fun testCustomConfiguration() {
        val customConfig = AppConfig(
            environment = AppEnvironment.STAGING,
            appVersion = "0.0.1-staging",
            buildNumber = 42,
            baseUrl = "https://staging-api.alquran.cloud/v1/",
            isDebug = true
        )

        assertEquals(AppEnvironment.STAGING, customConfig.environment)
        assertEquals("0.0.1-staging", customConfig.appVersion)
        assertEquals(42, customConfig.buildNumber)
        assertEquals("https://staging-api.alquran.cloud/v1/", customConfig.baseUrl)
        assertTrue(customConfig.isDebug)
    }
}

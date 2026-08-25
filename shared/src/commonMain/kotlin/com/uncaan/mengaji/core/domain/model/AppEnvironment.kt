package com.uncaan.mengaji.core.domain.model

/**
 * Enumeration of application build environments and flavors.
 */
enum class AppEnvironment {
    /**
     * Local development and debug environment.
     */
    DEV,

    /**
     * Staging and QA verification environment.
     */
    STAGING,

    /**
     * Production release environment.
     */
    PROD
}

package com.uncaan.mengaji.core.di

import org.koin.core.module.Module

/**
 * Target-specific Koin dependency injection module.
 *
 * Implemented per platform (`androidMain` and `iosMain`) to register native platform
 * instances and drivers.
 */
expect val platformModule: Module

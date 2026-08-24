package com.uncaan.mengaji

import platform.UIKit.UIDevice

/**
 * iOS-specific implementation of the [Platform] abstraction.
 *
 * Resolves the iOS device system name and version via UIKit's [UIDevice].
 *
 * @property name iOS device system name and version (e.g. "iOS 17.5").
 */
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

/**
 * Creates and returns the [IOSPlatform] instance for the iOS target.
 *
 * @return The [IOSPlatform] instance.
 */
actual fun getPlatform(): Platform = IOSPlatform()
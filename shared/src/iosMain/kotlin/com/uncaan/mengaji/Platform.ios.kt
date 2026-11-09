package com.uncaan.mengaji

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
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

/**
 * iOS actual implementation returning epoch milliseconds via Foundation's [NSDate].
 *
 * @return Current Unix epoch time in milliseconds.
 */
actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()

package com.uncaan.mengaji

/**
 * Abstraction representing the target runtime platform for Compose Multiplatform.
 *
 * Exposes platform metadata such as operating system name and version.
 * Concrete implementations are provided per target in `androidMain` and `iosMain`.
 *
 * @property name A human-readable identifier of the host platform and OS version.
 */
interface Platform {
    val name: String
}

/**
 * Factory function returning the platform-specific [Platform] instance.
 *
 * Must be implemented by target source sets (`androidMain` and `iosMain`).
 *
 * @return The active platform descriptor instance.
 */
expect fun getPlatform(): Platform
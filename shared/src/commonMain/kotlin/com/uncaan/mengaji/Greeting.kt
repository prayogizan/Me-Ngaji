package com.uncaan.mengaji

/**
 * Demonstration helper class that generates platform-aware greeting messages.
 *
 * Utilizes [getPlatform] to detect the current operating system and formats
 * the output using [sayHello].
 *
 * @see getPlatform
 * @see sayHello
 */
class Greeting {
    private val platform = getPlatform()

    /**
     * Generates a greeting string incorporating the active platform's name.
     *
     * @return Formatted greeting string (e.g. "Hello, Android 34!").
     */
    fun greet(): String {
        return sayHello(platform.name)
    }
}
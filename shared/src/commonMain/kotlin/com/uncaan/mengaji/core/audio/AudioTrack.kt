package com.uncaan.mengaji.core.audio

/**
 * Immutable metadata and audio stream locator for a single Quran verse recitation track.
 *
 * Encapsulates the remote audio asset URL alongside Surah and Ayah positional identifiers
 * required for real-time reader verse tracking and automatic queue progression.
 *
 * @property url The remote HTTPS audio stream URL.
 * @property surahNumber The 1-based Surah index within the Quran (1..114).
 * @property ayahNumberInSurah The 1-based Ayah index within the specified Surah.
 * @property globalAyahNumber The sequential 1-based Ayah index across the entire Quran (1..6236).
 * @see AudioPlayer.playQueue
 * @see AudioState
 */
data class AudioTrack(
    val url: String,
    val surahNumber: Int,
    val ayahNumberInSurah: Int,
    val globalAyahNumber: Int
)


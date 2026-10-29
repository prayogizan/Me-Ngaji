package com.uncaan.mengaji.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destination for the Shalat Schedule screen.
 */
@Serializable
data object ShalatScheduleRoute

/**
 * Type-safe navigation destination for the Al-Quran reader screen.
 *
 * Supports optional [surahNumber] and [ayahNumber] parameters supplied via external deep links
 * (e.g., from home screen Glance widgets or notifications) to trigger automated search and verse focus.
 *
 * @property surahNumber Optional 1-indexed Surah number (1–114). Defaults to `null`.
 * @property ayahNumber Optional 1-indexed Ayah number within the Surah. Defaults to `null`.
 * @see AyahDeepLink
 */
@Serializable
data class AlQuranRoute(
    val surahNumber: Int? = null,
    val ayahNumber: Int? = null
)

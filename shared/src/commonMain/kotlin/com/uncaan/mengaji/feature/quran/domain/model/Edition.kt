package com.uncaan.mengaji.feature.quran.domain.model

/**
 * Domain entity representing a Quran edition, translation, or recitation provider.
 *
 * @property identifier Unique API edition slug (e.g. "en.sahih", "ar.alafasy").
 * @property language ISO language code or identifier (e.g. "en", "id", "ar").
 * @property name Native display name of the edition.
 * @property englishName English name of the edition or reciter.
 * @property format Data format type (e.g. "text", "audio").
 * @property type Edition category (e.g. "translation", "quran", "versebyverse").
 */
data class Edition(
    val identifier: String,
    val language: String,
    val name: String,
    val englishName: String,
    val format: String,
    val type: String
)

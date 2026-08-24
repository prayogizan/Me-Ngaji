package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object representing the edition metadata from the Quran API response.
 *
 * @property identifier The unique identifier slug for the edition (e.g. "quran-uthmani").
 * @property language ISO language code.
 * @property name Native edition title.
 * @property englishName English transliteration or description.
 * @property format Payload format ("text" or "audio").
 * @property type Content classification ("quran", "translation", "versebyverse").
 */
@Serializable
data class EditionDto(
    @SerialName("identifier") val identifier: String,
    @SerialName("language") val language: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("englishName") val englishName: String = "",
    @SerialName("format") val format: String = "",
    @SerialName("type") val type: String = ""
)

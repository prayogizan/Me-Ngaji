package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditionDto(
    @SerialName("identifier") val identifier: String,
    @SerialName("language") val language: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("englishName") val englishName: String = "",
    @SerialName("format") val format: String = "",
    @SerialName("type") val type: String = ""
)

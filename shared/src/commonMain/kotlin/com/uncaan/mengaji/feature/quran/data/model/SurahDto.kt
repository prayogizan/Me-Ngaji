package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurahDto(
    @SerialName("number") val number: Int,
    @SerialName("name") val name: String,
    @SerialName("englishName") val englishName: String,
    @SerialName("englishNameTranslation") val englishNameTranslation: String? = null,
    @SerialName("numberOfAyahs") val numberOfAyahs: Int? = null,
    @SerialName("revelationType") val revelationType: String? = null
)

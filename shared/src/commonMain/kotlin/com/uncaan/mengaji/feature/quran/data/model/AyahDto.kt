package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AyahDto(
    @SerialName("number") val number: Int,
    @SerialName("text") val text: String,
    @SerialName("audio") val audio: String? = null,
    @SerialName("audioSecondary") val audioSecondary: List<String> = emptyList(),
    @SerialName("edition") val edition: EditionDto? = null,
    @SerialName("surah") val surah: SurahDto,
    @SerialName("numberInSurah") val numberInSurah: Int,
    @SerialName("juz") val juz: Int,
    @SerialName("page") val page: Int? = null,
    @SerialName("hizbQuarter") val hizbQuarter: Int? = null
)

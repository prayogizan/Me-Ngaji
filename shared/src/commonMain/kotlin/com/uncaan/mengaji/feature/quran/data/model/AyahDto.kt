package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object representing a raw Ayah returned from the Al-Quran Cloud REST API.
 *
 * Mapped to the pure domain entity [com.uncaan.mengaji.feature.quran.domain.model.Ayah] via
 * [com.uncaan.mengaji.feature.quran.data.mapper.toDomain].
 *
 * @property number The global Ayah sequence number in the Quran.
 * @property text The verse text in the requested edition format.
 * @property audio The direct audio file URL, if present in the response.
 * @property audioSecondary Secondary audio URLs (such as alternative bitrate recitations).
 * @property edition Metadata regarding the edition/reciter for this DTO.
 * @property surah Surah chapter information.
 * @property numberInSurah The relative Ayah index within its parent Surah.
 * @property juz The Juz number.
 * @property page Physical Mushaf page number.
 * @property hizbQuarter Hizb quarter number.
 */
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

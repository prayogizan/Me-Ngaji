package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object representing Surah chapter details embedded in the API response.
 *
 * @property number Surah number (1–114).
 * @property name Arabic name of the Surah.
 * @property englishName English transliteration of the Surah name.
 * @property englishNameTranslation English meaning of the Surah name.
 * @property numberOfAyahs Total number of verses in the Surah.
 * @property revelationType Revelation place category ("Meccan" or "Medinan").
 */
@Serializable
data class SurahDto(
    @SerialName("number") val number: Int,
    @SerialName("name") val name: String,
    @SerialName("englishName") val englishName: String,
    @SerialName("englishNameTranslation") val englishNameTranslation: String? = null,
    @SerialName("numberOfAyahs") val numberOfAyahs: Int? = null,
    @SerialName("revelationType") val revelationType: String? = null
)

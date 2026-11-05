package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Top-level network response envelope for multi-edition Surah detail endpoints
 * (`GET /v1/surah/{surahNumber}/editions/quran-uthmani,{translationEdition},{audioEdition}`).
 *
 * @property code HTTP status code returned in the JSON payload (e.g., 200).
 * @property status Status message string (e.g., "OK").
 * @property data List of [SurahEditionDto] items corresponding to each requested edition.
 * @see SurahEditionDto
 */
@Serializable
data class SurahDetailResponseDto(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @SerialName("data") val data: List<SurahEditionDto>
)

/**
 * Data Transfer Object representing a single edition payload of a full Surah response.
 *
 * Contains the Surah's chapter metadata along with the complete array of [AyahDto] verses
 * for the associated [edition].
 *
 * @property number The 1-indexed Surah ordinal number (`1..114`).
 * @property name The original Uthmani Arabic script name of the Surah.
 * @property englishName The transliterated English name of the Surah.
 * @property englishNameTranslation The English meaning of the Surah name.
 * @property revelationType The revelation place classification string ("Meccan" or "Medinan").
 * @property numberOfAyahs The total number of verses in this Surah.
 * @property ayahs Ordered list of [AyahDto] verses belonging to this Surah in this edition.
 * @property edition Metadata describing the edition (e.g., `quran-uthmani`, `en.sahih`, `ar.alafasy`).
 * @see SurahDetailResponseDto
 * @see AyahDto
 * @see EditionDto
 */
@Serializable
data class SurahEditionDto(
    @SerialName("number") val number: Int,
    @SerialName("name") val name: String,
    @SerialName("englishName") val englishName: String,
    @SerialName("englishNameTranslation") val englishNameTranslation: String = "",
    @SerialName("revelationType") val revelationType: String = "",
    @SerialName("numberOfAyahs") val numberOfAyahs: Int = 0,
    @SerialName("ayahs") val ayahs: List<AyahDto> = emptyList(),
    @SerialName("edition") val edition: EditionDto? = null
)


package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Top-level network response envelope for the 114-Surah catalog endpoint (`GET /v1/surah`).
 *
 * @property code HTTP status code returned in the JSON payload (e.g., 200).
 * @property status Status message string (e.g., "OK").
 * @property data Ordered list of [SurahSummaryDto] items representing all 114 Surahs.
 * @see SurahSummaryDto
 */
@Serializable
data class SurahListResponseDto(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @SerialName("data") val data: List<SurahSummaryDto>
)

/**
 * Data Transfer Object representing a single Surah summary item returned by the Al-Quran Cloud API.
 *
 * Mapped to the pure domain entity [com.uncaan.mengaji.feature.quran.domain.model.SurahSummary]
 * via [com.uncaan.mengaji.feature.quran.data.mapper.toDomain].
 *
 * @property number The 1-indexed Surah ordinal number (`1..114`).
 * @property name The original Uthmani Arabic script name of the Surah.
 * @property englishName The transliterated English name of the Surah.
 * @property englishNameTranslation The English meaning of the Surah name.
 * @property numberOfAyahs The total number of verses in the Surah.
 * @property revelationType The revelation place classification string ("Meccan" or "Medinan").
 */
@Serializable
data class SurahSummaryDto(
    @SerialName("number") val number: Int,
    @SerialName("name") val name: String,
    @SerialName("englishName") val englishName: String,
    @SerialName("englishNameTranslation") val englishNameTranslation: String = "",
    @SerialName("numberOfAyahs") val numberOfAyahs: Int = 0,
    @SerialName("revelationType") val revelationType: String = ""
)


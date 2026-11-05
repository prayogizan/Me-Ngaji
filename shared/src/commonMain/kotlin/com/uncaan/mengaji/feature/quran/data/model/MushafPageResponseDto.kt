package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Top-level network response envelope for multi-edition Medina Mushaf page endpoints
 * (`GET /v1/page/{pageNumber}/editions/quran-uthmani,{translationEdition}`).
 *
 * @property code HTTP status code returned in the JSON payload (e.g., 200).
 * @property status Status message string (e.g., "OK").
 * @property data List of [MushafPageEditionDto] items corresponding to each requested edition.
 * @see MushafPageEditionDto
 */
@Serializable
data class MushafPageResponseDto(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @SerialName("data") val data: List<MushafPageEditionDto>
)

/**
 * Data Transfer Object representing a single edition payload of a Medina Mushaf page (`1..604`).
 *
 * @property number The 1-indexed Medina Mushaf page number (`1..604`).
 * @property ayahs Ordered list of [AyahDto] verses appearing on this Mushaf page.
 * @property surahs Map of Surah number strings to [SurahSummaryDto] metadata for Surahs present on this page.
 * @property edition Metadata describing the edition (e.g., `quran-uthmani`, `en.sahih`).
 * @see MushafPageResponseDto
 * @see AyahDto
 * @see SurahSummaryDto
 * @see EditionDto
 */
@Serializable
data class MushafPageEditionDto(
    @SerialName("number") val number: Int,
    @SerialName("ayahs") val ayahs: List<AyahDto> = emptyList(),
    @SerialName("surahs") val surahs: Map<String, SurahSummaryDto> = emptyMap(),
    @SerialName("edition") val edition: EditionDto? = null
)


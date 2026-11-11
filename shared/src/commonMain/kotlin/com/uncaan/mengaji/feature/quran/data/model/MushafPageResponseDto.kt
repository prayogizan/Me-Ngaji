package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * Top-level network response envelope for Medina Mushaf page endpoints
 * (`GET /v1/page/{pageNumber}/{edition}`).
 *
 * Uses [MushafPageEditionListSerializer] on [data] to seamlessly deserialize both single-edition
 * object payloads returned by `GET /v1/page/{pageNumber}/{edition}` and multi-edition array payloads.
 *
 * @property code HTTP status code returned in the JSON payload (e.g., 200).
 * @property status Status message string (e.g., "OK").
 * @property data List of [MushafPageEditionDto] items corresponding to each requested edition.
 * @see MushafPageEditionDto
 * @see MushafPageEditionListSerializer
 */
@Serializable
data class MushafPageResponseDto(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @Serializable(with = MushafPageEditionListSerializer::class)
    @SerialName("data") val data: List<MushafPageEditionDto>
)

/**
 * Custom [JsonTransformingSerializer] that normalizes the `data` field of [MushafPageResponseDto].
 *
 * The Al-Quran Cloud REST API v1 returns a single JSON object for `GET /v1/page/{page}/{edition}`,
 * whereas multi-edition test fixtures may provide a JSON array. This serializer wraps a single
 * JSON object into a single-element [JsonArray] during deserialization.
 */
object MushafPageEditionListSerializer :
    JsonTransformingSerializer<List<MushafPageEditionDto>>(
        ListSerializer(MushafPageEditionDto.serializer())
    ) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return if (element is JsonArray) element else JsonArray(listOf(element))
    }
}

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


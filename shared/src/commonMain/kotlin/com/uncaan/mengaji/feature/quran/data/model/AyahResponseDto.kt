package com.uncaan.mengaji.feature.quran.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response wrapper for single-edition Ayah API endpoints.
 *
 * @property code HTTP status code returned in the JSON payload.
 * @property status Status message string (e.g. "OK").
 * @property data The [AyahDto] payload.
 */
@Serializable
data class AyahResponseDto(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @SerialName("data") val data: AyahDto
)

/**
 * Response wrapper for multi-edition Ayah API endpoints (e.g. parallel Arabic + translation + audio).
 *
 * @property code HTTP status code returned in the JSON payload.
 * @property status Status message string (e.g. "OK").
 * @property data List of [AyahDto] items corresponding to each requested edition.
 */
@Serializable
data class AyahEditionsResponseDto(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @SerialName("data") val data: List<AyahDto>
)

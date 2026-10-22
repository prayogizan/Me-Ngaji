package com.uncaan.mengaji.feature.widget.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Top-level network response envelope returned by the Al-Quran Cloud API for multi-edition requests.
 *
 * This DTO represents the response payload for:
 * `GET /v1/ayah/random/editions/quran-uthmani,id.indonesian`
 *
 * @property code HTTP response status code reported in body (e.g., 200).
 * @property status Response status message (e.g., "OK").
 * @property data List of editions for the requested random Ayah.
 */
@Serializable
data class DailyAyahResponseDto(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @SerialName("data") val data: List<DailyAyahEditionDto>
)

/**
 * Data Transfer Object representing an individual edition item within the Ayah response array.
 *
 * @property number Global verse index across the whole Quran (1–6236).
 * @property text The verse text corresponding to this edition.
 * @property edition Metadata regarding the text edition (script or translation).
 * @property surah Surah metadata enclosing this Ayah.
 * @property numberInSurah The Ayah's position within its Surah.
 * @property juz The Juz index containing this verse.
 */
@Serializable
data class DailyAyahEditionDto(
    @SerialName("number") val number: Int,
    @SerialName("text") val text: String,
    @SerialName("edition") val edition: DailyAyahEditionDetailDto,
    @SerialName("surah") val surah: DailyAyahSurahDto,
    @SerialName("numberInSurah") val numberInSurah: Int,
    @SerialName("juz") val juz: Int = 1
)

/**
 * Details of the edition source (recitation, translation, or script style).
 *
 * @property identifier Unique edition key (e.g., "quran-uthmani", "id.indonesian").
 * @property language ISO language code (e.g., "ar", "id").
 * @property name Human-readable localized edition title.
 * @property englishName English name of the edition.
 * @property format Content format (e.g., "text").
 * @property type Content type classification (e.g., "quran", "translation").
 * @property direction Reading direction ("rtl" or "ltr").
 */
@Serializable
data class DailyAyahEditionDetailDto(
    @SerialName("identifier") val identifier: String,
    @SerialName("language") val language: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("englishName") val englishName: String? = null,
    @SerialName("format") val format: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("direction") val direction: String? = null
)

/**
 * Surah metadata payload nested inside an Ayah edition response.
 *
 * @property number 1-indexed Surah number (1–114).
 * @property name Arabic name of the Surah.
 * @property englishName Latinized English name of the Surah.
 * @property englishNameTranslation English meaning of the Surah title.
 * @property numberOfAyahs Total number of verses within the Surah.
 * @property revelationType City of revelation ("Meccan" or "Medinan").
 */
@Serializable
data class DailyAyahSurahDto(
    @SerialName("number") val number: Int,
    @SerialName("name") val name: String,
    @SerialName("englishName") val englishName: String,
    @SerialName("englishNameTranslation") val englishNameTranslation: String? = null,
    @SerialName("numberOfAyahs") val numberOfAyahs: Int? = null,
    @SerialName("revelationType") val revelationType: String? = null
)

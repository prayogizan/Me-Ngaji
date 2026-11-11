package com.uncaan.mengaji.feature.quran.data.remote

import com.uncaan.mengaji.feature.quran.data.model.AyahEditionsResponseDto
import com.uncaan.mengaji.feature.quran.data.model.MushafPageResponseDto
import com.uncaan.mengaji.feature.quran.data.model.SurahDetailResponseDto
import com.uncaan.mengaji.feature.quran.data.model.SurahListResponseDto
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Remote API service interface for querying the Al-Quran Cloud REST API v1.
 *
 * Defines operations for fetching single verses, the 114-Surah catalog, full multi-edition
 * Surahs, and 604-page Medina Mushaf pages.
 *
 * @see QuranApiServiceImpl
 */
interface QuranApiService {

    /**
     * Queries multi-edition Ayah data including Arabic text, requested translation, and recitation audio.
     *
     * @param reference The Ayah reference query (e.g. "2:255" or global verse index).
     * @param translationEdition The target translation identifier (e.g. "en.sahih"). Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @param recitationEdition The target recitation audio identifier (e.g. "ar.alafasy"). Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
     * @return [AyahEditionsResponseDto] containing a list of [com.uncaan.mengaji.feature.quran.data.model.AyahDto] per requested edition.
     */
    suspend fun getAyah(
        reference: String,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        recitationEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ): AyahEditionsResponseDto

    /**
     * Queries the complete 114-Surah catalog from `GET /v1/surah`.
     *
     * @return [SurahListResponseDto] containing metadata for all 114 Surahs.
     */
    suspend fun getSurahList(): SurahListResponseDto

    /**
     * Queries a complete Surah with parallel Uthmani Arabic, translation, and audio editions
     * from `GET /v1/surah/{surahNumber}/editions/quran-uthmani,{translationEdition},{audioEdition}`.
     *
     * @param surahNumber The 1-indexed Surah number (`1..114`).
     * @param translationEdition The target translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @param audioEdition The target audio recitation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
     * @return [SurahDetailResponseDto] wrapping the multi-edition Surah arrays.
     */
    suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        audioEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ): SurahDetailResponseDto

    /**
     * Queries a Medina Mushaf page (`1..604`) with parallel Uthmani Arabic and translation editions
     * from `GET /v1/page/{pageNumber}/quran-uthmani` and `GET /v1/page/{pageNumber}/{translationEdition}`.
     *
     * @param pageNumber The 1-indexed Mushaf page number (`1..604`).
     * @param translationEdition The target translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @return [MushafPageResponseDto] wrapping the combined Mushaf page edition payloads.
     */
    suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION
    ): MushafPageResponseDto
}

/**
 * Default Ktor implementation of [QuranApiService].
 *
 * Executes HTTP GET requests against the Al-Quran Cloud REST API v1 and deserializes
 * JSON payloads via `kotlinx.serialization`.
 *
 * @param httpClient The configured Ktor [HttpClient] instance.
 * @see QuranApiService
 */
class QuranApiServiceImpl(
    private val httpClient: HttpClient
) : QuranApiService {

    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AyahEditionsResponseDto {
        return httpClient.get("ayah/$reference/editions/quran-uthmani,$translationEdition,$recitationEdition").body()
    }

    override suspend fun getSurahList(): SurahListResponseDto {
        return httpClient.get("surah").body()
    }

    override suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String,
        audioEdition: String
    ): SurahDetailResponseDto {
        return httpClient.get(
            "surah/$surahNumber/editions/quran-uthmani,$translationEdition,$audioEdition"
        ).body()
    }

    override suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String
    ): MushafPageResponseDto = coroutineScope {
        val arabicDeferred = async {
            httpClient.get("page/$pageNumber/quran-uthmani").body<MushafPageResponseDto>()
        }
        val translationDeferred = async {
            val trimmedEdition = translationEdition.trim()
            if (trimmedEdition.isNotBlank() && trimmedEdition != "quran-uthmani") {
                runCatching {
                    httpClient.get("page/$pageNumber/$trimmedEdition").body<MushafPageResponseDto>()
                }.getOrNull()
            } else {
                null
            }
        }

        val arabicResponse = arabicDeferred.await()
        val translationResponse = translationDeferred.await()

        val combinedEditions = buildList {
            addAll(arabicResponse.data)
            translationResponse?.data?.forEach { editionDto ->
                val editionId = editionDto.edition?.identifier
                if (editionId == null || none { it.edition?.identifier == editionId }) {
                    add(editionDto)
                }
            }
        }

        arabicResponse.copy(data = combinedEditions)
    }
}

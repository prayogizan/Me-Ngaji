package com.uncaan.mengaji.feature.quran.data.repository

import com.uncaan.mengaji.core.data.network.safeApiCall
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.core.domain.model.map
import com.uncaan.mengaji.feature.quran.data.mapper.toDomain
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiService
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of [QuranRepository] communicating with the remote API service.
 *
 * Dispatches all network operations on the given [ioDispatcher] and safely executes
 * calls using [safeApiCall], mapping DTO responses to pure domain models. Also maintains
 * an in-memory cache of the 114-Surah catalog to avoid redundant network requests unless
 * `forceRefresh` is requested.
 *
 * @property apiService The remote Quran API service.
 * @property ioDispatcher The [CoroutineDispatcher] for I/O operations (defaults to [Dispatchers.IO]).
 * @see QuranRepository
 * @see QuranApiService
 */
class QuranRepositoryImpl(
    private val apiService: QuranApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : QuranRepository {

    private var cachedSurahList: List<SurahSummary>? = null

    /**
     * Fetches and maps an Ayah from the remote API into the domain [Ayah] model.
     *
     * @param reference The Ayah reference query.
     * @param translationEdition Target translation edition identifier.
     * @param recitationEdition Target audio recitation edition identifier.
     * @return [AppResult.Success] containing the domain [Ayah], or [AppResult.Error] on network or parsing failures.
     */
    override suspend fun getAyah(
        reference: String,
        translationEdition: String,
        recitationEdition: String
    ): AppResult<Ayah> {
        return withContext(ioDispatcher) {
            safeApiCall {
                apiService.getAyah(reference, translationEdition, recitationEdition)
            }.map { response ->
                response.data.toDomain()
            }
        }
    }

    /**
     * Fetches and maps the 114-Surah catalog from the remote API into a list of [SurahSummary] domain models.
     *
     * Returns the in-memory cached catalog immediately when [forceRefresh] is `false` and the cache
     * has already been populated.
     *
     * @param forceRefresh When `true`, bypasses the in-memory cache and queries the remote endpoint.
     * @return [AppResult.Success] containing the 114 [SurahSummary] entries, or [AppResult.Error] on failure.
     */
    override suspend fun getSurahList(
        forceRefresh: Boolean
    ): AppResult<List<SurahSummary>> {
        if (!forceRefresh) {
            cachedSurahList?.let { cached ->
                if (cached.isNotEmpty()) {
                    return AppResult.Success(cached)
                }
            }
        }

        return withContext(ioDispatcher) {
            safeApiCall {
                apiService.getSurahList()
            }.map { response ->
                val mapped = response.data.map { it.toDomain() }
                if (mapped.isNotEmpty()) {
                    cachedSurahList = mapped
                }
                mapped
            }
        }
    }

    /**
     * Fetches a complete Surah with parallel Uthmani Arabic, translation, and audio editions,
     * merging them into a single [SurahDetail] domain entity.
     *
     * @param surahNumber The 1-indexed Surah number (`1..114`).
     * @param translationEdition Target translation edition identifier.
     * @param audioEdition Target audio recitation edition identifier.
     * @return [AppResult.Success] containing the merged [SurahDetail], or [AppResult.Error] on failure.
     */
    override suspend fun getSurahDetail(
        surahNumber: Int,
        translationEdition: String,
        audioEdition: String
    ): AppResult<SurahDetail> {
        return withContext(ioDispatcher) {
            safeApiCall {
                apiService.getSurahDetail(
                    surahNumber = surahNumber,
                    translationEdition = translationEdition,
                    audioEdition = audioEdition
                )
            }.map { response ->
                response.data.toDomain(
                    translationEdition = translationEdition,
                    audioEdition = audioEdition
                )
            }
        }
    }

    /**
     * Fetches a Medina Mushaf page (`1..604`) with parallel Uthmani Arabic and translation editions,
     * merging them into a single [MushafPage] domain entity.
     *
     * @param pageNumber The 1-indexed Mushaf page number (`1..604`).
     * @param translationEdition Target translation edition identifier.
     * @return [AppResult.Success] containing the merged [MushafPage], or [AppResult.Error] on failure.
     */
    override suspend fun getMushafPage(
        pageNumber: Int,
        translationEdition: String
    ): AppResult<MushafPage> {
        return withContext(ioDispatcher) {
            safeApiCall {
                apiService.getMushafPage(
                    pageNumber = pageNumber,
                    translationEdition = translationEdition
                )
            }.map { response ->
                response.data.toDomain(
                    translationEdition = translationEdition
                )
            }
        }
    }
}

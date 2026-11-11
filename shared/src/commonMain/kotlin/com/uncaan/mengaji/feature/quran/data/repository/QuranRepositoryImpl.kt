package com.uncaan.mengaji.feature.quran.data.repository

import com.uncaan.mengaji.core.data.network.safeApiCall
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.core.domain.model.map
import com.uncaan.mengaji.feature.quran.data.local.LocalRecitationCache
import com.uncaan.mengaji.feature.quran.data.mapper.toDomain
import com.uncaan.mengaji.feature.quran.data.mapper.toDto
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiService
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.AyahBookmark
import com.uncaan.mengaji.feature.quran.domain.model.LastReadProgress
import com.uncaan.mengaji.feature.quran.domain.model.MushafPage
import com.uncaan.mengaji.feature.quran.domain.model.SurahDetail
import com.uncaan.mengaji.feature.quran.domain.model.SurahSummary
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of [QuranRepository] communicating with the remote API service
 * and local persistence cache.
 *
 * Dispatches all network and persistence operations on the given [ioDispatcher] and safely executes
 * calls using [safeApiCall], mapping DTO responses to pure domain models. Also maintains
 * an in-memory cache and a persistent offline fallback cache of the 114-Surah catalog via [localCache]
 * to avoid redundant network requests unless `forceRefresh` is requested.
 *
 * @property apiService The remote Quran API service.
 * @property localCache Local cache abstraction for recitation progress, bookmarks, and catalog persistence.
 * @property ioDispatcher The [CoroutineDispatcher] for I/O operations (defaults to [Dispatchers.IO]).
 * @see QuranRepository
 * @see QuranApiService
 * @see LocalRecitationCache
 */
class QuranRepositoryImpl(
    private val apiService: QuranApiService,
    private val localCache: LocalRecitationCache,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : QuranRepository {

    private var cachedSurahList: List<SurahSummary>? = null
    private val cachedMushafPages = mutableMapOf<Pair<Int, String>, MushafPage>()

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
     * has already been populated. On successful network responses, persists the catalog into [localCache]
     * for offline fallback. If network retrieval fails, seamlessly falls back to the locally cached catalog.
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
            val networkResult = safeApiCall {
                apiService.getSurahList()
            }

            when (networkResult) {
                is AppResult.Success -> {
                    val rawDtos = networkResult.data.data
                    if (rawDtos.isNotEmpty()) {
                        localCache.cacheSurahCatalog(rawDtos)
                    }
                    val mapped = rawDtos.map { it.toDomain() }
                    if (mapped.isNotEmpty()) {
                        cachedSurahList = mapped
                    }
                    AppResult.Success(mapped)
                }
                is AppResult.Error -> {
                    val offlineCached = localCache.getCachedSurahCatalog()
                    if (!offlineCached.isNullOrEmpty()) {
                        val mapped = offlineCached.map { it.toDomain() }
                        cachedSurahList = mapped
                        AppResult.Success(mapped)
                    } else {
                        networkResult
                    }
                }
                is AppResult.Loading -> networkResult
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
        val cacheKey = pageNumber to translationEdition
        cachedMushafPages[cacheKey]?.let { cached ->
            return AppResult.Success(cached)
        }

        return withContext(ioDispatcher) {
            safeApiCall {
                apiService.getMushafPage(
                    pageNumber = pageNumber,
                    translationEdition = translationEdition
                )
            }.map { response ->
                response.data.toDomain(
                    translationEdition = translationEdition
                ).also { domainPage ->
                    cachedMushafPages[cacheKey] = domainPage
                }
            }
        }
    }

    /**
     * Retrieves the user's last recorded recitation progress, or `null` if none has been saved.
     *
     * @return The persisted [LastReadProgress] domain model, or `null` if no progress is recorded.
     */
    override suspend fun getLastReadProgress(): LastReadProgress? {
        return withContext(ioDispatcher) {
            localCache.getLastReadProgress()?.toDomain()
        }
    }

    /**
     * Persists the user's current recitation progress into local storage.
     *
     * @param progress The [LastReadProgress] domain model to store.
     */
    override suspend fun saveLastReadProgress(progress: LastReadProgress) {
        withContext(ioDispatcher) {
            localCache.saveLastReadProgress(progress.toDto())
        }
    }

    /**
     * Retrieves all saved ayah bookmarks, sorted newest first ([AyahBookmark.savedAtEpochMillis] descending).
     *
     * @return List of persisted [AyahBookmark] entities, or an empty list if none are saved.
     */
    override suspend fun getBookmarks(): List<AyahBookmark> {
        return withContext(ioDispatcher) {
            localCache.getBookmarks()
                .map { it.toDomain() }
                .sortedByDescending { it.savedAtEpochMillis }
        }
    }

    /**
     * Toggles the bookmark state for an Ayah.
     *
     * If a bookmark with matching [AyahBookmark.globalAyahNumber] exists, it is removed and `false` is returned.
     * If not present, it is added to the bookmarks list and `true` is returned.
     * Persisted bookmarks remain sorted descending by [AyahBookmark.savedAtEpochMillis].
     *
     * @param bookmark The [AyahBookmark] to add or remove.
     * @return `true` if the bookmark was added, `false` if it was removed.
     */
    override suspend fun toggleBookmark(bookmark: AyahBookmark): Boolean {
        return withContext(ioDispatcher) {
            val currentBookmarks = localCache.getBookmarks().toMutableList()
            val index = currentBookmarks.indexOfFirst { it.globalAyahNumber == bookmark.globalAyahNumber }
            val wasAdded: Boolean

            if (index >= 0) {
                currentBookmarks.removeAt(index)
                wasAdded = false
            } else {
                currentBookmarks.add(bookmark.toDto())
                wasAdded = true
            }

            val sorted = currentBookmarks.sortedByDescending { it.savedAtEpochMillis }
            localCache.saveBookmarks(sorted)
            wasAdded
        }
    }
}

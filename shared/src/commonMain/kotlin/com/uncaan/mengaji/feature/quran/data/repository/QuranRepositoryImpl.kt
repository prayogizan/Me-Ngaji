package com.uncaan.mengaji.feature.quran.data.repository

import com.uncaan.mengaji.core.data.network.safeApiCall
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.core.domain.model.map
import com.uncaan.mengaji.feature.quran.data.mapper.toDomain
import com.uncaan.mengaji.feature.quran.data.remote.QuranApiService
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.repository.QuranRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of [QuranRepository] communicating with the remote API service.
 *
 * Dispatches all network operations on the given [ioDispatcher] and safely executes
 * calls using [safeApiCall], mapping DTO responses to domain models.
 *
 * @property apiService The remote Quran API service.
 * @property ioDispatcher The CoroutineDispatcher for I/O operations (defaults to [Dispatchers.IO]).
 * @see QuranRepository
 * @see QuranApiService
 */
class QuranRepositoryImpl(
    private val apiService: QuranApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : QuranRepository {

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
}

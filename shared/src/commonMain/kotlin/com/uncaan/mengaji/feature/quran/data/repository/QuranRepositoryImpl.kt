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

class QuranRepositoryImpl(
    private val apiService: QuranApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : QuranRepository {

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

package com.uncaan.mengaji.feature.widget.data.repository

import com.uncaan.mengaji.core.data.network.safeApiCall
import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.core.domain.model.map
import com.uncaan.mengaji.feature.widget.data.mapper.toDailyAyah
import com.uncaan.mengaji.feature.widget.data.remote.DailyAyahApiService
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.feature.widget.domain.repository.DailyAyahRepository
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Concrete implementation of [DailyAyahRepository] communicating with [DailyAyahApiService].
 *
 * Dispatches all network operations on [ioDispatcher] and wraps HTTP execution in [safeApiCall],
 * mapping multi-edition DTO responses to domain [DailyAyah] entities stamped with the current calendar date.
 *
 * @param apiService Remote service fetching the multi-edition quote payload.
 * @param ioDispatcher CoroutineDispatcher for I/O tasks. Defaults to [Dispatchers.IO].
 * @param currentDateProvider Supplier for current date string ("yyyy-MM-dd"). Defaults to system clock.
 * @see DailyAyahRepository
 * @see DailyAyahApiService
 */
class DailyAyahRepositoryImpl(
    private val apiService: DailyAyahApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val currentDateProvider: () -> String = {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    }
) : DailyAyahRepository {

    /**
     * Retrieves a random daily Ayah from the API and maps it to a [DailyAyah] domain model.
     *
     * @return [AppResult.Success] containing the mapped [DailyAyah], or [AppResult.Error] on failure.
     */
    override suspend fun getDailyAyah(): AppResult<DailyAyah> {
        return withContext(ioDispatcher) {
            safeApiCall {
                apiService.getRandomDailyAyah()
            }.map { response ->
                val today = currentDateProvider()
                response.data.toDailyAyah(fetchedDate = today)
            }
        }
    }
}

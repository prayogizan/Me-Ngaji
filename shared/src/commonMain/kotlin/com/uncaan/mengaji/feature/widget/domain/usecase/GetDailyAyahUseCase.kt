package com.uncaan.mengaji.feature.widget.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.widget.data.local.LocalAyahCache
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.feature.widget.domain.repository.DailyAyahRepository
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Single-responsibility Use Case for orchestrating daily Ayah retrieval.
 *
 * Implements a cache-first strategy with network fallback:
 * 1. Checks [LocalAyahCache] for an Ayah cached on the current calendar date.
 * 2. If valid cache exists for today, returns [AppResult.Success] immediately without hitting the network.
 * 3. If cache is missing or from a previous day, invokes [DailyAyahRepository.getDailyAyah].
 * 4. On successful network response, updates [LocalAyahCache] with the new Ayah and returns it.
 * 5. On network failure, falls back to the most recent cached Ayah (regardless of date). If no cache exists, propagates the original [AppResult.Error].
 *
 * @param repository Remote repository for fetching daily verses.
 * @param cache Local cache for storing and validating daily verses.
 * @param currentDateProvider Optional lambda providing the current ISO-8601 date string ("yyyy-MM-dd"). Defaults to system clock.
 * @see DailyAyahRepository
 * @see LocalAyahCache
 */
class GetDailyAyahUseCase(
    private val repository: DailyAyahRepository,
    private val cache: LocalAyahCache,
    private val currentDateProvider: () -> String = {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    }
) {

    /**
     * Executes the daily Ayah retrieval workflow.
     *
     * @return [AppResult.Success] containing the current or cached [DailyAyah], or [AppResult.Error] on unrecoverable failure.
     */
    suspend operator fun invoke(): AppResult<DailyAyah> {
        val today = currentDateProvider()
        val cachedDate = cache.getCacheDate()
        val cachedAyah = cache.getCachedAyah()

        // Cache hit: today's quote is already saved
        if (cachedDate == today && cachedAyah != null) {
            return AppResult.Success(cachedAyah)
        }

        // Cache miss or expired: fetch from network
        return when (val networkResult = repository.getDailyAyah()) {
            is AppResult.Success -> {
                cache.cacheAyah(networkResult.data)
                networkResult
            }
            is AppResult.Error -> {
                // Fallback to any previously cached Ayah if available
                if (cachedAyah != null) {
                    AppResult.Success(cachedAyah)
                } else {
                    networkResult
                }
            }
            is AppResult.Loading -> networkResult
        }
    }
}

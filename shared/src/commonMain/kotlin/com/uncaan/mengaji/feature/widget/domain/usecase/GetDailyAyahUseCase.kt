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
     * When [forceRefresh] is `false` (default):
     * 1. Checks [LocalAyahCache] for an Ayah cached on the current calendar date.
     * 2. If valid cache exists for today, returns [AppResult.Success] immediately without hitting the network.
     * 3. If cache is missing or from a previous day, invokes [DailyAyahRepository.getDailyAyah].
     * 4. On successful network response, updates [LocalAyahCache] with the new Ayah and returns it.
     * 5. On network failure, falls back to the most recent cached Ayah (regardless of date) if available,
     *    or propagates [AppResult.Error] if cache is completely empty.
     *
     * When [forceRefresh] is `true`:
     * 1. Bypasses the cache date validation check and directly invokes [DailyAyahRepository.getDailyAyah].
     * 2. On successful network response, persists the new Ayah to [LocalAyahCache] and returns it.
     * 3. On network failure, directly propagates [AppResult.Error] without falling back to stale cache,
     *    enabling background workers or refresh actions to detect the failure and schedule retries.
     *
     * @param forceRefresh When `true`, forces a fresh network retrieval bypassing today's cache check.
     *                     Defaults to `false`.
     * @return [AppResult.Success] containing the current or cached [DailyAyah], or [AppResult.Error] on failure.
     */
    suspend operator fun invoke(forceRefresh: Boolean = false): AppResult<DailyAyah> {
        val today = currentDateProvider()
        val cachedDate = cache.getCacheDate()
        val cachedAyah = cache.getCachedAyah()

        // Cache hit: today's quote is already saved (bypassed if forceRefresh is true)
        if (!forceRefresh && cachedDate == today && cachedAyah != null) {
            return AppResult.Success(cachedAyah)
        }

        // Cache miss, expired, or force refresh: fetch from network
        return when (val networkResult = repository.getDailyAyah()) {
            is AppResult.Success -> {
                cache.cacheAyah(networkResult.data)
                networkResult
            }
            is AppResult.Error -> {
                // Fallback to any previously cached Ayah only if NOT force refreshing
                if (!forceRefresh && cachedAyah != null) {
                    AppResult.Success(cachedAyah)
                } else {
                    networkResult
                }
            }
            is AppResult.Loading -> networkResult
        }
    }
}

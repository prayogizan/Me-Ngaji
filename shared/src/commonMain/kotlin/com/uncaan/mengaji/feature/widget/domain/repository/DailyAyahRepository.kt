package com.uncaan.mengaji.feature.widget.domain.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah

/**
 * Domain contract for retrieving daily Ayah quote data.
 *
 * Implementations reside in the data layer and are registered via Koin DI.
 * Consumers (Use Cases) depend strictly on this abstraction.
 *
 * @see com.uncaan.mengaji.feature.widget.data.repository.DailyAyahRepositoryImpl
 */
interface DailyAyahRepository {

    /**
     * Fetches a random daily Ayah containing paired Arabic text and the requested translation edition.
     *
     * @param edition Identifier of the translation edition (e.g., "id.indonesian", "en.asad").
     *                Defaults to `"id.indonesian"`.
     * @return [AppResult.Success] containing the mapped [DailyAyah] domain entity,
     *         or [AppResult.Error] encapsulating network or serialization failures.
     */
    suspend fun getDailyAyah(edition: String = "id.indonesian"): AppResult<DailyAyah>
}

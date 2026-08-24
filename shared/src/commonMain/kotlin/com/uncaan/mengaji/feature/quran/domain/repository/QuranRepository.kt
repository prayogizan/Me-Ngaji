package com.uncaan.mengaji.feature.quran.domain.repository

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.quran.domain.model.Ayah
import com.uncaan.mengaji.feature.quran.domain.model.QuranEditionPresets

/**
 * Contract defining the data access operations for the Quran feature.
 *
 * Implemented in the data layer by [com.uncaan.mengaji.feature.quran.data.repository.QuranRepositoryImpl]
 * and injected into Use Cases via Koin.
 */
interface QuranRepository {

    /**
     * Retrieves a single Ayah with parallel text and audio editions.
     *
     * @param reference Ayah reference formatted as `surah:ayah` (e.g. "2:255") or global index (e.g. "262").
     * @param translationEdition Translation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_TRANSLATION].
     * @param recitationEdition Audio recitation edition identifier. Defaults to [QuranEditionPresets.DEFAULT_RECITATION].
     * @return [AppResult.Success] containing the domain [Ayah] model, or [AppResult.Error] on failure.
     */
    suspend fun getAyah(
        reference: String,
        translationEdition: String = QuranEditionPresets.DEFAULT_TRANSLATION,
        recitationEdition: String = QuranEditionPresets.DEFAULT_RECITATION
    ): AppResult<Ayah>
}

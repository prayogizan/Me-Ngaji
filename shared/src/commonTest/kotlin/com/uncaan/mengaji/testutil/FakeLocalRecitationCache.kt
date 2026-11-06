package com.uncaan.mengaji.testutil

import com.uncaan.mengaji.feature.quran.data.local.LocalRecitationCache
import com.uncaan.mengaji.feature.quran.data.model.AyahBookmarkDto
import com.uncaan.mengaji.feature.quran.data.model.LastReadProgressDto
import com.uncaan.mengaji.feature.quran.data.model.SurahSummaryDto

/**
 * In-memory test double for [LocalRecitationCache] used in unit tests.
 */
class FakeLocalRecitationCache : LocalRecitationCache {

    var storedLastReadProgress: LastReadProgressDto? = null
    val storedBookmarks: MutableList<AyahBookmarkDto> = mutableListOf()
    var storedCatalog: List<SurahSummaryDto>? = null

    override fun getLastReadProgress(): LastReadProgressDto? = storedLastReadProgress

    override fun saveLastReadProgress(progress: LastReadProgressDto) {
        storedLastReadProgress = progress
    }

    override fun getBookmarks(): List<AyahBookmarkDto> = storedBookmarks.toList()

    override fun saveBookmarks(bookmarks: List<AyahBookmarkDto>) {
        storedBookmarks.clear()
        storedBookmarks.addAll(bookmarks)
    }

    override fun getCachedSurahCatalog(): List<SurahSummaryDto>? = storedCatalog

    override fun cacheSurahCatalog(catalog: List<SurahSummaryDto>) {
        storedCatalog = catalog
    }

    override fun clear() {
        storedLastReadProgress = null
        storedBookmarks.clear()
        storedCatalog = null
    }
}

package com.uncaan.mengaji.feature.widget.domain.usecase

import com.uncaan.mengaji.core.domain.model.AppResult
import com.uncaan.mengaji.feature.widget.data.local.LocalAyahCache
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.feature.widget.domain.repository.DailyAyahRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetDailyAyahUseCaseTest {

    private val sampleAyahToday = DailyAyah(
        arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
        translationText = "Allah! Tidak ada tuhan selain Dia",
        surahName = "Al-Baqara",
        surahNameArabic = "سُورَةُ البَقَرَةِ",
        surahNumber = 2,
        ayahNumberInSurah = 255,
        globalAyahNumber = 262,
        translationEdition = "id.indonesian",
        fetchedDate = "2026-10-22"
    )

    private val sampleAyahYesterday = sampleAyahToday.copy(
        arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
        translationText = "Segala puji bagi Allah, Tuhan seluruh alam",
        surahName = "Al-Fatiha",
        surahNameArabic = "سُورَةُ الفَاتِحَةِ",
        surahNumber = 1,
        ayahNumberInSurah = 2,
        globalAyahNumber = 2,
        fetchedDate = "2026-10-21"
    )

    @Test
    fun invoke_cacheHitForToday_returnsCachedAyahWithoutCallingRepository() = runTest {
        val fakeRepo = FakeDailyAyahRepository(AppResult.Success(sampleAyahToday))
        val fakeCache = FakeLocalAyahCache(initialAyah = sampleAyahToday, initialDate = "2026-10-22")
        val useCase = GetDailyAyahUseCase(
            repository = fakeRepo,
            cache = fakeCache,
            currentDateProvider = { "2026-10-22" }
        )

        val result = useCase()

        assertTrue(result is AppResult.Success)
        assertEquals(sampleAyahToday, result.data)
        assertEquals(0, fakeRepo.fetchCallsCount)
    }

    @Test
    fun invoke_cacheMiss_fetchesFromRepositoryAndUpdatesCache() = runTest {
        val fakeRepo = FakeDailyAyahRepository(AppResult.Success(sampleAyahToday))
        val fakeCache = FakeLocalAyahCache()
        val useCase = GetDailyAyahUseCase(
            repository = fakeRepo,
            cache = fakeCache,
            currentDateProvider = { "2026-10-22" }
        )

        val result = useCase()

        assertTrue(result is AppResult.Success)
        assertEquals(sampleAyahToday, result.data)
        assertEquals(1, fakeRepo.fetchCallsCount)
        assertEquals(sampleAyahToday, fakeCache.getCachedAyah())
        assertEquals("2026-10-22", fakeCache.getCacheDate())
    }

    @Test
    fun invoke_cacheExpiredFromPreviousDay_fetchesFromRepositoryAndUpdatesCache() = runTest {
        val fakeRepo = FakeDailyAyahRepository(AppResult.Success(sampleAyahToday))
        val fakeCache = FakeLocalAyahCache(initialAyah = sampleAyahYesterday, initialDate = "2026-10-21")
        val useCase = GetDailyAyahUseCase(
            repository = fakeRepo,
            cache = fakeCache,
            currentDateProvider = { "2026-10-22" }
        )

        val result = useCase()

        assertTrue(result is AppResult.Success)
        assertEquals(sampleAyahToday, result.data)
        assertEquals(1, fakeRepo.fetchCallsCount)
        assertEquals(sampleAyahToday, fakeCache.getCachedAyah())
        assertEquals("2026-10-22", fakeCache.getCacheDate())
    }

    @Test
    fun invoke_networkFailureWithExistingCache_returnsFallbackCachedAyah() = runTest {
        val networkError = AppResult.Error(Exception("Network timeout"), "No internet connection")
        val fakeRepo = FakeDailyAyahRepository(networkError)
        val fakeCache = FakeLocalAyahCache(initialAyah = sampleAyahYesterday, initialDate = "2026-10-21")
        val useCase = GetDailyAyahUseCase(
            repository = fakeRepo,
            cache = fakeCache,
            currentDateProvider = { "2026-10-22" }
        )

        val result = useCase()

        assertTrue(result is AppResult.Success)
        assertEquals(sampleAyahYesterday, result.data)
        assertEquals(1, fakeRepo.fetchCallsCount)
    }

    @Test
    fun invoke_networkFailureWithEmptyCache_propagatesError() = runTest {
        val networkError = AppResult.Error(Exception("Network timeout"), "No internet connection")
        val fakeRepo = FakeDailyAyahRepository(networkError)
        val fakeCache = FakeLocalAyahCache()
        val useCase = GetDailyAyahUseCase(
            repository = fakeRepo,
            cache = fakeCache,
            currentDateProvider = { "2026-10-22" }
        )

        val result = useCase()

        assertTrue(result is AppResult.Error)
        assertEquals("No internet connection", result.message)
        assertEquals(1, fakeRepo.fetchCallsCount)
    }

    private class FakeDailyAyahRepository(
        private val response: AppResult<DailyAyah>
    ) : DailyAyahRepository {
        var fetchCallsCount = 0
            private set

        override suspend fun getDailyAyah(): AppResult<DailyAyah> {
            fetchCallsCount++
            return response
        }
    }

    private class FakeLocalAyahCache(
        initialAyah: DailyAyah? = null,
        initialDate: String? = null
    ) : LocalAyahCache {
        private var memoryAyah: DailyAyah? = initialAyah
        private var memoryDate: String? = initialDate

        override fun getCachedAyah(): DailyAyah? = memoryAyah

        override fun cacheAyah(ayah: DailyAyah) {
            memoryAyah = ayah
            memoryDate = ayah.fetchedDate
        }

        override fun getCacheDate(): String? = memoryDate
    }
}

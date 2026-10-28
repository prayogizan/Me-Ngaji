package com.uncaan.mengaji.widget.worker

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit test verifying configuration constants and contracts of [DailyAyahWorkScheduler].
 */
class DailyAyahWorkSchedulerTest {

    @Test
    fun workName_matchesExpectedTag() {
        assertEquals("DailyAyahPeriodicRefreshWork", DailyAyahWorkScheduler.WORK_NAME)
    }

    @Test
    fun repeatIntervalHours_isConfiguredTo24Hours() {
        assertEquals(24L, DailyAyahWorkScheduler.REPEAT_INTERVAL_HOURS)
    }
}

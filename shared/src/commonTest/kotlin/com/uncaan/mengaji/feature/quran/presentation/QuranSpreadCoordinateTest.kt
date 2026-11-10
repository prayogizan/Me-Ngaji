package com.uncaan.mengaji.feature.quran.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests validating mathematical coordinate mappings between single Medina Mushaf pages
 * and dual-page book spreads (CMP-606).
 */
class QuranSpreadCoordinateTest {

    private fun pageToSpread(pageNumber: Int): Int {
        return ((pageNumber - 1) / 2).coerceIn(0, 301)
    }

    private fun spreadToPages(spreadIndex: Int): Pair<Int, Int> {
        val boundedSpread = spreadIndex.coerceIn(0, 301)
        val pageOdd = boundedSpread * 2 + 1
        val pageEven = (pageOdd + 1).coerceAtMost(604)
        return Pair(pageOdd, pageEven)
    }

    @Test
    fun spreadZero_mapsToPagesOneAndTwo() {
        val (odd, even) = spreadToPages(0)
        assertEquals(1, odd)
        assertEquals(2, even)

        assertEquals(0, pageToSpread(1))
        assertEquals(0, pageToSpread(2))
    }

    @Test
    fun spreadOne_mapsToPagesThreeAndFour() {
        val (odd, even) = spreadToPages(1)
        assertEquals(3, odd)
        assertEquals(4, even)

        assertEquals(1, pageToSpread(3))
        assertEquals(1, pageToSpread(4))
    }

    @Test
    fun intermediateSpreadTwentyFour_mapsToPagesFortyNineAndFifty() {
        val (odd, even) = spreadToPages(24)
        assertEquals(49, odd)
        assertEquals(50, even)

        assertEquals(24, pageToSpread(49))
        assertEquals(24, pageToSpread(50))
    }

    @Test
    fun finalSpreadThreeHundredOne_mapsToPagesSixHundredThreeAndSixHundredFour() {
        val (odd, even) = spreadToPages(301)
        assertEquals(603, odd)
        assertEquals(604, even)

        assertEquals(301, pageToSpread(603))
        assertEquals(301, pageToSpread(604))
    }

    @Test
    fun outOfBoundsInputs_clampSafelyToMedinaMushafLimits() {
        assertEquals(0, pageToSpread(0))
        assertEquals(0, pageToSpread(-5))
        assertEquals(301, pageToSpread(605))
        assertEquals(301, pageToSpread(1000))

        val (firstOdd, firstEven) = spreadToPages(-1)
        assertEquals(1, firstOdd)
        assertEquals(2, firstEven)

        val (lastOdd, lastEven) = spreadToPages(999)
        assertEquals(603, lastOdd)
        assertEquals(604, lastEven)
    }
}


package com.uncaan.mengaji.navigation

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests verifying serialization contracts, default states, and bottom bar tab mapping
 * for [AlQuranRoute], [AyahDeepLink], and [DeepLinkConstants].
 */
class AlQuranRouteTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun alQuranRoute_defaultValues_areNull() {
        val route = AlQuranRoute()

        assertNull(route.surahNumber)
        assertNull(route.ayahNumber)
    }

    @Test
    fun alQuranRoute_customParameters_arePreserved() {
        val route = AlQuranRoute(surahNumber = 18, ayahNumber = 10)

        assertEquals(18, route.surahNumber)
        assertEquals(10, route.ayahNumber)
    }

    @Test
    fun alQuranRoute_serializationRoundTrip_defaultValues() {
        val route = AlQuranRoute()
        val serialized = json.encodeToString(route)
        val deserialized = json.decodeFromString<AlQuranRoute>(serialized)

        assertEquals(route, deserialized)
        assertNull(deserialized.surahNumber)
        assertNull(deserialized.ayahNumber)
    }

    @Test
    fun alQuranRoute_serializationRoundTrip_withParameters() {
        val route = AlQuranRoute(surahNumber = 2, ayahNumber = 255)
        val serialized = json.encodeToString(route)
        val deserialized = json.decodeFromString<AlQuranRoute>(serialized)

        assertEquals(route, deserialized)
        assertEquals(2, deserialized.surahNumber)
        assertEquals(255, deserialized.ayahNumber)
    }

    @Test
    fun navigationTab_alQuran_usesAlQuranRouteInstance() {
        val route = NavigationTab.AL_QURAN.route

        assertTrue(route is AlQuranRoute)
        assertNull(route.surahNumber)
        assertNull(route.ayahNumber)
    }

    @Test
    fun ayahDeepLink_holdsValidData() {
        val deepLink = AyahDeepLink(surahNumber = 36, ayahNumber = 1)

        assertEquals(36, deepLink.surahNumber)
        assertEquals(1, deepLink.ayahNumber)
    }

    @Test
    fun deepLinkConstants_haveExpectedKeys() {
        assertEquals("surah_number", DeepLinkConstants.EXTRA_SURAH_NUMBER)
        assertEquals("ayah_number", DeepLinkConstants.EXTRA_AYAH_NUMBER)
    }
}

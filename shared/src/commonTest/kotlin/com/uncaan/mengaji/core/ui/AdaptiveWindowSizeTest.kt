package com.uncaan.mengaji.core.ui

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests validating [calculateWindowWidthClass] breakpoint thresholds (CMP-606).
 */
class AdaptiveWindowSizeTest {

    @Test
    fun calculateWindowWidthClass_compactBelow600dp() {
        assertEquals(MeNgajiWindowWidthClass.COMPACT, calculateWindowWidthClass(0.dp))
        assertEquals(MeNgajiWindowWidthClass.COMPACT, calculateWindowWidthClass(320.dp))
        assertEquals(MeNgajiWindowWidthClass.COMPACT, calculateWindowWidthClass(390.dp))
        assertEquals(MeNgajiWindowWidthClass.COMPACT, calculateWindowWidthClass(599.dp))
        assertEquals(MeNgajiWindowWidthClass.COMPACT, calculateWindowWidthClass(599.9.dp))
    }

    @Test
    fun calculateWindowWidthClass_mediumBetween600dpAnd839dp() {
        assertEquals(MeNgajiWindowWidthClass.MEDIUM, calculateWindowWidthClass(600.dp))
        assertEquals(MeNgajiWindowWidthClass.MEDIUM, calculateWindowWidthClass(673.dp))
        assertEquals(MeNgajiWindowWidthClass.MEDIUM, calculateWindowWidthClass(768.dp))
        assertEquals(MeNgajiWindowWidthClass.MEDIUM, calculateWindowWidthClass(839.dp))
        assertEquals(MeNgajiWindowWidthClass.MEDIUM, calculateWindowWidthClass(839.9.dp))
    }

    @Test
    fun calculateWindowWidthClass_expandedAtOrAbove840dp() {
        assertEquals(MeNgajiWindowWidthClass.EXPANDED, calculateWindowWidthClass(840.dp))
        assertEquals(MeNgajiWindowWidthClass.EXPANDED, calculateWindowWidthClass(1024.dp))
        assertEquals(MeNgajiWindowWidthClass.EXPANDED, calculateWindowWidthClass(1280.dp))
        assertEquals(MeNgajiWindowWidthClass.EXPANDED, calculateWindowWidthClass(1920.dp))
    }

    @Test
    fun gutterAndPaneWidthConstants_satisfyDesignSpecifications() {
        assertEquals(600.dp, COMPACT_MAX_WIDTH)
        assertEquals(840.dp, MEDIUM_MAX_WIDTH)
        assertEquals(16.dp, FOLDABLE_HINGE_GUTTER)
        assertEquals(20.dp, EXPANDED_SPINE_GUTTER)
        assertEquals(280.dp, MASTER_PANE_MIN_WIDTH)
        assertEquals(320.dp, MASTER_PANE_EXPANDED_WIDTH)
        assertEquals(720.dp, VERSE_LIST_MAX_CONTENT_WIDTH)
    }
}


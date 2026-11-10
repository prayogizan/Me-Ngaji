package com.uncaan.mengaji.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Breakpoint threshold separating [MeNgajiWindowWidthClass.COMPACT] and [MeNgajiWindowWidthClass.MEDIUM].
 * Viewports narrower than 600.dp represent compact portrait phones.
 */
val COMPACT_MAX_WIDTH: Dp = 600.dp

/**
 * Breakpoint threshold separating [MeNgajiWindowWidthClass.MEDIUM] and [MeNgajiWindowWidthClass.EXPANDED].
 * Viewports narrower than 840.dp but >= 600.dp represent unfolded foldables and portrait tablets.
 */
val MEDIUM_MAX_WIDTH: Dp = 840.dp

/**
 * Standard gutter width reserved for physical or virtual foldable device center hinges.
 * Prevents text or interactive elements from being bisected by the central screen crease.
 */
val FOLDABLE_HINGE_GUTTER: Dp = 16.dp

/**
 * Depth-shadow gutter width separating facing pages in the authentic Medina Mushaf dual-page book spread.
 * Accommodates central book spine perspective and foldable device hinges.
 */
val EXPANDED_SPINE_GUTTER: Dp = 20.dp

/**
 * Minimum allowable width for the master catalog pane in split-pane and side-drawer configurations.
 */
val MASTER_PANE_MIN_WIDTH: Dp = 280.dp

/**
 * Default fixed width for the master catalog sidebar in expanded tablet and desktop configurations.
 */
val MASTER_PANE_EXPANDED_WIDTH: Dp = 320.dp

/**
 * Maximum content width applied to verse-by-verse analytical list study on wide screens.
 * Retains optimal typographic line length and eye-tracking ergonomics for Arabic and translation text.
 */
val VERSE_LIST_MAX_CONTENT_WIDTH: Dp = 720.dp

/**
 * Enumeration classifying viewport horizontal dimensions into standard responsive window width classes.
 *
 * Implements Google Material 3 window size class conventions tailored for Compose Multiplatform:
 * - [COMPACT]: Handheld smartphones in portrait orientation (`< 600.dp`).
 * - [MEDIUM]: Unfolded foldable devices, small tablets, and landscape smartphones (`600.dp..839.dp`).
 * - [EXPANDED]: Landscape tablets, desktop-class windows, and large foldable displays (`>= 840.dp`).
 */
enum class MeNgajiWindowWidthClass {
    /**
     * Compact viewports (`< 600.dp`).
     * Single-pane sequential navigation with bottom navigation bar and floating thumb-zone controls.
     */
    COMPACT,

    /**
     * Medium viewports (`600.dp..839.dp`).
     * Two-pane master-detail split layout with left-docked navigation rail and hinge-safe gutter.
     */
    MEDIUM,

    /**
     * Expanded viewports (`>= 840.dp`).
     * Collapsible master catalog drawer and dual-page RTL Medina Mushaf book spread.
     */
    EXPANDED
}

/**
 * Pure evaluation function that maps a horizontal dimension [width] to a [MeNgajiWindowWidthClass].
 *
 * @param width The current viewport width in [Dp].
 * @return The corresponding [MeNgajiWindowWidthClass].
 */
fun calculateWindowWidthClass(width: Dp): MeNgajiWindowWidthClass {
    return when {
        width < COMPACT_MAX_WIDTH -> MeNgajiWindowWidthClass.COMPACT
        width < MEDIUM_MAX_WIDTH -> MeNgajiWindowWidthClass.MEDIUM
        else -> MeNgajiWindowWidthClass.EXPANDED
    }
}

/**
 * Remembers the [MeNgajiWindowWidthClass] corresponding to the given [maxWidth].
 *
 * Re-evaluates only when [maxWidth] transitions across [COMPACT_MAX_WIDTH] or [MEDIUM_MAX_WIDTH].
 *
 * @param maxWidth The maximum width constraint provided by a layout container (e.g., `BoxWithConstraints`).
 * @return The memoized [MeNgajiWindowWidthClass].
 * @see calculateWindowWidthClass
 */
@Composable
fun rememberWindowWidthClass(maxWidth: Dp): MeNgajiWindowWidthClass {
    return remember(maxWidth) {
        calculateWindowWidthClass(maxWidth)
    }
}

/**
 * CompositionLocal providing ambient access to the active [MeNgajiWindowWidthClass].
 * Defaults to [MeNgajiWindowWidthClass.COMPACT] when not explicitly provided higher in the hierarchy.
 */
val LocalWindowWidthClass = compositionLocalOf { MeNgajiWindowWidthClass.COMPACT }

/**
 * Helper composable that binds [LocalWindowWidthClass] to the evaluated [maxWidth] constraint.
 *
 * @param maxWidth The current viewport width in [Dp] obtained from a layout container.
 * @param content The composable content hierarchy consuming [LocalWindowWidthClass].
 */
@Composable
fun ProvideWindowWidthClass(
    maxWidth: Dp,
    content: @Composable () -> Unit
) {
    val windowWidthClass = rememberWindowWidthClass(maxWidth)
    CompositionLocalProvider(
        LocalWindowWidthClass provides windowWidthClass,
        content = content
    )
}


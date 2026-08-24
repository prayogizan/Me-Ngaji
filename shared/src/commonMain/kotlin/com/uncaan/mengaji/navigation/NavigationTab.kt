package com.uncaan.mengaji.navigation

import mengaji.shared.generated.resources.Res
import mengaji.shared.generated.resources.ic_quran
import mengaji.shared.generated.resources.ic_schedule
import org.jetbrains.compose.resources.DrawableResource

/**
 * Tab items configured for the bottom navigation bar.
 *
 * @property label Human-readable title displayed on the tab item and top app bar.
 * @property icon Vector drawable resource for the tab icon.
 * @property route Target type-safe navigation route object.
 */
enum class NavigationTab(
    val label: String,
    val icon: DrawableResource,
    val route: Any
) {
    /** Shalat schedule tab entry. */
    SHALAT_SCHEDULE(
        label = "Shalat Schedule",
        icon = Res.drawable.ic_schedule,
        route = ShalatScheduleRoute
    ),
    /** Al-Quran tab entry. */
    AL_QURAN(
        label = "Al-Quran",
        icon = Res.drawable.ic_quran,
        route = AlQuranRoute
    )
}

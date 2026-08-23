package com.uncaan.mengaji.navigation

import mengaji.shared.generated.resources.Res
import mengaji.shared.generated.resources.ic_quran
import mengaji.shared.generated.resources.ic_schedule
import org.jetbrains.compose.resources.DrawableResource

enum class NavigationTab(
    val label: String,
    val icon: DrawableResource,
    val route: Any
) {
    SHALAT_SCHEDULE(
        label = "Shalat Schedule",
        icon = Res.drawable.ic_schedule,
        route = ShalatScheduleRoute
    ),
    AL_QURAN(
        label = "Al-Quran",
        icon = Res.drawable.ic_quran,
        route = AlQuranRoute
    )
}

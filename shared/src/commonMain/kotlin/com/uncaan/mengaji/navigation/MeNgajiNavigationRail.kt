package com.uncaan.mengaji.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.uncaan.mengaji.theme.MeNgajiTheme
import com.uncaan.mengaji.theme.PrimaryGreen
import com.uncaan.mengaji.theme.SecondaryGold
import org.jetbrains.compose.resources.painterResource

/**
 * Adaptive vertical navigation rail docked on the start edge of medium and expanded displays (CMP-606).
 *
 * Implements Figma Screen 04 (`node-id=1-202`) with an 80.dp fixed width, app branding emblem,
 * responsive tab selection, and Material 3 emerald & gold styling. Replaces [MeNgajiBottomBar]
 * on viewports wider than 600.dp to maximize vertical reading canvas height.
 *
 * @param currentDestination The currently active [NavDestination] in the NavHost backstack.
 * @param onTabSelected Callback invoked when a navigation tab is tapped.
 * @param modifier Layout modifier applied to the rail container.
 * @see NavigationTab
 * @see MeNgajiBottomBar
 */
@Composable
fun MeNgajiNavigationRail(
    currentDestination: NavDestination?,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        header = {
            // App Emblem Header (Mosque Dome Branding)
            Box(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🕌",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        NavigationTab.entries.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(tab.route::class)
            } == true

            NavigationRailItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(tab.icon),
                        contentDescription = tab.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = if (tab == NavigationTab.AL_QURAN) "Quran" else "Shalat",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = PrimaryGreen,
                    selectedTextColor = PrimaryGreen,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview
@Composable
private fun MeNgajiNavigationRailPreview() {
    MeNgajiTheme {
        MeNgajiNavigationRail(
            currentDestination = null,
            onTabSelected = {}
        )
    }
}


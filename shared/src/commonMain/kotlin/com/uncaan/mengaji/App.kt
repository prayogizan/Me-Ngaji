package com.uncaan.mengaji

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.uncaan.mengaji.feature.quran.presentation.AlQuranScreen
import com.uncaan.mengaji.feature.shalat.ShalatScheduleScreen
import com.uncaan.mengaji.navigation.AlQuranRoute
import com.uncaan.mengaji.navigation.MeNgajiBottomBar
import com.uncaan.mengaji.navigation.NavigationTab
import com.uncaan.mengaji.navigation.ShalatScheduleRoute
import com.uncaan.mengaji.theme.MeNgajiTheme

/**
 * Root Composable entry point for the MeNgaji application.
 *
 * Configures the [MeNgajiTheme], sets up the central [rememberNavController], manages
 * the active [NavigationTab] title in the [CenterAlignedTopAppBar], and hosts the
 * application's [NavHost] container with cross-fade transitions between [ShalatScheduleRoute]
 * and [AlQuranRoute].
 *
 * @see MeNgajiTheme
 * @see MeNgajiBottomBar
 * @see NavigationTab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MeNgajiTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val currentTab = NavigationTab.entries.find { tab ->
            currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
        } ?: NavigationTab.AL_QURAN

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = currentTab.label,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                MeNgajiBottomBar(
                    currentDestination = currentDestination,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AlQuranRoute,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                composable<ShalatScheduleRoute> {
                    ShalatScheduleScreen()
                }
                composable<AlQuranRoute> {
                    AlQuranScreen()
                }
            }
        }
    }
}
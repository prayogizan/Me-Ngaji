package com.uncaan.mengaji

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.uncaan.mengaji.core.ui.MeNgajiWindowWidthClass
import com.uncaan.mengaji.core.ui.ProvideWindowWidthClass
import com.uncaan.mengaji.core.ui.rememberWindowWidthClass
import com.uncaan.mengaji.feature.quran.presentation.AlQuranScreen
import com.uncaan.mengaji.feature.shalat.presentation.ShalatScheduleScreen
import com.uncaan.mengaji.navigation.AlQuranRoute
import com.uncaan.mengaji.navigation.AyahDeepLink
import com.uncaan.mengaji.navigation.MeNgajiBottomBar
import com.uncaan.mengaji.navigation.MeNgajiNavigationRail
import com.uncaan.mengaji.navigation.NavigationTab
import com.uncaan.mengaji.navigation.ShalatScheduleRoute
import com.uncaan.mengaji.theme.MeNgajiTheme

/**
 * Root Composable entry point for the MeNgaji application.
 *
 * Implements Multi-Factor Adaptive Layouts (CMP-606):
 * - On **Compact viewports (`< 600.dp`)**, renders a top app bar and [MeNgajiBottomBar].
 * - On **Medium (`600..839.dp`)** and **Expanded (`>= 840.dp`) viewports**, docks a left-aligned
 *   vertical [MeNgajiNavigationRail] to maximize vertical reading height for foldables and tablets.
 * - Dynamically adapts across device rotation and posture changes without losing navigation state.
 *
 * @param deepLink Optional deep-link payload received from external sources (e.g., home screen Glance widget).
 * @param onDeepLinkConsumed Callback invoked when the [deepLink] has been successfully processed by navigation.
 * @see MeNgajiTheme
 * @see MeNgajiBottomBar
 * @see MeNgajiNavigationRail
 * @see MeNgajiWindowWidthClass
 * @see NavigationTab
 * @see AyahDeepLink
 * @see AlQuranRoute
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    deepLink: AyahDeepLink? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    MeNgajiTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val initialRoute = remember {
            if (deepLink != null) {
                AlQuranRoute(surahNumber = deepLink.surahNumber, ayahNumber = deepLink.ayahNumber)
            } else {
                AlQuranRoute()
            }
        }

        var isInitialLaunchConsumed by remember { mutableStateOf(false) }

        LaunchedEffect(deepLink) {
            deepLink?.let { link ->
                if (!isInitialLaunchConsumed && initialRoute.surahNumber == link.surahNumber && initialRoute.ayahNumber == link.ayahNumber) {
                    isInitialLaunchConsumed = true
                    onDeepLinkConsumed()
                    return@LaunchedEffect
                }
                navController.navigate(
                    AlQuranRoute(
                        surahNumber = link.surahNumber,
                        ayahNumber = link.ayahNumber
                    )
                ) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = (currentDestination?.hasRoute<AlQuranRoute>() == true)
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
                onDeepLinkConsumed()
            }
        }

        val currentTab = NavigationTab.entries.find { tab ->
            currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
        } ?: NavigationTab.AL_QURAN

        val onTabSelected: (NavigationTab) -> Unit = { tab ->
            navController.navigate(tab.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val windowWidthClass = rememberWindowWidthClass(maxWidth)

            ProvideWindowWidthClass(maxWidth = maxWidth) {
                if (windowWidthClass == MeNgajiWindowWidthClass.COMPACT) {
                    // Compact Phone Layout (< 600.dp): Scaffold with TopAppBar and BottomNavigationBar
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
                                onTabSelected = onTabSelected
                            )
                        }
                    ) { innerPadding ->
                        MeNgajiNavHost(
                            navController = navController,
                            initialRoute = initialRoute,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                } else {
                    // Medium & Expanded Layout (>= 600.dp): NavigationRail + Content Canvas Row
                    Row(modifier = Modifier.fillMaxSize()) {
                        MeNgajiNavigationRail(
                            currentDestination = currentDestination,
                            onTabSelected = onTabSelected
                        )

                        VerticalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            MeNgajiNavHost(
                                navController = navController,
                                initialRoute = initialRoute,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Internal host container managing declarative animated transitions between top-level feature routes.
 *
 * @param navController The central [NavHostController] driving the application backstack.
 * @param initialRoute The initial destination route when cold starting the application.
 * @param modifier Layout modifier applied to the NavHost container.
 */
@Composable
private fun MeNgajiNavHost(
    navController: NavHostController,
    initialRoute: Any,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = initialRoute,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable<ShalatScheduleRoute> {
            ShalatScheduleScreen()
        }
        composable<AlQuranRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<AlQuranRoute>()
            AlQuranScreen(
                initialSurahNumber = route.surahNumber,
                initialAyahNumber = route.ayahNumber
            )
        }
    }
}

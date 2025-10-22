package com.example.foryou.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * A composable function that creates and displays the bottom navigation bar.
 * It observes the navigation back stack to highlight the currently selected item.
 *
 * @param navController The [NavController] for handling navigation between bottom bar items.
 */
@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Report,
        BottomNavItem.Settings
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    // Navigate to the selected route if it's not the current one.
                    // This prevents creating a new entry on the back stack for the same screen.
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to avoid building up a large back stack.
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Ensure that there's only one copy of a destination on the back stack.
                            launchSingleTop = true
                            // Restore state when re-navigating to a previously selected item.
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

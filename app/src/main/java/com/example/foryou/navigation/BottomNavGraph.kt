package com.example.foryou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foryou.ui.screens.*
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.MedicineViewModel

/**
 * Defines the nested navigation graph for the screens accessible via the bottom navigation bar.
 *
 * @param navController The NavController for this nested graph. It handles navigation between the Home, Report, and Settings screens.
 * @param mainNavController The main NavController of the app. It's used for navigating to destinations outside of this graph, such as logging out.
 * @param authViewModel The ViewModel for authentication-related logic.
 * @param medicineViewModel The ViewModel for medicine-related logic.
 * @param userEmail The email of the currently logged-in user, passed to the screens that need it.
 * @param modifier A modifier to be applied to the NavHost.
 */
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    mainNavController: NavHostController,
    authViewModel: AuthViewModel,
    medicineViewModel: MedicineViewModel,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        // The main screen of the app, showing the user's daily medicine schedule.
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, medicineViewModel, userEmail)
        }

        // A screen displaying a monthly report of medicine intake.
        composable(BottomNavItem.Report.route) {
            ReportScreen(medicineViewModel, userEmail)
        }

        // A screen with app settings, including the logout functionality.
        composable(BottomNavItem.Settings.route) {
            SettingsScreen(mainNavController, authViewModel)
        }

        // A screen for adding a new medicine. It's part of this graph because it's accessed from the HomeScreen.
        composable("add_medicine/{userEmail}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("userEmail") ?: userEmail
            AddMedicineScreen(
                navController = navController,
                medicineViewModel = medicineViewModel,
                userEmail = email
            )
        }
    }
}

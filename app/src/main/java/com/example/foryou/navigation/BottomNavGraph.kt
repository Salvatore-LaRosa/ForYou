package com.example.foryou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foryou.ui.screens.*
import com.example.foryou.viewmodel.MedicineViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    medicineViewModel: MedicineViewModel,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            MainPage(navController, medicineViewModel, userEmail)
        }
        composable(BottomNavItem.Report.route) {
            ReportPage()
        }
        composable(BottomNavItem.Profile.route) {
            ProfilePage()
        }
        composable(BottomNavItem.Settings.route) {
            SettingsPage()
        }
        composable("add_medicine/{userEmail}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("userEmail") ?: userEmail
            AddMedicinePage(
                navController = navController,
                medicineViewModel = medicineViewModel,
                userEmail = email
            )
        }
    }
}

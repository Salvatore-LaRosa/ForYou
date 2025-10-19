package com.example.foryou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foryou.ui.screens.*
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.MedicineViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    medicineViewModel: MedicineViewModel,
    userEmail: String,
    modifier: Modifier = Modifier,
    mainNavController: NavHostController // 👈 nuovo parametro
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
            ReportPage(medicineViewModel, userEmail)
        }
        composable(BottomNavItem.Profile.route) {
            ProfilePage()
        }
        composable(BottomNavItem.Settings.route) {
            SettingsPage(mainNavController, authViewModel)
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


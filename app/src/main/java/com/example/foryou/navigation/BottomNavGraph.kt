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
    mainNavController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, medicineViewModel, userEmail)
        }
        composable(BottomNavItem.Report.route) {
            ReportScreen(medicineViewModel, userEmail)
        }
        composable(BottomNavItem.Settings.route) {
            SettingsScreen(mainNavController, authViewModel)
        }
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

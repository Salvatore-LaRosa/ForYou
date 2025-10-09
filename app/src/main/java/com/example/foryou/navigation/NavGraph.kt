package com.example.foryou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foryou.ui.screens.MainPage
import com.example.foryou.ui.screens.*
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.MedicineViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel,
    medicineViewModel: MedicineViewModel,
    modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("mainPage/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            MainPage(
                navController = navController,
                medicineViewModel = medicineViewModel,
                userEmail = userEmail
            )
        }
        composable("add_medicine/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            AddMedicinePage(
                navController = navController,
                medicineViewModel = medicineViewModel,
                userEmail = userEmail
            )
        }
    }
}

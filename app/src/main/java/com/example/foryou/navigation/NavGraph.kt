package com.example.foryou.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foryou.ui.screens.LoginScreen
import com.example.foryou.ui.screens.RegisterScreen
import com.example.foryou.ui.screens.AddMedicinePage
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.MedicineViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel,
    medicineViewModel: MedicineViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("mainPage/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            val bottomNavController = rememberNavController()

            Scaffold(
                bottomBar = { BottomNavBar(bottomNavController) }
            ) { innerPadding ->
                BottomNavGraph(
                    navController = bottomNavController,
                    medicineViewModel = medicineViewModel,
                    userEmail = userEmail,
                    modifier = Modifier.padding(innerPadding)
                )
            }
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

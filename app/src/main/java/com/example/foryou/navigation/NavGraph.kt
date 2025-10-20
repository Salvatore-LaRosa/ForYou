package com.example.foryou.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foryou.ui.screens.LoginScreen
import com.example.foryou.ui.screens.PinLoginScreen
import com.example.foryou.ui.screens.RegisterScreen
import com.example.foryou.ui.screens.SetupPinScreen
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.MedicineViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel,
    medicineViewModel: MedicineViewModel,
    modifier: Modifier = Modifier
) {
    val startDestination = remember { mutableStateOf("login") }

    LaunchedEffect(Unit) {
        val savedEmail = viewModel.getSavedEmail()
        if (savedEmail != null) {
            if (viewModel.isPinSet(savedEmail)) {
                startDestination.value = "pin_login"
            } else {
                startDestination.value = "setup_pin"
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination.value,
        modifier = modifier
    ) {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("setup_pin") { SetupPinScreen(navController, viewModel) }
        composable("pin_login") { PinLoginScreen(navController, viewModel) }

        composable("mainPage/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            val bottomNavController = rememberNavController()

            Scaffold(bottomBar = { BottomNavBar(bottomNavController) }) { innerPadding ->
                BottomNavGraph(
                    navController = bottomNavController,
                    mainNavController = navController,
                    authViewModel = viewModel,
                    medicineViewModel = medicineViewModel,
                    userEmail = userEmail,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

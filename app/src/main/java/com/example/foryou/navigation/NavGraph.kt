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
import com.example.foryou.util.BiometricPromptManager

/**
 * Defines the main navigation graph for the application.
 * This graph includes the initial authentication flow (login, registration, PIN setup)
 * and the main content of the app, which is a nested graph with a bottom navigation bar.
 *
 * @param navController The primary NavController for the application.
 * @param viewModel The ViewModel for authentication-related logic.
 * @param medicineViewModel The ViewModel for medicine-related logic.
 * @param promptManager The manager for handling biometric authentication prompts.
 * @param modifier A modifier to be applied to the NavHost.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel,
    medicineViewModel: MedicineViewModel,
    promptManager: BiometricPromptManager,
    modifier: Modifier = Modifier
) {
    // A mutable state to determine the starting destination of the app.
    // This allows for dynamic routing based on the user's session state.
    val startDestination = remember { mutableStateOf("login") }

    // A side effect that runs once to determine the appropriate start destination.
    LaunchedEffect(Unit) {
        val savedEmail = viewModel.getSavedEmail()
        if (savedEmail != null) {
            startDestination.value = if (viewModel.isPinSet(savedEmail)) {
                "pin_login" // If PIN is set, go to PIN login screen.
            } else {
                "setup_pin" // If no PIN is set, go to PIN setup screen.
            }
        }
        // If no email is saved, the default start destination remains "login".
    }

    NavHost(
        navController = navController,
        startDestination = startDestination.value,
        modifier = modifier
    ) {
        // --- Authentication Flow ---
        composable("login") { LoginScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("setup_pin") { SetupPinScreen(navController, viewModel) }
        composable("pin_login") { PinLoginScreen(navController, viewModel, promptManager) } // Pass the promptManager to the PIN login screen.

        // --- Main App Content ---
        // This route hosts the main part of the app, which has its own nested navigation.
        composable("mainPage/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            val bottomNavController = rememberNavController()

            // Scaffold provides the basic structure with a bottom bar.
            Scaffold(bottomBar = { BottomNavBar(bottomNavController) }) { innerPadding ->
                // The BottomNavGraph contains the screens accessible from the bottom navigation bar.
                BottomNavGraph(
                    navController = bottomNavController, // The NavController for the nested graph.
                    mainNavController = navController, // The main NavController for global navigation (e.g., logout).
                    authViewModel = viewModel,
                    medicineViewModel = medicineViewModel,
                    userEmail = userEmail,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

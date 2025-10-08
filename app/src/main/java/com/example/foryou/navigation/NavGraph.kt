package com.example.foryou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foryou.ui.screen.MainPage
import com.example.foryou.ui.screens.*
import com.example.foryou.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController, viewModel: AuthViewModel, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("mainPage") { MainPage() }
    }
}

package com.example.foryou.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foryou.R
import com.example.foryou.viewmodel.AuthViewModel

/**
 * A composable function that represents the login screen of the application.
 * It allows users to enter their credentials and log in.
 *
 * @param navController The NavController for handling navigation.
 * @param viewModel The AuthViewModel for handling authentication logic.
 */
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    // Collecting state from the ViewModel for email, password, and authentication result.
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authResult by viewModel.authResult.collectAsState()

    // Main container with a background color.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7EAF0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Top illustration image.
            Image(
                painter = painterResource(id = R.drawable.wallpaper),
                contentDescription = null, // Decorative image
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Screen title and subtitle.
            Text(
                text = "Bentornato",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05A9B2)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Inserisci tue credenziali",
                fontSize = 16.sp,
                color = Color(0xFF62C9CE)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email input field.
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "Email Icon",
                        tint = Color(0xFF05A9B2)
                    )
                },
                placeholder = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input field.
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Password Icon",
                        tint = Color(0xFF05A9B2)
                    )
                },
                placeholder = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login button.
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF05A9B2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Accedi", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button to navigate to the registration screen.
            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF62C9CE)),
                border = BorderStroke(
                    width = 1.dp, brush = SolidColor(Color(0xFF62C9CE))
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Crea un nuovo account", color = Color(0xFF62C9CE), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Displays the authentication result message and handles navigation on success.
            authResult?.let {
                AuthResultMessage(message = it) {
                    // LaunchedEffect to handle navigation after a successful login.
                    LaunchedEffect(Unit) {
                        val pinSet = viewModel.isPinSet(email)
                        // Navigate to the main page if PIN is set, otherwise to PIN setup.
                        if (pinSet) {
                            navController.navigate("mainPage/${email}") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("setup_pin") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        viewModel.clearMessage() // Clear the message after handling.
                    }
                }
            }
        }
    }
}

/**
 * A private composable to display the authentication result message.
 * It triggers a callback on login success.
 *
 * @param message The authentication message to display.
 * @param onLoginSuccess A composable lambda to be executed on successful login.
 */
@Composable
private fun AuthResultMessage(message: String, onLoginSuccess: @Composable () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Text(message, color = Color(0xFF05A9B2))
    // If the message indicates success, invoke the success callback.
    if (message == "Login success") {
        onLoginSuccess()
    }
}

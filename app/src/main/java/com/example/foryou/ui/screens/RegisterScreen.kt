package com.example.foryou.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foryou.R
import com.example.foryou.viewmodel.AuthViewModel

/**
 * A composable function that represents the registration screen of the application.
 * It allows new users to create an account by providing an email and password.
 *
 * @param navController The NavController for handling navigation.
 * @param viewModel The AuthViewModel for handling registration logic.
 */
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
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
                text = "Crea il tuo account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05A9B2)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Inserisci email e password per registrarti",
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

            // Register button.
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF05A9B2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrati", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button to navigate back to the Login screen.
            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF62C9CE)),
                border = BorderStroke(1.dp, Color(0xFF62C9CE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hai già un account? Accedi", color = Color(0xFF62C9CE), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Displays the registration result message and handles navigation on success.
            authResult?.let {
                RegistrationResultMessage(message = it) {
                    // LaunchedEffect to handle navigation after a successful registration.
                    LaunchedEffect(Unit) {
                        viewModel.saveUserSession(email)
                        // Navigate to the PIN setup screen after registration.
                        navController.navigate("setup_pin") {
                            popUpTo("register") { inclusive = true }
                        }
                        viewModel.clearMessage() // Clear the message after handling.
                    }
                }
            }
        }
    }
}

/**
 * A private composable to display the registration result message.
 * It triggers a callback on registration success.
 *
 * @param message The registration message to display.
 * @param onRegistrationSuccess A composable lambda to be executed on successful registration.
 */
@Composable
private fun RegistrationResultMessage(message: String, onRegistrationSuccess: @Composable () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Text(message, color = Color(0xFF05A9B2))
    // If the message indicates success, invoke the success callback.
    if (message == "Registration success") {
        onRegistrationSuccess()
    }
}

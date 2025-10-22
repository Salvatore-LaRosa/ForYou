package com.example.foryou.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.viewmodel.AuthViewModel

/**
 * Registration screen for a new user.
 * Allows the user to enter an email and password to create a new account.
 *
 * @param navController The NavController for navigation.
 * @param viewModel The ViewModel for authentication.
 */
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authResult by viewModel.authResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registration", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        EmailTextField(email = email, onEmailChange = { viewModel.onEmailChange(it) })

        Spacer(Modifier.height(8.dp))

        PasswordTextField(password = password, onPasswordChange = { viewModel.onPasswordChange(it) })

        Spacer(Modifier.height(16.dp))

        RegisterActionButton(onClick = { viewModel.register() })

        Spacer(Modifier.height(8.dp))

        LoginNavigationButton(onClick = { navController.navigate("login") })

        authResult?.let {
            RegistrationResultMessage(message = it) {
                LaunchedEffect(Unit) {
                    viewModel.saveUserSession(email) // Immediately save the user session
                    navController.navigate("setup_pin") {
                        popUpTo("register") { inclusive = true }
                    }
                    viewModel.clearMessage()
                }
            }
        }
    }
}

/**
 * A text field for entering an email address.
 *
 * @param email The current email value.
 * @param onEmailChange The callback to invoke when the email value changes.
 */
@Composable
private fun EmailTextField(email: String, onEmailChange: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * A text field for entering a password.
 *
 * @param password The current password value.
 * @param onPasswordChange The callback to invoke when the password value changes.
 */
@Composable
private fun PasswordTextField(password: String, onPasswordChange: (String) -> Unit) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * A button to trigger the registration action.
 *
 * @param onClick The callback to invoke when the button is clicked.
 */
@Composable
private fun RegisterActionButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Register")
    }
}

/**
 * A text button to navigate to the login screen.
 *
 * @param onClick The callback to invoke when the button is clicked.
 */
@Composable
private fun ColumnScope.LoginNavigationButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        Text("Already have an account? Login")
    }
}

/**
 * Displays the registration result message and handles navigation on success.
 *
 * @param message The message to display.
 * @param onRegistrationSuccess The composable to execute upon successful registration.
 */
@Composable
private fun RegistrationResultMessage(message: String, onRegistrationSuccess: @Composable () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Text(message)
    if (message == "Registration success") {
        onRegistrationSuccess()
    }
}

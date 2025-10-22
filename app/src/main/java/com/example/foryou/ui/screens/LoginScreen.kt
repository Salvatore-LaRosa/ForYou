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
 * Login screen for the user.
 * Allows the user to enter their email and password to log in.
 *
 * @param navController The NavController for navigation.
 * @param viewModel The ViewModel for authentication.
 */
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authResult by viewModel.authResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        EmailTextField(email = email, onEmailChange = { viewModel.onEmailChange(it) })

        Spacer(Modifier.height(8.dp))

        PasswordTextField(password = password, onPasswordChange = { viewModel.onPasswordChange(it) })

        Spacer(Modifier.height(16.dp))

        LoginButton(onClick = { viewModel.login() })

        Spacer(Modifier.height(8.dp))

        RegisterButton(onClick = { navController.navigate("register") })

        authResult?.let {
            AuthResultMessage(message = it) {
                LaunchedEffect(Unit) {
                    val pinSet = viewModel.isPinSet(email)
                    if (pinSet) {
                        navController.navigate("mainPage/${email}") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("setup_pin") {
                            popUpTo("login") { inclusive = true }
                        }
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
 * A button to trigger the login action.
 *
 * @param onClick The callback to invoke when the button is clicked.
 */
@Composable
private fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Login")
    }
}

/**
 * A button to navigate to the registration screen.
 *
 * @param onClick The callback to invoke when the button is clicked.
 */
@Composable
private fun ColumnScope.RegisterButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        Text("Don't have an account? Register")
    }
}

/**
 * Displays the authentication result message.
 *
 * @param message The message to display.
 * @param onLoginSuccess The callback to invoke when the login is successful.
 */
@Composable
private fun AuthResultMessage(message: String, onLoginSuccess: @Composable () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Text(message)
    if (message == "Login success") {
        onLoginSuccess()
    }
}

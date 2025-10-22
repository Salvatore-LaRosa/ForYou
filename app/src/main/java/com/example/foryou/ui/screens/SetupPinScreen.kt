package com.example.foryou.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.viewmodel.AuthViewModel

/**
 * A screen for setting up a new PIN.
 * This screen is shown after registration or if the user has not set a PIN yet.
 *
 * @param navController The NavController for navigation.
 * @param viewModel The ViewModel for authentication.
 */
@Composable
fun SetupPinScreen(navController: NavController, viewModel: AuthViewModel) {
    var pin by remember { mutableStateOf("") }
    val email = viewModel.getSavedEmail() ?: return

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create a PIN", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        PinTextField(pin = pin, onPinChange = { pin = it })

        Spacer(Modifier.height(16.dp))

        ConfirmButton(pin = pin, email = email, viewModel = viewModel, navController = navController)
    }
}

/**
 * A text field for entering a 4-digit PIN.
 *
 * @param pin The current PIN value.
 * @param onPinChange The callback to invoke when the PIN value changes.
 */
@Composable
private fun PinTextField(pin: String, onPinChange: (String) -> Unit) {
    OutlinedTextField(
        value = pin,
        onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onPinChange(it) },
        label = { Text("PIN (4 digits)") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true
    )
}

/**
 * A button to confirm the PIN and navigate to the main page.
 *
 * @param pin The current PIN value.
 * @param email The user's email.
 * @param viewModel The ViewModel for authentication.
 * @param navController The NavController for navigation.
 */
@Composable
private fun ConfirmButton(
    pin: String,
    email: String,
    viewModel: AuthViewModel,
    navController: NavController
) {
    Button(
        onClick = {
            if (pin.length == 4) {
                viewModel.saveUserPin(email, pin)
                navController.navigate("mainPage/$email") {
                    popUpTo("setup_pin") { inclusive = true }
                }
            }
        }
    ) {
        Text("Confirm")
    }
}

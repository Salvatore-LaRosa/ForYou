package com.example.foryou.ui.screens

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.util.BiometricPromptManager
import com.example.foryou.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * A screen for logging in with a PIN or biometric authentication.
 *
 * @param navController The NavController for navigation.
 * @param viewModel The ViewModel for authentication.
 * @param promptManager The manager for handling biometric prompts.
 */
@Composable
fun PinLoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    promptManager: BiometricPromptManager
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val emailState = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        emailState.value = viewModel.getSavedEmail()
    }

    val biometricResult by promptManager.promptResults.collectAsState(initial = null)

    HandleBiometricEnrollment(biometricResult)
    HandleBiometricSuccess(biometricResult, emailState.value, navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter your PIN", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        PinInputField(pin = pin, onPinChange = { pin = it }, isError = error)

        if (error) {
            Text("Incorrect PIN", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(16.dp))

        UnlockButton(onClick = {
            scope.launch {
                val email = emailState.value
                if (email != null && viewModel.verifyPin(email, pin)) {
                    navController.navigate("mainPage/$email") {
                        popUpTo("pin_login") { inclusive = true }
                    }
                } else {
                    error = true
                    pin = ""
                }
            }
        })

        Spacer(Modifier.height(16.dp))

        BiometricAuthButton(promptManager = promptManager)

        biometricResult?.let { BiometricResultMessage(result = it) }
    }
}

/**
 * A text field for entering a 4-digit PIN.
 *
 * @param pin The current PIN value.
 * @param onPinChange The callback to invoke when the PIN value changes.
 * @param isError Whether the PIN is incorrect.
 */
@Composable
private fun PinInputField(pin: String, onPinChange: (String) -> Unit, isError: Boolean) {
    OutlinedTextField(
        value = pin,
        onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onPinChange(it) },
        label = { Text("PIN") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        isError = isError,
        singleLine = true
    )
}

/**
 * A button to trigger the unlock action.
 *
 * @param onClick The callback to invoke when the button is clicked.
 */
@Composable
private fun UnlockButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Unlock")
    }
}

/**
 * A button to trigger biometric authentication.
 *
 * @param promptManager The manager for handling biometric prompts.
 */
@Composable
private fun BiometricAuthButton(promptManager: BiometricPromptManager) {
    Button(onClick = {
        promptManager.showBiometricPrompt(
            title = "Biometric Authentication",
            description = "Confirm your identity to log in"
        )
    }) {
        Text("Authenticate with biometrics")
    }
}

/**
 * Handles the result of a biometric authentication attempt and displays a message.
 *
 * @param result The result of the biometric authentication.
 */
@Composable
private fun BiometricResultMessage(result: BiometricPromptManager.BiometricResult) {
    Spacer(Modifier.height(12.dp))
    val message = when (result) {
        is BiometricPromptManager.BiometricResult.AuthenticationError -> "Error: ${result.error}"
        BiometricPromptManager.BiometricResult.AuthenticationFailed -> "Authentication failed"
        BiometricPromptManager.BiometricResult.AuthenticationNotSet -> "Biometrics not configured"
        BiometricPromptManager.BiometricResult.AuthenticationSuccess -> "Authentication successful!"
        BiometricPromptManager.BiometricResult.FeatureUnavailable -> "Feature unavailable"
        BiometricPromptManager.BiometricResult.HardwareUnavailable -> "Hardware unavailable"
    }
    Text(message)
}

/**
 * An effect that launches the biometric enrollment intent if biometrics are not set up.
 *
 * @param biometricResult The current biometric result.
 */
@Composable
private fun HandleBiometricEnrollment(biometricResult: BiometricPromptManager.BiometricResult?) {
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { println("Activity result: $it") }
    )

    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet && Build.VERSION.SDK_INT >= 30) {
            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
            }
            enrollLauncher.launch(enrollIntent)
        }
    }
}

/**
 * An effect that navigates to the main page on successful biometric authentication.
 *
 * @param biometricResult The current biometric result.
 * @param email The user's email.
 * @param navController The NavController for navigation.
 */
@Composable
private fun HandleBiometricSuccess(
    biometricResult: BiometricPromptManager.BiometricResult?,
    email: String?,
    navController: NavController
) {
    LaunchedEffect(biometricResult, email) {
        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess && email != null) {
            navController.navigate("mainPage/$email") {
                popUpTo("pin_login") { inclusive = true }
            }
        }
    }
}

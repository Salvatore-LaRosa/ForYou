package com.example.foryou.ui.screens

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import com.example.foryou.util.BiometricPromptManager
import com.example.foryou.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * A screen for PIN login, which also offers biometric authentication as an alternative.
 * It handles PIN input, validation, and biometric prompts.
 *
 * @param navController The navigation controller for screen transitions.
 * @param viewModel The authentication ViewModel to handle user data and verification.
 * @param promptManager The manager for handling biometric authentication prompts.
 */
@Composable
fun PinLoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    promptManager: BiometricPromptManager
) {
    // State for the entered PIN.
    var pin by remember { mutableStateOf("") }
    // State to track if an error has occurred (e.g., wrong PIN).
    var error by remember { mutableStateOf(false) }
    // Coroutine scope for launching async operations.
    val scope = rememberCoroutineScope()
    // State to hold the user's email, fetched from the session.
    val emailState = remember { mutableStateOf<String?>(null) }

    // Fetches the saved user email when the screen is first launched.
    LaunchedEffect(Unit) {
        emailState.value = viewModel.getSavedEmail()
    }

    // Collects the result from the biometric prompt.
    val biometricResult by promptManager.promptResults.collectAsState(initial = null)

    // Handles the biometric enrollment process if biometrics are not set up.
    HandleBiometricEnrollment(biometricResult)
    // Handles successful biometric authentication by navigating to the main page.
    HandleBiometricSuccess(biometricResult, emailState.value, navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inserisci il PIN",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // A continuous PIN field for entering the 4-digit PIN.
        ContinuousPinField(
            pin = pin,
            onPinChange = { newPin ->
                // Ensures the PIN is no more than 4 digits and contains only numbers.
                if (newPin.length <= 4 && newPin.all(Char::isDigit)) pin = newPin
            },
            isError = error
        )

        // Displays an error message if the PIN is incorrect.
        if (error) {
            Text(
                text = "PIN Sbagliato",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Button to submit the PIN for verification.
        Button(
            onClick = {
                scope.launch {
                    val email = emailState.value
                    // Verifies the PIN and navigates on success, otherwise shows an error.
                    if (email != null && viewModel.verifyPin(email, pin)) {
                        navController.navigate("mainPage/$email") {
                            popUpTo("pin_login") { inclusive = true }
                        }
                    } else {
                        error = true
                        pin = "" // Reset PIN on error.
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sblocca", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(24.dp))

        // Button to trigger biometric authentication.
        OutlinedButton(
            onClick = {
                promptManager.showBiometricPrompt(
                    title = "Accedi con Biometrico",
                    description = "Conferma la tua identità per eseguire il login"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Spacer(Modifier.width(8.dp))
            Text(
                "Authenticate with biometrics",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
            )
        }

        // Displays a message based on the biometric authentication result.
        biometricResult?.let { BiometricResultMessage(result = it) }
    }
}

/**
 * A composable for an OTP-style PIN field with 4 separate numeric boxes.
 *
 * @param pin The current PIN value.
 * @param onPinChange Callback invoked when the PIN changes.
 * @param isError Boolean to indicate if the field should be in an error state.
 */
@Composable
fun ContinuousPinField(
    pin: String,
    onPinChange: (String) -> Unit,
    isError: Boolean
) {
    // A list of FocusRequesters to manage focus between the PIN boxes.
    val focusRequesters = List(4) { FocusRequester() }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val char = pin.getOrNull(index)?.toString() ?: ""
            var isFocused by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .size(65.dp)
                    .border(
                        width = 2.dp,
                        color = when {
                            isError -> MaterialTheme.colorScheme.error
                            isFocused -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = TextFieldValue(
                        text = char,
                        selection = TextRange(char.length)
                    ),
                    onValueChange = { newText ->
                        // Allows only single-digit numeric input.
                        if (newText.text.length <= 1 && newText.text.isDigitsOnly()) {
                            val newPin = buildString {
                                append(pin.take(index))
                                append(newText.text)
                                append(pin.drop(index + 1))
                            }
                            onPinChange(newPin)
                            // Moves focus to the next field if a digit is entered.
                            if (newText.text.isNotEmpty() && index < 3) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged { isFocused = it.isFocused }
                        .onKeyEvent { event ->
                            // Handles backspace to move focus to the previous field.
                            if (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                                char.isEmpty() && index > 0
                            ) {
                                focusRequesters[index - 1].requestFocus()
                                onPinChange(pin.dropLast(1))
                            }
                            false
                        },
                    visualTransformation = PasswordVisualTransformation(),
                    decorationBox = { inner ->
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            // Shows a placeholder if the box is empty.
                            if (char.isEmpty()) {
                                Text(
                                    text = "-",
                                    fontSize = 28.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            inner()
                        }
                    }
                )
            }
        }
    }
}

/**
 * A composable to display messages related to the biometric authentication result.
 *
 * @param result The result of the biometric prompt.
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
    Text(
        message,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/**
 * Handles the case where biometric authentication is not set up on the device.
 * It launches an intent to prompt the user to enroll in biometrics.
 *
 * @param biometricResult The result from the biometric prompt.
 */
@Composable
private fun HandleBiometricEnrollment(biometricResult: BiometricPromptManager.BiometricResult?) {
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { println("Activity result: $it") } // Placeholder for result handling.
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
 * Handles successful biometric authentication by navigating to the main page.
 *
 * @param biometricResult The result from the biometric prompt.
 * @param email The user's email, required for navigation.
 * @param navController The navigation controller.
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

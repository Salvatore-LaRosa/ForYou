package com.example.foryou.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.viewmodel.AuthViewModel
import com.plcoding.biometricauth.BiometricPromptManager
import com.plcoding.biometricauth.BiometricPromptManager.BiometricResult
import kotlinx.coroutines.launch

@Composable
fun PinLoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    promptManager: BiometricPromptManager
) {
    var pin by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf(false) }

    val email = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        email.value = viewModel.getSavedEmail()
    }

    val biometricResult by promptManager.promptResults.collectAsState(initial = null)
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { println("Activity result: $it") }
    )

    // Se l’utente non ha configurato la biometria → apri impostazioni
    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricResult.AuthenticationNotSet && Build.VERSION.SDK_INT >= 30) {
            val enrollIntent = android.content.Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
            }
            enrollLauncher.launch(enrollIntent)
        }
    }

    // Naviga appena la biometria ha successo
    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricResult.AuthenticationSuccess) {
            val currentEmail = email.value
            if (currentEmail != null) {
                navController.navigate("mainPage/$currentEmail") {
                    popUpTo("pin_login") { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inserisci il tuo PIN", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pin = it },
            label = { Text("PIN") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        if (error) {
            Spacer(Modifier.height(8.dp))
            Text("PIN errato", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    val currentEmail = email.value
                    if (currentEmail != null) {
                        val correct = viewModel.verifyPin(currentEmail, pin)
                        if (correct) {
                            navController.navigate("mainPage/$currentEmail") {
                                popUpTo("pin_login") { inclusive = true }
                            }
                        } else {
                            error = true
                            pin = ""
                        }
                    }
                }
            }
        ) {
            Text("Sblocca")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                promptManager.showBiometricPrompt(
                    title = "Autenticazione biometrica",
                    description = "Conferma la tua identità per accedere"
                )
            }
        ) {
            Text("Autenticati con biometria")
        }

        biometricResult?.let { result ->
            Spacer(Modifier.height(12.dp))
            Text(
                text = when (result) {
                    is BiometricResult.AuthenticationError -> "Errore: ${result.error}"
                    BiometricResult.AuthenticationFailed -> "Autenticazione fallita"
                    BiometricResult.AuthenticationNotSet -> "Biometria non configurata"
                    BiometricResult.AuthenticationSuccess -> "Autenticazione riuscita!"
                    BiometricResult.FeatureUnavailable -> "Funzionalità non disponibile"
                    BiometricResult.HardwareUnavailable -> "Hardware non disponibile"
                }
            )
        }
    }
}
package com.example.foryou.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun PinLoginScreen(navController: NavController, viewModel: AuthViewModel) {
    var pin by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf(false) }

    // Recupera l'email salvata nella sessione
    val email = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        email.value = viewModel.getSavedEmail()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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
    }
}
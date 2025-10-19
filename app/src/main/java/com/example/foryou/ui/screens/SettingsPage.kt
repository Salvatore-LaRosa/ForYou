package com.example.foryou.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.viewmodel.AuthViewModel

@Composable
fun SettingsPage(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Impostazioni",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    // Logout dell'utente
                    authViewModel.logout()
                    // Torna alla schermata di login
                    navController.navigate("login") {
                        popUpTo(0) // Rimuove tutto lo stack di navigazione
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

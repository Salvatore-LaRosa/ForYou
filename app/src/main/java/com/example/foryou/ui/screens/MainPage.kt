package com.example.foryou.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.LocalDate

@Composable
fun MainPage(
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    val today = LocalDate.now().toString()
    val medicines by medicineViewModel.getMedicinesForUserAndDate(userEmail, today)
        .collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_medicine/$userEmail") }
            ) {
                "Aggiungi Farmaco"
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Farmaci da assumere oggi",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (medicines.isEmpty()) {
                Text("Nessun farmaco previsto per oggi.")
            } else {
                LazyColumn {
                    items(medicines) { med ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(med.name, style = MaterialTheme.typography.titleMedium)
                                Text("Quantità: ${med.quantity}")
                                Text("Note: ${med.notes ?: "—"}")
                            }
                        }
                    }
                }
            }
        }
    }
}

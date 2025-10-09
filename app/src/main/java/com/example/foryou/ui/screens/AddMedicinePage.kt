package com.example.foryou.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.data.Medicine
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicinePage(
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    var name by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Ogni giorno") }
    var quantity by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val frequencyOptions = listOf("Ogni giorno", "N volte al giorno", "Ogni settimana", "Ogni mese")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Aggiungi un nuovo farmaco", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantità (mg/ml o compresse)") })
        OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Data fine assunzione (YYYY-MM-DD)") })

        // Dropdown per frequenza
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = frequency,
                onValueChange = {},
                label = { Text("Frequenza") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                frequencyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            frequency = option
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Note (opzionali)") })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Annulla")
            }

            Button(onClick = {
                if (name.isNotBlank() && quantity.isNotBlank() && endDate.isNotBlank()) {
                    val medicine = Medicine(
                        name = name,
                        frequency = frequency,
                        quantity = quantity,
                        endDate = endDate,
                        notes = notes.ifBlank { null },
                        userEmail = userEmail,
                        creationDate = LocalDate.now().toString()
                    )
                    medicineViewModel.insertMedicine(medicine)
                    navController.popBackStack()
                }
            }) {
                Text("Conferma")
            }
        }
    }
}

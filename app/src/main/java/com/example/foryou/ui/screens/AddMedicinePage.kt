package com.example.foryou.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.data.Medicine
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState

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
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf<LocalTime?>(null) }
    var notes by remember { mutableStateOf("") }

    val frequencyOptions = listOf("Ogni giorno", "Ogni settimana", "Ogni mese")

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Aggiungi un nuovo farmaco", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { if (it.all { c -> c.isDigit() }) quantity = it },
            label = { Text("Quantità (mg/ml o compresse)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = endDate?.format(dateFormatter) ?: "",
            onValueChange = {},
            label = { Text("Data fine assunzione") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        OutlinedTextField(
            value = time?.format(timeFormatter) ?: "",
            onValueChange = {},
            label = { Text("Orario assunzione") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true }
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = frequency,
                onValueChange = {},
                readOnly = true,
                label = { Text("Frequenza") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
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

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Note (opzionali)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Annulla")
            }

            Button(onClick = {
                if (name.isNotBlank() && quantity.isNotBlank() && endDate != null && time != null) {
                    val medicine = Medicine(
                        name = name,
                        frequency = frequency,
                        quantity = quantity,
                        endDate = endDate.toString(),
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

    // --- Dialog DatePicker ---
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        endDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annulla")
                }
            },
            text = {
                DatePicker(state = datePickerState)
            }
        )
    }

    // --- Dialog TimePicker ---
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Annulla")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

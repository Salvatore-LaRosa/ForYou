package com.example.foryou.ui.screens

import androidx.compose.foundation.clickable
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
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneId
import com.example.foryou.data.Medicine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val medicines by medicineViewModel.getAllMedicinesForUser(userEmail)
        .collectAsState(initial = emptyList())

    fun parseDate(dateStr: String): LocalDate? {
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
        )

        for (format in formats) {
            try {
                return LocalDate.parse(dateStr, format)
            } catch (_: DateTimeParseException) {
            }
        }
        return null
    }

    val filteredMedicines = medicines.filter { med ->
        val start = parseDate(med.creationDate)
        val end = parseDate(med.endDate)
        if (start != null && end != null) {
            selectedDate in start..end
        } else {
            false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmaci") },
                actions = {
                    OutlinedTextField(
                        value = selectedDate.format(dateFormatter),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleziona data") },
                        modifier = Modifier
                            .width(150.dp)
                            .clickable { showDatePicker = true }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_medicine/$userEmail") }
            ) {
                Text("+")
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
                text = "Farmaci per il ${selectedDate.format(dateFormatter)}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (filteredMedicines.isEmpty()) {
                Text("Nessun farmaco previsto per questa data.")
            } else {
                LazyColumn {
                    items(filteredMedicines) { med ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedMedicine = med //apre il dettaglio
                                }
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
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

    // --- Dialog Dettaglio Farmaco ---
    if (selectedMedicine != null) {
        AlertDialog(
            onDismissRequest = { selectedMedicine = null },
            confirmButton = {
                TextButton(onClick = { selectedMedicine = null }) {
                    Text("Chiudi")
                }
            },
            title = { Text(selectedMedicine!!.name) },
            text = {
                Column {
                    Text("Quantità: ${selectedMedicine!!.quantity}")
                    Text("Frequenza: ${selectedMedicine!!.frequency}")
                    Text("Data fine: ${selectedMedicine!!.endDate}")
                    Text("Note: ${selectedMedicine!!.notes ?: "Nessuna"}")
                }
            }
        )
    }
}

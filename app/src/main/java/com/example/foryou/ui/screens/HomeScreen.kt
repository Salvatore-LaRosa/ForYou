package com.example.foryou.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.data.local.entity.Medicine
import com.example.foryou.ui.theme.*
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private fun parseDate(dateStr: String): LocalDate? {
    val formats = listOf(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    )
    for (format in formats) {
        try {
            return LocalDate.parse(dateStr, format)
        } catch (_: DateTimeParseException) { }
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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

    val filteredMedicines = medicines.filter { med ->
        val start = parseDate(med.creationDate)
        val end = parseDate(med.endDate)
        if (start != null && end != null) selectedDate in start..end else false
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_medicine/$userEmail") },
                containerColor = MainColor,
                shape = CircleShape
            ) {
                Text("+", color = White, style = MaterialTheme.typography.titleLarge)
            }
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineLarge,
                color = MainColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = selectedDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleziona data") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainColor,
                    unfocusedBorderColor = SecondaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            val dateText = if (selectedDate == LocalDate.now()) "Oggi" else selectedDate.format(dateFormatter)
            Text(
                text = "Medicinali per $dateText",
                style = MaterialTheme.typography.titleLarge,
                color = MainColor
            )
            Spacer(modifier = Modifier.height(12.dp))

            MedicineList(
                medicines = filteredMedicines,
                medicineViewModel = medicineViewModel,
                userEmail = userEmail,
                selectedDate = selectedDate,
                onMedicineClick = { selectedMedicine = it }
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                selectedDate = it
                showDatePicker = false
            }
        )
    }

    if (selectedMedicine != null) {
        MedicineDetailDialog(
            medicine = selectedMedicine!!,
            onDismissRequest = { selectedMedicine = null },
            onMarkAsTaken = {
                val today = selectedDate.toString()
                medicineViewModel.markAsTaken(selectedMedicine!!.name, userEmail, today)
                selectedMedicine = null
            },
            onDeleteFromDate = {
                val today = selectedDate.toString()
                medicineViewModel.deleteFromDate(selectedMedicine!!.name, userEmail, today)
                selectedMedicine = null
            }
        )
    }
}

@Composable
private fun MedicineList(
    medicines: List<Medicine>,
    medicineViewModel: MedicineViewModel,
    userEmail: String,
    selectedDate: LocalDate,
    onMedicineClick: (Medicine) -> Unit
) {
    if (medicines.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nessun medicinale per questa data", color = TextGray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(medicines) { med ->
                val intakes by medicineViewModel
                    .getIntakesForMedicine(userEmail, med.name)
                    .collectAsState(initial = emptyList())

                val today = selectedDate.toString()
                val isTakenToday = intakes.any { it.date == today && it.taken }

                MedicineCard(
                    medicine = med,
                    isTaken = isTakenToday,
                    onClick = { onMedicineClick(med) }
                )
            }
        }
    }
}

@Composable
private fun MedicineCard(
    medicine: Medicine,
    isTaken: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (isTaken) Confirmed else White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleMedium.copy(color = MainColor)
            )
            Text("Quantità: ${medicine.quantity}", color = SecondaryColor)
            Text("Note: ${medicine.notes ?: "—"}", color = TextGray)
            if (isTaken) {
                Text(
                    "Assunto oggi",
                    color = MainColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val selectedDate = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onDateSelected(selectedDate)
                }
            }) {
                Text("Conferma", color = MainColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Annulla", color = SecondaryColor)
            }
        },
        text = { DatePicker(state = datePickerState) }
    )
}

@Composable
private fun MedicineDetailDialog(
    medicine: Medicine,
    onDismissRequest: () -> Unit,
    onMarkAsTaken: () -> Unit,
    onDeleteFromDate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                medicine.name,
                color = MainColor,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text("Quantità: ${medicine.quantity}")
                Text("Frequenza: ${medicine.frequency}")
                Text("Fine: ${medicine.endDate}")
                Text("Note: ${medicine.notes ?: "—"}")
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkAsTaken,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    Text("Assunto / Completato", color = White)
                }

                Button(
                    onClick = onDeleteFromDate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
                ) {
                    Text("Elimina da oggi", color = White)
                }

                TextButton(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
                    Text("Chiudi", color = TextGray)
                }
            }
        }
    )
}

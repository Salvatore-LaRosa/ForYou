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

/**
 * Utility function to parse a string into a LocalDate object.
 * Tries to parse the string with different date formats.
 *
 * @param dateStr The date string to parse.
 * @return A LocalDate object if parsing is successful, otherwise null.
 */
private fun parseDate(dateStr: String): LocalDate? {
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

/**
 * Main screen of the application.
 * Displays the list of medicines for the selected date and allows the user to add new ones.
 *
 * @param navController The NavController for navigation.
 * @param medicineViewModel The ViewModel for accessing medicine data.
 * @param userEmail The current user's email.
 */
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
        if (start != null && end != null) {
            selectedDate in start..end
        } else false
    }

    Scaffold(
        topBar = {
            HomeScreenTopAppBar(
                selectedDate = selectedDate,
                dateFormatter = dateFormatter,
                onDateClick = { showDatePicker = true }
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
            val dateText = if (selectedDate == LocalDate.now()) "Today" else selectedDate.format(dateFormatter)
            Text(
                text = "Medicines for $dateText",
                style = MaterialTheme.typography.headlineSmall
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
                medicineViewModel.markAsTaken(
                    selectedMedicine!!.name,
                    userEmail,
                    today
                )
                selectedMedicine = null
            },
            onDeleteFromDate = {
                val today = selectedDate.toString()
                medicineViewModel.deleteFromDate(
                    selectedMedicine!!.name,
                    userEmail,
                    today
                )
                selectedMedicine = null
            }
        )
    }
}

/**
 * The top app bar for the main screen.
 * Displays the title and a text field for date selection.
 *
 * @param selectedDate The currently selected date.
 * @param dateFormatter The formatter for displaying the date.
 * @param onDateClick The callback to invoke when the date field is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenTopAppBar(
    selectedDate: LocalDate,
    dateFormatter: DateTimeFormatter,
    onDateClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Medicines") },
        actions = {
            OutlinedTextField(
                value = selectedDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Select date") },
                modifier = Modifier
                    .width(150.dp)
                    .clickable(onClick = onDateClick)
            )
        }
    )
}

/**
 * Displays a list of medicines.
 * If the list is empty, a message is displayed.
 *
 * @param medicines The list of medicines to display.
 * @param medicineViewModel The ViewModel for accessing medicine data.
 * @param userEmail The current user's email.
 * @param selectedDate The currently selected date.
 * @param onMedicineClick The callback to invoke when a medicine is clicked.
 */
@Composable
private fun MedicineList(
    medicines: List<Medicine>,
    medicineViewModel: MedicineViewModel,
    userEmail: String,
    selectedDate: LocalDate,
    onMedicineClick: (Medicine) -> Unit
) {
    if (medicines.isEmpty()) {
        Text("No medicines scheduled for this date.")
    } else {
        LazyColumn {
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

/**
 * Displays a single medicine card.
 *
 * @param medicine The medicine to display.
 * @param isTaken Indicates whether the medicine has been taken today.
 * @param onClick The callback to invoke when the card is clicked.
 */
@Composable
private fun MedicineCard(
    medicine: Medicine,
    isTaken: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isTaken)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                medicine.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isTaken)
                    MaterialTheme.colorScheme.onTertiaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text("Quantity: ${medicine.quantity}")
            Text("Notes: ${medicine.notes ?: "—"}")
            if (isTaken) {
                Text(
                    "Taken today",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Dialog for selecting a date.
 *
 * @param onDismissRequest The callback to invoke when the dialog is dismissed.
 * @param onDateSelected The callback to invoke when a date is selected.
 */
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
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = { DatePicker(state = datePickerState) }
    )
}

/**
 * Dialog for displaying medicine details and performing actions.
 *
 * @param medicine The medicine to display.
 * @param onDismissRequest The callback to invoke when the dialog is dismissed.
 * @param onMarkAsTaken The callback to invoke when the medicine is marked as taken.
 * @param onDeleteFromDate The callback to invoke when the medicine is deleted from today onwards.
 */
@Composable
private fun MedicineDetailDialog(
    medicine: Medicine,
    onDismissRequest: () -> Unit,
    onMarkAsTaken: () -> Unit,
    onDeleteFromDate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(medicine.name) },
        text = {
            Column {
                Text("Quantity: ${medicine.quantity}")
                Text("Frequency: ${medicine.frequency}")
                Text("End date: ${medicine.endDate}")
                Text("Notes: ${medicine.notes ?: "None"}")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Select an action:",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkAsTaken,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Taken / Completed")
                }

                Button(
                    onClick = onDeleteFromDate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("Delete from today onwards")
                }

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        },
        dismissButton = {}
    )
}

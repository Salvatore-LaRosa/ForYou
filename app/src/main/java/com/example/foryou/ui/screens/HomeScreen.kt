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
import androidx.compose.ui.text.font.FontWeight
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

/**
 * Utility function to parse a date string into a [LocalDate] object.
 * It tries different common formats to ensure flexibility.
 *
 * @param dateStr The date string to parse.
 * @return A [LocalDate] object if parsing is successful, otherwise null.
 */
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

/**
 * The main screen of the application that displays the list of medicines for the selected date.
 * The user can select a different date, add a new medicine, and view the details of a medicine.
 *
 * @param navController The navigation controller to move between screens.
 * @param medicineViewModel The ViewModel for managing medicine data.
 * @param userEmail The email of the logged-in user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    // State for the currently selected date, initialized with today's date.
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    // State to control the visibility of the date picker dialog.
    var showDatePicker by remember { mutableStateOf(false) }
    // State for the currently selected medicine, used to show the detail dialog.
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }

    // Formatter to display the date in the desired format.
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    // Collects the list of all user's medicines from the ViewModel.
    val medicines by medicineViewModel.getAllMedicinesForUser(userEmail)
        .collectAsState(initial = emptyList())

    // Filters the medicines to display only those relevant to the selected date.
    val filteredMedicines = medicines.filter { med ->
        val start = parseDate(med.creationDate)
        val end = parseDate(med.endDate)
        // Checks if the selected date is within the medicine's start and end date.
        if (start != null && end != null) selectedDate in start..end else false
    }

    // Scaffold provides the basic layout structure with a floating action button.
    Scaffold(
        floatingActionButton = {
            // Floating action button to navigate to the "add medicine" screen.
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
        // Main layout column for the screen content.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            // Screen title.
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineLarge,
                color = MainColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Read-only text field to display the selected date and trigger the date picker.
            OutlinedTextField(
                value = selectedDate.format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleziona data") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }, // Shows the date picker on click.
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainColor,
                    unfocusedBorderColor = SecondaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Header for the medicine list, indicating the selected date.
            val dateText = if (selectedDate == LocalDate.now()) "Oggi" else selectedDate.format(dateFormatter)
            Text(
                text = "Medicinali per $dateText",
                style = MaterialTheme.typography.titleLarge,
                color = MainColor
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Displays the list of filtered medicines.
            MedicineList(
                medicines = filteredMedicines,
                medicineViewModel = medicineViewModel,
                userEmail = userEmail,
                selectedDate = selectedDate,
                onMedicineClick = { selectedMedicine = it } // Sets the selected medicine to show the detail dialog.
            )
        }
    }

    // Shows the date picker dialog if showDatePicker is true.
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                selectedDate = it
                showDatePicker = false
            }
        )
    }

    // Shows the medicine detail dialog if a medicine is selected.
    if (selectedMedicine != null) {
        MedicineDetailDialog(
            medicine = selectedMedicine!!,
            onDismissRequest = { selectedMedicine = null },
            onMarkAsTaken = {
                val today = selectedDate.toString()
                medicineViewModel.markAsTaken(selectedMedicine!!.name, userEmail, today)
                selectedMedicine = null // Closes the dialog after the action.
            },
            onDeleteFromDate = {
                val today = selectedDate.toString()
                medicineViewModel.deleteFromDate(selectedMedicine!!.name, userEmail, today)
                selectedMedicine = null // Closes the dialog after the action.
            }
        )
    }
}

/**
 * Composable that displays a list of medicines.
 * Shows a message if the list is empty.
 *
 * @param medicines List of medicines to display.
 * @param medicineViewModel The ViewModel for managing medicine data.
 * @param userEmail The email of the logged-in user.
 * @param selectedDate The currently selected date.
 * @param onMedicineClick Callback invoked when a medicine is clicked.
 */
@Composable
private fun MedicineList(
    medicines: List<Medicine>,
    medicineViewModel: MedicineViewModel,
    userEmail: String,
    selectedDate: LocalDate,
    onMedicineClick: (Medicine) -> Unit
) {
    // Shows a message if there are no medicines for the selected date.
    if (medicines.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nessun medicinale per questa data", color = TextGray)
        }
    } else {
        // Displays the list of medicines in a LazyColumn for performance.
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(medicines) { med ->
                // Collects the intake status for the specific medicine.
                val intakes by medicineViewModel
                    .getIntakesForMedicine(userEmail, med.name)
                    .collectAsState(initial = emptyList())

                // Checks if the medicine has been taken on the selected date.
                val today = selectedDate.toString()
                val isTakenToday = intakes.any { it.date == today && it.taken }

                // Displays the individual medicine card.
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
 * Composable that displays a single medicine card.
 * Shows the name, time, quantity, notes, and whether it has been taken.
 *
 * @param medicine The [Medicine] object to display.
 * @param isTaken Boolean indicating if the medicine has been taken.
 * @param onClick Callback invoked when the card is clicked.
 */
@Composable
private fun MedicineCard(
    medicine: Medicine,
    isTaken: Boolean,
    onClick: () -> Unit
) {
    // Change card color if the medicine has been taken.
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

            // Medicine name.
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MainColor,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Row for intake time with an icon.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = medicine.time.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MainColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Medicine quantity.
            Text(
                text = "Quantità: ${medicine.quantity}",
                color = SecondaryColor,
                style = MaterialTheme.typography.bodyMedium
            )

            // Notes, if available.
            Text(
                text = "Note: ${medicine.notes ?: "—"}",
                color = TextGray,
                style = MaterialTheme.typography.bodySmall
            )

            // "Taken today" status message.
            if (isTaken) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Assunto oggi",
                    color = MainColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


/**
 * Custom dialog for selecting a date.
 * Uses the Material 3 [DatePicker].
 *
 * @param onDismissRequest Callback invoked when the dialog is dismissed.
 * @param onDateSelected Callback invoked with the selected date when "Confirm" is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    // State for the date picker component.
    val datePickerState = rememberDatePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                // Converts the selected milliseconds to a LocalDate object and triggers the callback.
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

/**
 * Dialog that shows the details of a medicine and provides actions.
 * Actions include "Mark as Taken" and "Delete from today".
 *
 * @param medicine The medicine to show the details of.
 * @param onDismissRequest Callback to dismiss the dialog.
 * @param onMarkAsTaken Callback to mark the medicine as taken.
 * @param onDeleteFromDate Callback to delete the medicine from the selected date onwards.
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
        title = {
            // Dialog title with the medicine's name.
            Text(
                medicine.name,
                color = MainColor,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            // Displays the details of the medicine.
            Column {
                Text("Quantità: ${medicine.quantity}")
                Text("Frequenza: ${medicine.frequency}")
                Text("Fine: ${medicine.endDate}")
                Text("Note: ${medicine.notes ?: "—"}")
            }
        },
        confirmButton = {
            // Container for action buttons.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button to mark the medicine as taken.
                Button(
                    onClick = onMarkAsTaken,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    Text("Assunto / Completato", color = White)
                }

                // Button to delete the medicine from the selected date onwards.
                Button(
                    onClick = onDeleteFromDate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
                ) {
                    Text("Elimina da oggi", color = White)
                }

                // Button to close the dialog.
                TextButton(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
                    Text("Chiudi", color = TextGray)
                }
            }
        }
    )
}

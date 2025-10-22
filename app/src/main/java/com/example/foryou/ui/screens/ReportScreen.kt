package com.example.foryou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * A screen that displays a monthly report of medicine intake.
 * The user can navigate between months to see the intake history.
 *
 * @param medicineViewModel The ViewModel to access medicine and intake data.
 * @param userEmail The current user's email to filter the data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    // State to keep track of the currently selected month by the user. Initialized to the current month.
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    // Collects the list of all medicines for the user from the ViewModel as a Composable state.
    val medicines by medicineViewModel.getAllMedicinesForUser(userEmail)
        .collectAsState(initial = emptyList())

    // Formatter to parse date strings into LocalDate objects.
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Generates a list of all days for the selected month.
    val monthDays = (1..selectedMonth.lengthOfMonth()).map { day ->
        selectedMonth.atDay(day)
    }

    // An object representing today's date.
    val today = LocalDate.now()

    Scaffold(
        topBar = {
            // Top app bar displaying the title with the current month and year.
            TopAppBar(
                title = { Text("Resoconto ${selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${selectedMonth.year}") },
                actions = {
                    // Actions in the TopAppBar to navigate to the previous or next month.
                    Row {
                        IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                            Text("<")
                        }
                        IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
                            Text(">")
                        }
                    }
                }
            )
        }
    ) { padding ->
        // Main content of the screen, scrollable vertically.
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            // If there are no medicines, display a message to the user.
            if (medicines.isEmpty()) {
                Text("Nessun farmaco registrato.")
                return@Column
            }

            // Iterate over each medicine to create an individual report Card.
            medicines.forEach { med ->
                // Collect the intake history for the specific medicine.
                val intakes by medicineViewModel
                    .getIntakesForMedicine(userEmail, med.name)
                    .collectAsState(initial = emptyList())

                // Parse the creation and end dates of the medicine.
                val creationDate = LocalDate.parse(med.creationDate, formatter)
                val endDate = LocalDate.parse(med.endDate, formatter)

                // Check if the medicine is active (i.e., its intake period overlaps) in the selected month.
                val isInMonth = !(endDate.isBefore(selectedMonth.atDay(1)) ||
                        creationDate.isAfter(selectedMonth.atEndOfMonth()))

                // Display the medicine card only if it is relevant for the displayed month.
                if (isInMonth) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            // Display the medicine name.
                            Text(
                                med.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // A horizontally scrollable row representing the month's calendar.
                            Row(
                                Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(bottom = 4.dp)
                            ) {
                                // Iterate over each day of the month to display the intake status.
                                monthDays.forEach { day ->
                                    // Check if the current day is within the medicine's intake period.
                                    val isBeforeEnd = !day.isAfter(endDate)
                                    val isAfterStart = !day.isBefore(creationDate)

                                    // Find the recorded intake for this specific day.
                                    val intakeForDay = intakes.find { it.date == day.toString() }

                                    // Determine the background color for the day's box based on intake status.
                                    val color = when {
                                        // If the day is outside the intake range, it's transparent.
                                        !isBeforeEnd || !isAfterStart -> Color.Transparent
                                        // If the day is in the future, it's light gray.
                                        day.isAfter(today) -> Color.LightGray
                                        // If the intake was marked as "taken", it's green.
                                        intakeForDay?.taken == true -> Color(0xFF81C784)
                                        // If the intake was missed or not yet recorded (for past days), it's red.
                                        intakeForDay?.taken == false -> Color(0xFFE57373)
                                        // Default to red for past days without intake records.
                                        else -> Color(0xFFE57373)
                                    }

                                    // Box representing a single day in the calendar.
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .padding(2.dp)
                                            .background(color, shape = MaterialTheme.shapes.small)
                                            .border(1.dp, Color.Gray)
                                            .align(Alignment.CenterVertically),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Display the day number.
                                        Text(day.dayOfMonth.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

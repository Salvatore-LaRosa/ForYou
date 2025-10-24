package com.example.foryou.ui.screens

import androidx.compose.foundation.background
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
import com.example.foryou.ui.theme.*
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.foryou.data.local.entity.MedicineIntake

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
        containerColor = BackgroundColor,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(BackgroundColor)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Resoconto",
                    color = MainColor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = selectedMonth.month.name.lowercase()
                        .replaceFirstChar { it.uppercase() } + " ${selectedMonth.year}",
                    color = MainColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        // Main content of the screen, scrollable vertically.
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
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
                    ReportCard(
                        title = med.name,
                        intakes = intakes,
                        creationDate = creationDate,
                        endDate = endDate,
                        selectedMonth = selectedMonth,
                        monthDays = monthDays,
                        today = today
                    )
                }
            }
        }
    }
}

/**
 * Composable per la card di resoconto singola.
 */
@Composable
fun ReportCard(
    title: String,
    intakes: List<MedicineIntake>,
    creationDate: LocalDate,
    endDate: LocalDate,
    selectedMonth: YearMonth,
    monthDays: List<LocalDate>,
    today: LocalDate
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = MainColor)
            Spacer(modifier = Modifier.height(8.dp))

            // Griglia calendario
            Column(
                modifier = Modifier
                    .background(Color.White, shape = MaterialTheme.shapes.large)
                    .padding(8.dp)
            ) {
                val daysOfWeek = listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    daysOfWeek.forEach {
                        Text(it, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val firstDayOfWeek = monthDays.first().dayOfWeek.value
                var currentIndex = 1
                val totalDays = monthDays.size

                while (currentIndex <= totalDays) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 1..7) {
                            if ((currentIndex == 1 && i < firstDayOfWeek) || currentIndex > totalDays) {
                                Box(modifier = Modifier.size(36.dp))
                            } else {
                                val day = monthDays[currentIndex - 1]
                                val isBeforeEnd = !day.isAfter(endDate)
                                val isAfterStart = !day.isBefore(creationDate)
                                val intakeForDay = intakes.find { it.date == day.toString() }

                                val color = when {
                                    !isBeforeEnd || !isAfterStart -> Color.Transparent
                                    day.isAfter(today) -> Color.LightGray
                                    intakeForDay?.taken == true -> Color(0xFF8AFF8A)
                                    intakeForDay?.taken == false -> Color(0xFFFF6B6B)
                                    else -> Color(0xFFFF6B6B)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(color, shape = MaterialTheme.shapes.medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(day.dayOfMonth.toString())
                                }
                                currentIndex++
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

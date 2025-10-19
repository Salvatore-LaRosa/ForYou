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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPage(
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    val medicines by medicineViewModel.getAllMedicinesForUser(userEmail)
        .collectAsState(initial = emptyList())

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val monthDays = (1..selectedMonth.lengthOfMonth()).map { day ->
        selectedMonth.atDay(day)
    }

    val today = LocalDate.now()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resoconto ${selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${selectedMonth.year}") },
                actions = {
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
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            if (medicines.isEmpty()) {
                Text("Nessun farmaco registrato.")
                return@Column
            }

            medicines.forEach { med ->
                val intakes by medicineViewModel
                    .getIntakesForMedicine(userEmail, med.name)
                    .collectAsState(initial = emptyList())

                val creationDate = LocalDate.parse(med.creationDate, formatter)
                val endDate = LocalDate.parse(med.endDate, formatter)

                // Filtra solo i farmaci attivi nel mese selezionato
                val isInMonth = !(endDate.isBefore(selectedMonth.atDay(1)) ||
                        creationDate.isAfter(selectedMonth.atEndOfMonth()))

                if (isInMonth) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text(
                                med.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(bottom = 4.dp)
                            ) {
                                monthDays.forEach { day ->
                                    val isBeforeEnd = !day.isAfter(endDate)
                                    val isAfterStart = !day.isBefore(creationDate)

                                    val intakeForDay = intakes.find { it.date == day.toString() }

                                    val color = when {
                                        !isBeforeEnd || !isAfterStart -> Color.Transparent
                                        day.isAfter(today) -> Color.LightGray
                                        intakeForDay?.taken == true -> Color(0xFF81C784) // verde
                                        intakeForDay?.taken == false -> Color(0xFFE57373) // rosso
                                        else -> Color(0xFFE57373) // non assunto di default se passato
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .padding(2.dp)
                                            .background(color, shape = MaterialTheme.shapes.small)
                                            .border(1.dp, Color.Gray)
                                            .align(Alignment.CenterVertically),
                                        contentAlignment = Alignment.Center
                                    ) {
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
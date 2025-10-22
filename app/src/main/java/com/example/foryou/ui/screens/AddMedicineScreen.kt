package com.example.foryou.ui.screens

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.foryou.data.Medicine
import com.example.foryou.notification.ReminderReceiver
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * A screen for adding a new medicine.
 *
 * @param navController The NavController for navigation.
 * @param medicineViewModel The ViewModel for accessing medicine data.
 * @param userEmail The current user's email.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Every day") }
    var quantity by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf<LocalTime?>(null) }
    var notes by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    RequestNotificationPermission(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add a New Medicine", style = MaterialTheme.typography.headlineSmall)

        MedicineForm(
            name, { name = it },
            quantity, { quantity = it },
            endDate, { showDatePicker = true },
            time, { showTimePicker = true },
            frequency, { frequency = it },
            notes, { notes = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        ActionButtons(
            onCancel = { navController.popBackStack() },
            onConfirm = {
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
                    scheduleMedicineReminder(context, name, time!!, frequency)
                    navController.popBackStack()
                }
            }
        )
    }

    if (showDatePicker) {
        EndDateDatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { endDate = it; showDatePicker = false }
        )
    }

    if (showTimePicker) {
        IntakeTimePicker(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { time = it; showTimePicker = false }
        )
    }
}

/**
 * Displays the form for entering medicine details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicineForm(
    name: String, onNameChange: (String) -> Unit,
    quantity: String, onQuantityChange: (String) -> Unit,
    endDate: LocalDate?, onEndDateClick: () -> Unit,
    time: LocalTime?, onTimeClick: () -> Unit,
    frequency: String, onFrequencyChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = quantity,
        onValueChange = { if (it.all(Char::isDigit)) onQuantityChange(it) },
        label = { Text("Quantity (mg, ml, pills, etc.)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )

    ReadOnlyTextField(
        value = endDate?.format(dateFormatter) ?: "",
        label = "End Date",
        onClick = onEndDateClick
    )

    ReadOnlyTextField(
        value = time?.format(timeFormatter) ?: "",
        label = "Time",
        onClick = onTimeClick
    )

    FrequencyDropdown(frequency, onFrequencyChange)

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notes (optional)") },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * A read-only text field that looks like a text field but acts like a button.
 */
@Composable
private fun ReadOnlyTextField(value: String, label: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

/**
 * A dropdown menu for selecting the medicine frequency.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrequencyDropdown(frequency: String, onFrequencyChange: (String) -> Unit) {
    val frequencyOptions = listOf("Every day", "Every week", "Every month")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = frequency,
            onValueChange = {},
            readOnly = true,
            label = { Text("Frequency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            frequencyOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onFrequencyChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Displays the Cancel and Confirm buttons.
 */
@Composable
private fun ActionButtons(onCancel: () -> Unit, onConfirm: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onCancel) {
            Text("Cancel")
        }
        Button(onClick = onConfirm) {
            Text("Confirm")
        }
    }
}

/**
 * A dialog for picking the end date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EndDateDatePickerDialog(onDismissRequest: () -> Unit, onDateSelected: (LocalDate) -> Unit) {
    val datePickerState = rememberDatePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    onDateSelected(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate())
                }
            }) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        text = { DatePicker(state = datePickerState) }
    )
}

/**
 * A dialog for picking the intake time.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntakeTimePicker(onDismissRequest: () -> Unit, onTimeSelected: (LocalTime) -> Unit) {
    val timePickerState = rememberTimePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
            }) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        text = { TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth()) }
    )
}

/**
 * A side effect that requests notification permissions on Android 13+.
 */
@Composable
private fun RequestNotificationPermission(context: Context) {
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }
}

/**
 * Schedules a repeating reminder notification for a medicine.
 *
 * @param context The application context.
 * @param medicineName The name of the medicine.
 * @param time The time of the reminder.
 * @param frequency The frequency of the reminder.
 */
private fun scheduleMedicineReminder(
    context: Context,
    medicineName: String,
    time: LocalTime,
    frequency: String
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("medicineName", medicineName)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        medicineName.hashCode(), // Unique ID for each medicine
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, time.hour)
        set(Calendar.MINUTE, time.minute)
        set(Calendar.SECOND, 0)
        // If the time is in the past, schedule it for the next day
        if (before(Calendar.getInstance())) {
            add(Calendar.DATE, 1)
        }
    }

    val interval = when (frequency) {
        "Every day" -> AlarmManager.INTERVAL_DAY
        "Every week" -> AlarmManager.INTERVAL_DAY * 7
        "Every month" -> AlarmManager.INTERVAL_DAY * 30 // Note: This is an approximation
        else -> AlarmManager.INTERVAL_DAY
    }

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        interval,
        pendingIntent
    )
}

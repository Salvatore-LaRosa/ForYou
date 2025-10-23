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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.foryou.R
import com.example.foryou.data.local.entity.Medicine
import com.example.foryou.notification.ReminderReceiver
import com.example.foryou.viewmodel.MedicineViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * Screen for adding a new medicine. The user can input the name, frequency, quantity,
 * end date, time, and notes. It also includes logic for requesting notification permissions
 * and scheduling reminders.
 *
 * @param navController The NavController for navigation.
 * @param medicineViewModel The ViewModel for interacting with medicine data.
 * @param userEmail The email of the currently logged-in user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    userEmail: String
) {
    val context = LocalContext.current

    // State for the form fields.
    var nome by remember { mutableStateOf("") }
    var frequenza by remember { mutableStateOf("Tutti i giorni") }
    var quantita by remember { mutableStateOf("") }
    var dataFine by remember { mutableStateOf<LocalDate?>(null) }
    var orario by remember { mutableStateOf<LocalTime?>(null) }
    var note by remember { mutableStateOf("") }

    // State for the visibility of the date and time pickers.
    var mostraDatePicker by remember { mutableStateOf(false) }
    var mostraTimePicker by remember { mutableStateOf(false) }

    // Request notification permission when the screen is launched.
    RequestNotificationPermission(context)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Aggiungi medicinale",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_backarrow),
                            contentDescription = "Indietro",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Field for the medicine name.
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                placeholder = { Text("Inserisci il nome del medicinale") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            // Dropdown for the frequency.
            FrequencyDropdown(frequenza) { frequenza = it }

            // Field for the quantity.
            OutlinedTextField(
                value = quantita,
                onValueChange = { if (it.all(Char::isDigit)) quantita = it },
                label = { Text("Quantità") },
                placeholder = { Text("Solo numeri es. 1, 2, 3...") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            // Read-only field for the end date, opens a dialog.
            ReadOnlyTextField(
                value = dataFine?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                label = "Fine",
                placeholder = "Seleziona data di fine",
                onClick = { mostraDatePicker = true }
            )

            // Read-only field for the time, opens a dialog.
            ReadOnlyTextField(
                value = orario?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                label = "Orario",
                placeholder = "Seleziona orario di assunzione",
                onClick = { mostraTimePicker = true }
            )

            // Field for notes.
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                placeholder = { Text("Inserisci eventuali note") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Button to add the medicine.
            Button(
                onClick = {
                    // Validate fields and insert the medicine.
                    if (nome.isNotBlank() && quantita.isNotBlank() && dataFine != null && orario != null) {
                        val medicine = Medicine(
                            name = nome,
                            frequency = frequenza,
                            quantity = quantita,
                            endDate = dataFine.toString(),
                            notes = note.ifBlank { null },
                            userEmail = userEmail,
                            creationDate = LocalDate.now().toString()
                        )
                        medicineViewModel.insertMedicine(medicine)
                        scheduleMedicineReminder(context, nome, orario!!, frequenza)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Aggiungi", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    // Dialog for selecting the end date.
    if (mostraDatePicker) {
        EndDateDatePickerDialog(
            onDismissRequest = { mostraDatePicker = false },
            onDateSelected = { dataFine = it; mostraDatePicker = false }
        )
    }

    // Dialog for selecting the time.
    if (mostraTimePicker) {
        IntakeTimePicker(
            onDismissRequest = { mostraTimePicker = false },
            onTimeSelected = { orario = it; mostraTimePicker = false }
        )
    }
}

/**
 * A read-only text field that opens a dialog on click.
 * Used for date and time selection.
 *
 * @param value The value to display.
 * @param label The label of the field.
 * @param placeholder The placeholder text.
 * @param onClick The action to perform on click.
 */
@Composable
private fun ReadOnlyTextField(value: String, label: String, placeholder: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary
        )
    )
}

/**
 * A dropdown menu for selecting the medicine intake frequency.
 *
 * @param frequenza The currently selected frequency.
 * @param onFrequenzaChange The callback function to update the frequency.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrequencyDropdown(frequenza: String, onFrequenzaChange: (String) -> Unit) {
    val options = listOf("Tutti i giorni", "Ogni settimana", "Ogni mese")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = frequenza,
            onValueChange = {},
            readOnly = true,
            label = { Text("Frequenza") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onFrequenzaChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Dialog for selecting the end date.
 *
 * @param onDismissRequest The function to call when the dialog is dismissed.
 * @param onDateSelected The callback for the selected date.
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
            }) { Text("Conferma") }
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Annulla") } },
        text = { DatePicker(state = datePickerState) }
    )
}

/**
 * Dialog for selecting the intake time.
 *
 * @param onDismissRequest The function to call when the dialog is dismissed.
 * @param onTimeSelected The callback for the selected time.
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
            }) { Text("Conferma") }
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Annulla") } },
        text = { TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth()) }
    )
}

/**
 * Requests notification permission if it has not been granted already.
 * Uses a LaunchedEffect to perform the request only once.
 *
 * @param context The application context.
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
 * Schedules a reminder for taking a medicine using AlarmManager.
 * The reminder is repeated based on the specified frequency.
 *
 * @param context The application context.
 * @param nome The name of the medicine.
 * @param orario The intake time.
 * @param frequenza The intake frequency.
 */
private fun scheduleMedicineReminder(
    context: Context,
    nome: String,
    orario: LocalTime,
    frequenza: String
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("medicineName", nome)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        nome.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, orario.hour)
        set(Calendar.MINUTE, orario.minute)
        set(Calendar.SECOND, 0)
        // If the time has already passed today, schedule for tomorrow.
        if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
    }

    // Set the repetition interval based on the frequency.
    val intervallo = when (frequenza) {
        "Tutti i giorni" -> AlarmManager.INTERVAL_DAY
        "Ogni settimana" -> AlarmManager.INTERVAL_DAY * 7
        // Approximation for a month.
        "Ogni mese" -> AlarmManager.INTERVAL_DAY * 30
        else -> AlarmManager.INTERVAL_DAY
    }

    // Schedule the repeating reminder.
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        intervallo,
        pendingIntent
    )
}

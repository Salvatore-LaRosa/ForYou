package com.example.foryou

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foryou.data.SessionManager
import com.example.foryou.data.UserDatabase
import com.example.foryou.navigation.NavGraph
import com.example.foryou.repository.MedicineRepository
import com.example.foryou.repository.UserRepository
import com.example.foryou.ui.theme.ForYouTheme
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.AuthViewModelFactory
import com.example.foryou.viewmodel.MedicineViewModel
import com.example.foryou.viewmodel.MedicineViewModelFactory
import com.plcoding.biometricauth.BiometricPromptManager
import com.plcoding.biometricauth.BiometricPromptManager.BiometricResult

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy { BiometricPromptManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- Notifiche ---
        createNotificationChannel()
        requestNotificationPermission()

        // --- Database e repository ---
        val database = UserDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        val medicineRepository = MedicineRepository(database.medicineDao())

        // --- Gestione sessione utente ---
        val sessionManager = SessionManager(this)

        // --- ViewModel factories ---
        val authFactory = AuthViewModelFactory(userRepository, sessionManager)
        val medicineFactory = MedicineViewModelFactory(medicineRepository)

        setContent {
            ForYouTheme {
                val authViewModel: AuthViewModel = viewModel(factory = authFactory)
                val medicineViewModel: MedicineViewModel = viewModel(factory = medicineFactory)
                val navController = rememberNavController()

                val biometricResult by promptManager.promptResults.collectAsState(initial = null)
                val enrollLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { println("Activity result: $it") }
                )

                // Se l'utente non ha configurato la biometria → apri impostazioni
                LaunchedEffect(biometricResult) {
                    if (biometricResult is BiometricResult.AuthenticationNotSet && Build.VERSION.SDK_INT >= 30) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                        enrollLauncher.launch(enrollIntent)
                    }
                }

                // --- Navigazione principale dell’app ---
                NavGraph(
                    navController = navController,
                    viewModel = authViewModel,
                    medicineViewModel = medicineViewModel,
                    promptManager = promptManager, // 👈 Passaggio del manager biometrico
                    modifier = androidx.compose.ui.Modifier
                )
            }
        }
    }

    // --- Notifiche ---
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Promemoria Farmaci"
            val descriptionText = "Notifiche per ricordarti di assumere i farmaci"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("medicine_reminder_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }
}

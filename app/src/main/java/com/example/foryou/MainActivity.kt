package com.example.foryou

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
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

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy { BiometricPromptManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel()
        requestNotificationPermission()

        // Database e repository
        val database = UserDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        val medicineRepository = MedicineRepository(database.medicineDao())

        // Sessione utente
        val sessionManager = SessionManager(this)

        // ViewModel factories
        val authFactory = AuthViewModelFactory(userRepository, sessionManager)
        val medicineFactory = MedicineViewModelFactory(medicineRepository)

        setContent {
            ForYouTheme {
                val authViewModel: AuthViewModel = viewModel(factory = authFactory)
                val medicineViewModel: MedicineViewModel = viewModel(factory = medicineFactory)
                val navController = rememberNavController()

                // 🔗 Passa promptManager alla NavGraph
                NavGraph(
                    navController = navController,
                    viewModel = authViewModel,
                    medicineViewModel = medicineViewModel,
                    promptManager = promptManager
                )
            }
        }
    }

    // --- Canale notifiche ---
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Promemoria Farmaci"
            val descriptionText = "Notifiche per ricordarti di assumere i farmaci"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("medicine_reminder_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // --- Permesso notifiche ---
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

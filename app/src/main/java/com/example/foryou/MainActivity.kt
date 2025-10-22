package com.example.foryou

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.foryou.data.session.SessionManager
import com.example.foryou.data.local.UserDatabase
import com.example.foryou.navigation.NavGraph
import com.example.foryou.repository.MedicineRepository
import com.example.foryou.repository.UserRepository
import com.example.foryou.ui.theme.ForYouTheme
import com.example.foryou.util.BiometricPromptManager
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.AuthViewModelFactory
import com.example.foryou.viewmodel.MedicineViewModel
import com.example.foryou.viewmodel.MedicineViewModelFactory

/**
 * The main and only activity of the application.
 * It sets up the Compose content, initializes dependencies, handles notification setup,
 * and manages the navigation graph.
 */
class MainActivity : AppCompatActivity() {

    // Lazily initializes the BiometricPromptManager, tying it to this activity's lifecycle.
    private val promptManager by lazy { BiometricPromptManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables edge-to-edge display for a more immersive UI.
        enableEdgeToEdge()

        // Setup for notifications.
        createNotificationChannel()
        requestNotificationPermission()

        // --- Dependency Injection Setup ---
        // Manually setting up dependencies. In a larger app, a DI framework like Hilt would be preferable.

        // Database and Repositories
        val database = UserDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        val medicineRepository = MedicineRepository(database.medicineDao())

        // User Session Management
        val sessionManager = SessionManager(this)

        // ViewModel Factories
        val authFactory = AuthViewModelFactory(userRepository, sessionManager)
        val medicineFactory = MedicineViewModelFactory(medicineRepository)

        // Set the main content of the activity to be our Composable UI.
        setContent {
            ForYouTheme {
                // Obtain ViewModels using their respective factories.
                val authViewModel: AuthViewModel = viewModel(factory = authFactory)
                val medicineViewModel: MedicineViewModel = viewModel(factory = medicineFactory)
                val navController = rememberNavController()

                // Set up the navigation graph, passing all necessary dependencies.
                NavGraph(
                    navController = navController,
                    viewModel = authViewModel,
                    medicineViewModel = medicineViewModel,
                    promptManager = promptManager
                )
            }
        }
    }

    /**
     * Creates the notification channel for medicine reminders.
     * This is required for Android 8.0 (API level 26) and higher.
     * The channel is created with high importance to ensure reminders are visible.
     */
    private fun createNotificationChannel() {
        val name = "Medicine Reminders"
        val descriptionText = "Notifications to remind you to take your medicine"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("medicine_reminder_channel", name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system.
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Requests the POST_NOTIFICATIONS permission on Android 13 (API 33) and higher.
     * This is necessary to be able to show notifications to the user.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission from the user.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1 // This request code can be used in onRequestPermissionsResult if needed.
            )
        }
    }
}

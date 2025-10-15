package com.example.foryou

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.foryou.data.UserDatabase
import com.example.foryou.navigation.NavGraph
import com.example.foryou.repository.MedicineRepository
import com.example.foryou.repository.UserRepository
import com.example.foryou.ui.theme.ForYouTheme
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.AuthViewModelFactory
import com.example.foryou.viewmodel.MedicineViewModel
import com.example.foryou.viewmodel.MedicineViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crea il canale di notifica
        createNotificationChannel()
        requestNotificationPermission()

        // Controlla se l'app è stata aperta da una notifica
        val openDetails = intent?.getBooleanExtra("openDetails", false) ?: false
        val medicineName = intent?.getStringExtra("medicineName")

        val database = UserDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        val authFactory = AuthViewModelFactory(userRepository)

        val medicineRepository = MedicineRepository(database.medicineDao())
        val medicineFactory = MedicineViewModelFactory(medicineRepository)

        setContent {
            ForYouTheme {
                val authViewModel: AuthViewModel = viewModel(factory = authFactory)
                val medicineViewModel: MedicineViewModel = viewModel(factory = medicineFactory)
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    viewModel = authViewModel,
                    medicineViewModel = medicineViewModel,
                    modifier = if (openDetails && medicineName != null) {
                        androidx.compose.ui.Modifier
                    } else {
                        androidx.compose.ui.Modifier
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
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
}

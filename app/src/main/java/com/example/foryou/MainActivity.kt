package com.example.foryou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.foryou.data.UserDatabase
import com.example.foryou.repository.UserRepository
import com.example.foryou.repository.MedicineRepository
import com.example.foryou.viewmodel.AuthViewModel
import com.example.foryou.viewmodel.AuthViewModelFactory
import com.example.foryou.viewmodel.MedicineViewModel
import com.example.foryou.viewmodel.MedicineViewModelFactory
import com.example.foryou.navigation.NavGraph
import com.example.foryou.ui.theme.ForYouTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                    medicineViewModel = medicineViewModel
                )
            }
        }
    }
}

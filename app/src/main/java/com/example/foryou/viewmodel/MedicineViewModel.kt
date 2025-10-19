package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foryou.data.Medicine
import com.example.foryou.data.MedicineIntake
import com.example.foryou.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MedicineViewModel(private val repository: MedicineRepository) : ViewModel() {

    // Inserisce un farmaco nel database
    fun insertMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.insertMedicine(medicine)
        }
    }

    // Restituisce tutti i farmaci dell'utente senza filtrare per data
    fun getAllMedicinesForUser(email: String): Flow<List<Medicine>> {
        return repository.getAllMedicinesForUser(email)
    }

    fun markAsTaken(medicineName: String, userEmail: String, date: String) {
        viewModelScope.launch {
            repository.insertIntake(
                MedicineIntake(
                    medicineName = medicineName,
                    userEmail = userEmail,
                    date = date,
                    taken = true
                )
            )
        }
    }

    fun getIntakesForMedicine(email: String, medicineName: String): Flow<List<MedicineIntake>> {
        return repository.getIntakesForMedicine(email, medicineName)
    }
}

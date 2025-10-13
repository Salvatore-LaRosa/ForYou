package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foryou.data.Medicine
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

    // Restituisce i farmaci filtrati per oggi (o qualsiasi data se vuoi usarla)
    fun getMedicinesForUserAndDate(email: String, today: String): Flow<List<Medicine>> {
        return repository.getMedicinesForUserAndDate(email, today)
    }
}

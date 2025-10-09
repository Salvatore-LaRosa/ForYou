package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foryou.data.Medicine
import com.example.foryou.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MedicineViewModel(private val dao: MedicineRepository) : ViewModel() {

    fun insertMedicine(medicine: Medicine) {
        viewModelScope.launch {
            dao.insertMedicine(medicine)
        }
    }

    fun getMedicinesForUserAndDate(email: String, today: String): Flow<List<Medicine>> {
        return dao.getMedicinesForUserAndDate(email, today)
    }
}

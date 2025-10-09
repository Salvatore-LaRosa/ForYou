package com.example.foryou.repository

import com.example.foryou.data.Medicine
import com.example.foryou.data.MedicineDao
import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val dao: MedicineDao) {
    suspend fun insertMedicine(medicine: Medicine) = dao.insertMedicine(medicine)

    fun getMedicinesForUserAndDate(email: String, today: String): Flow<List<Medicine>> =
        dao.getMedicinesForUserAndDate(email, today)
}

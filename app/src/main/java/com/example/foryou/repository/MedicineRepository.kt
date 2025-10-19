package com.example.foryou.repository

import com.example.foryou.data.Medicine
import com.example.foryou.data.MedicineDao
import com.example.foryou.data.MedicineIntake
import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val dao: MedicineDao) {
    suspend fun insertMedicine(medicine: Medicine) = dao.insertMedicine(medicine)

    suspend fun insertIntake(intake: MedicineIntake) = dao.insertIntake(intake)

    fun getAllMedicinesForUser(email: String): Flow<List<Medicine>> =
        dao.getAllMedicinesForUser(email)

    fun getIntakesForMedicine(email: String, medicineName: String): Flow<List<MedicineIntake>> =
        dao.getIntakesForMedicine(email, medicineName)
}


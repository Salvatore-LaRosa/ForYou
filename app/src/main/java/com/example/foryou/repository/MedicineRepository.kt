package com.example.foryou.repository

import com.example.foryou.data.Medicine
import com.example.foryou.data.MedicineDao
import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val dao: MedicineDao) {
    suspend fun insertMedicine(medicine: Medicine) = dao.insertMedicine(medicine)

    fun getAllMedicinesForUser(email: String): Flow<List<Medicine>> =
        dao.getAllMedicinesForUser(email)

    fun getMedicinesForUserAndDate(email: String, today: String) =
        dao.getMedicinesForUserAndDate(email, today)
}


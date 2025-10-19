package com.example.foryou.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntake(intake: MedicineIntake)

    @Query("SELECT * FROM medicines WHERE userEmail = :email AND endDate >= :today")
    fun getMedicinesForUserAndDate(email: String, today: String): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE userEmail = :email")
    fun getAllMedicinesForUser(email: String): Flow<List<Medicine>>

    @Query("DELETE FROM medicines WHERE name = :medicineName AND userEmail = :email AND endDate >= :fromDate")
    suspend fun deleteMedicinesFromDate(medicineName: String, email: String, fromDate: String)

    @Query("UPDATE medicine_intake SET taken = :taken WHERE medicineName = :medicineName AND date = :date AND userEmail = :email")
    suspend fun updateIntakeStatus(medicineName: String, email: String, date: String, taken: Boolean)

    @Query("SELECT * FROM medicine_intake WHERE userEmail = :email AND medicineName = :medicineName")
    fun getIntakesForMedicine(email: String, medicineName: String): Flow<List<MedicineIntake>>
}

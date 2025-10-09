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

    @Query("SELECT * FROM medicines WHERE userEmail = :email AND endDate >= :today")
    fun getMedicinesForUserAndDate(email: String, today: String): Flow<List<Medicine>>
}

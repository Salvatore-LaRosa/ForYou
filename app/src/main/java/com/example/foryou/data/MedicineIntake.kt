package com.example.foryou.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_intake")
data class MedicineIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineName: String,
    val userEmail: String,
    val date: String, // yyyy-MM-dd
    val taken: Boolean // true = assunto, false = non assunto
)

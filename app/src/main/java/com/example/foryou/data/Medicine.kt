package com.example.foryou.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val frequency: String,
    val quantity: String,
    val endDate: String,
    val notes: String?,
    val userEmail: String,
    val creationDate: String
)

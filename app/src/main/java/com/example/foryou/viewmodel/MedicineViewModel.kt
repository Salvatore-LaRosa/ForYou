package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foryou.data.Medicine
import com.example.foryou.data.MedicineIntake
import com.example.foryou.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing medicine and intake data.
 * It interacts with the [MedicineRepository] to perform CRUD operations.
 *
 * @param repository The repository for accessing medicine and intake data.
 */
class MedicineViewModel(private val repository: MedicineRepository) : ViewModel() {

    /**
     * Inserts a new medicine into the database.
     *
     * @param medicine The [Medicine] object to insert.
     */
    fun insertMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.insertMedicine(medicine)
        }
    }

    /**
     * Retrieves all medicines for a given user.
     *
     * @param email The user's email.
     * @return A [Flow] emitting a list of [Medicine] objects.
     */
    fun getAllMedicinesForUser(email: String): Flow<List<Medicine>> {
        return repository.getAllMedicinesForUser(email)
    }

    /**
     * Deletes a medicine for a user starting from a specific date.
     * This is achieved by setting the end date of the medicine to the given date.
     *
     * @param medicineName The name of the medicine to delete.
     * @param userEmail The user's email.
     * @param fromDate The date from which to delete the medicine (inclusive).
     */
    fun deleteFromDate(medicineName: String, userEmail: String, fromDate: String) {
        viewModelScope.launch {
            repository.deleteFromDate(medicineName, userEmail, fromDate)
        }
    }

    /**
     * Marks a medicine as taken for a specific date.
     * This creates a new [MedicineIntake] record.
     *
     * @param medicineName The name of the medicine.
     * @param userEmail The user's email.
     * @param date The date the medicine was taken.
     */
    fun markAsTaken(medicineName: String, userEmail: String, date: String) {
        viewModelScope.launch {
            val intake = MedicineIntake(
                medicineName = medicineName,
                userEmail = userEmail,
                date = date,
                taken = true
            )
            repository.insertIntake(intake)
        }
    }

    /**
     * Retrieves all intakes for a specific medicine and user.
     *
     * @param email The user's email.
     * @param medicineName The name of the medicine.
     * @return A [Flow] emitting a list of [MedicineIntake] objects.
     */
    fun getIntakesForMedicine(email: String, medicineName: String): Flow<List<MedicineIntake>> {
        return repository.getIntakesForMedicine(email, medicineName)
    }
}

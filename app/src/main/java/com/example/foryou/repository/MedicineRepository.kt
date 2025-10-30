package com.example.foryou.repository

import com.example.foryou.data.local.entity.Medicine
import com.example.foryou.data.local.dao.MedicineDao
import com.example.foryou.data.local.entity.MedicineIntake
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing medicine and intake data.
 * This class abstracts the data source (in this case, the Room DAO) from the rest of the app,
 * providing a clean API for data access. It acts as a single source of truth for medicine data.
 *
 * @param dao The Data Access Object for medicines and intakes.
 */
open class MedicineRepository(private val dao: MedicineDao) {

    /**
     * Inserts a new medicine into the database.
     * This is a suspend function that calls the corresponding DAO method.
     *
     * @param medicine The [Medicine] object to insert.
     */
    open suspend fun insertMedicine(medicine: Medicine) = dao.insertMedicine(medicine)

    /**
     * Inserts a new medicine intake record into the database.
     * This is a suspend function that calls the corresponding DAO method.
     *
     * @param intake The [MedicineIntake] object to insert.
     */
    open suspend fun insertIntake(intake: MedicineIntake) = dao.insertIntake(intake)

    /**
     * Retrieves all medicines registered for a specific user, without any date filtering.
     *
     * @param email The user's email.
     * @return A [Flow] emitting a list of all [Medicine] objects for the user.
     */
    open fun getAllMedicinesForUser(email: String): Flow<List<Medicine>> =
        dao.getAllMedicinesForUser(email)

    /**
     * Deletes a medicine record for a user if its end date is on or after the specified date.
     * Note: This performs a hard delete of the entire medicine record, which might not be the
     * desired behavior. A safer approach would be to update the medicine's end date to effectively
     * "soft delete" it from a certain day onwards.
     *
     * @param medicineName The name of the medicine to delete.
     * @param email The user's email.
     * @param fromDate The date from which the deletion condition is checked.
     */
    open suspend fun deleteFromDate(medicineName: String, email: String, fromDate: String) =
        dao.deleteMedicinesFromDate(medicineName, email, fromDate)

    /**
     * Retrieves all intake records for a specific medicine and user.
     *
     * @param email The user's email.
     * @param medicineName The name of the medicine.
     * @return A [Flow] emitting a list of [MedicineIntake] objects.
     */
    open fun getIntakesForMedicine(email: String, medicineName: String): Flow<List<MedicineIntake>> =
        dao.getIntakesForMedicine(email, medicineName)
}

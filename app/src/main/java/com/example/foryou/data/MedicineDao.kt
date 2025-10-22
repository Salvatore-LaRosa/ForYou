package com.example.foryou.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the [Medicine] and [MedicineIntake] entities.
 * Provides methods to interact with the medicine and intake tables in the database.
 */
@Dao
interface MedicineDao {

    /**
     * Inserts a new medicine into the database. If a medicine with the same primary key already exists,
     * it will be replaced.
     *
     * @param medicine The [Medicine] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine)

    /**
     * Inserts a new medicine intake record. If a record for the same medicine and date already exists,
     * it will be replaced.
     *
     * @param intake The [MedicineIntake] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntake(intake: MedicineIntake)

    /**
     * Retrieves all medicines ever registered for a specific user.
     *
     * @param email The user's email.
     * @return A [Flow] emitting a list of all [Medicine] objects for the user.
     */
    @Query("SELECT * FROM medicines WHERE userEmail = :email")
    fun getAllMedicinesForUser(email: String): Flow<List<Medicine>>

    /**
     * Deletes a medicine for a user starting from a specific date.
     * This query is designed to remove future occurrences of a medicine, but it's important to note
     * that it permanently deletes records. A softer approach, like setting an end date, might be preferable.
     *
     * @param medicineName The name of the medicine to delete.
     * @param email The user's email.
     * @param fromDate The date from which to delete the medicine records.
     */
    @Query("DELETE FROM medicines WHERE name = :medicineName AND userEmail = :email AND endDate >= :fromDate")
    suspend fun deleteMedicinesFromDate(medicineName: String, email: String, fromDate: String)

    /**
     * Retrieves all intake records for a specific medicine and user.
     *
     * @param email The user's email.
     * @param medicineName The name of the medicine.
     * @return A [Flow] emitting a list of [MedicineIntake] objects.
     */
    @Query("SELECT * FROM medicine_intake WHERE userEmail = :email AND medicineName = :medicineName")
    fun getIntakesForMedicine(email: String, medicineName: String): Flow<List<MedicineIntake>>
}

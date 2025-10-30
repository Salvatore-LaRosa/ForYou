package com.example.foryou.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.foryou.data.local.dao.MedicineDao
import com.example.foryou.data.local.entity.Medicine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws
import kotlin.jvm.java

/**
 * Instrumented test for the [MedicineDao].
 * This class tests the database operations related to the Medicine entity.
 */
@RunWith(AndroidJUnit4::class)
class MedicineDaoTest {

    private lateinit var medicineDao: MedicineDao
    private lateinit var db: UserDatabase

    /**
     * Creates an in-memory database before each test.
     * This ensures that each test is isolated and runs on a clean database.
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, UserDatabase::class.java
        )
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        medicineDao = db.medicineDao()
    }

    /**
     * Closes the database after each test to release resources.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * Tests the insertion and retrieval of a [Medicine] from the database.
     * It inserts a medicine and then verifies that the retrieved medicine is the same.
     */
    @Test
    @Throws(Exception::class)
    fun insertAndGetMedicine() = runBlocking {
        val medicine = Medicine(
            id = 1,
            name = "Aspirin",
            frequency = "Daily",
            quantity = "1 pill",
            endDate = "2024-12-31",
            time = "10:00",
            notes = "Take with food",
            userEmail = "test@test.com",
            creationDate = "2024-01-01"
        )
        medicineDao.insertMedicine(medicine)
        val allMedicines = medicineDao.getAllMedicinesForUser("test@test.com").first()
        assertEquals(allMedicines[0], medicine)
    }
}

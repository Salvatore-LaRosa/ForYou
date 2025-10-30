package com.example.foryou

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.foryou.data.local.entity.Medicine
import com.example.foryou.data.local.entity.MedicineIntake
import com.example.foryou.repository.MedicineRepository
import com.example.foryou.viewmodel.MedicineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for the [MedicineViewModel].
 * This class tests the ViewModel's logic and its interaction with the repository.
 */
@ExperimentalCoroutinesApi
class MedicineViewModelTest {

    // This rule swaps the background executor used by the Architecture Components with a different one which executes each task synchronously.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // A test dispatcher for controlling the execution of coroutines in tests.
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MedicineViewModel
    private lateinit var repository: MedicineRepository

    /**
     * Sets up the test environment before each test.
     * It replaces the main dispatcher with a test dispatcher and initializes the ViewModel with a mock repository.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = MedicineViewModel(repository, testDispatcher)
    }

    /**
     * Cleans up the test environment after each test.
     * It resets the main dispatcher to its original state.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that calling `insertMedicine` on the ViewModel correctly calls the repository's `insertMedicine` method.
     */
    @Test
    fun `insertMedicine should call repository's insertMedicine`() = runTest {
        val medicine = Medicine(name = "Aspirin", frequency = "Daily", quantity = "1 pill", endDate = "2024-12-31", time = "10:00", notes = "Take with food", userEmail = "test@test.com", creationDate = "2024-01-01")
        viewModel.insertMedicine(medicine)
        // Advances the scheduler to execute the coroutine launched by the ViewModel.
        testDispatcher.scheduler.advanceUntilIdle()
        // Verifies that the suspend function on the repository was called.
        verify(repository).insertMedicine(medicine)
    }

    /**
     * Tests that `getAllMedicinesForUser` returns the correct data from the repository.
     */
    @Test
    fun `getAllMedicinesForUser should return medicines from repository`() = runTest {
        val email = "test@test.com"
        val medicines = listOf(Medicine(name = "Aspirin", frequency = "Daily", quantity = "1 pill", endDate = "2024-12-31", time = "10:00", notes = "Take with food", userEmail = email, creationDate = "2024-01-01"))
        whenever(repository.getAllMedicinesForUser(email)).thenReturn(flowOf(medicines))

        val result = viewModel.getAllMedicinesForUser(email).first()

        assertEquals(medicines, result)
    }

    /**
     * Tests that `getIntakesForMedicine` returns the correct data from the repository.
     */
    @Test
    fun `getIntakesForMedicine should return intakes from repository`() = runTest {
        val email = "test@test.com"
        val medicineName = "Aspirin"
        val intakes = listOf(MedicineIntake(id = 1, medicineName = medicineName, userEmail = email, date = "2023-01-10", taken = true))
        whenever(repository.getIntakesForMedicine(email, medicineName)).thenReturn(flowOf(intakes))

        val result = viewModel.getIntakesForMedicine(email, medicineName).first()

        assertEquals(intakes, result)
    }
}

package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foryou.repository.MedicineRepository

/**
 * Factory for creating instances of [MedicineViewModel].
 * This class is responsible for providing the [MedicineViewModel] with its dependencies.
 *
 * @param repository The repository for accessing medicine and intake data.
 */
class MedicineViewModelFactory(private val repository: MedicineRepository) :
    ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given `ViewModel` class.
     *
     * @param modelClass A `Class` whose instance is requested.
     * @return A newly created `ViewModel`.
     * @throws IllegalArgumentException if the `ViewModel` class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

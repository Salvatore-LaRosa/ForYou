package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foryou.data.session.SessionManager
import com.example.foryou.repository.UserRepository

/**
 * Factory for creating instances of [AuthViewModel].
 * This class is responsible for providing the [AuthViewModel] with its dependencies.
 *
 * @param repository The repository for accessing user data.
 * @param sessionManager The manager for handling user sessions.
 */
class AuthViewModelFactory(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given `ViewModel` class.
     *
     * @param modelClass A `Class` whose instance is requested.
     * @return A newly created `ViewModel`.
     * @throws IllegalArgumentException if the `ViewModel` class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

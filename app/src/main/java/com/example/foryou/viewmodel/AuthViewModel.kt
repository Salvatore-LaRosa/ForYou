package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foryou.data.SessionManager
import com.example.foryou.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling authentication-related logic,
 * such as login, registration, PIN management, and session handling.
 *
 * @param repository The repository for accessing user data.
 * @param sessionManager The manager for handling user sessions.
 */
class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Private mutable state for the user's email, exposed as a read-only StateFlow.
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    // Private mutable state for the user's password, exposed as a read-only StateFlow.
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // Private mutable state for the authentication result message, exposed as a read-only StateFlow.
    private val _authResult = MutableStateFlow<String?>(null)
    val authResult = _authResult.asStateFlow()

    /**
     * Updates the email value in the state.
     * @param newEmail The new email string.
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    /**
     * Updates the password value in the state.
     * @param newPassword The new password string.
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Attempts to log in the user with the current email and password.
     * Updates the authResult StateFlow with the result of the operation.
     */
    fun login() {
        viewModelScope.launch {
            val result = repository.loginUser(email.value, password.value)
            if (result) {
                sessionManager.saveUserEmail(email.value)
                _authResult.value = "Login success"
            } else {
                _authResult.value = "Invalid credentials"
            }
        }
    }

    /**
     * Attempts to register a new user with the current email and password.
     * Updates the authResult StateFlow with the result of the operation.
     */
    fun register() {
        viewModelScope.launch {
            val result = repository.registerUser(email.value, password.value)
            _authResult.value = if (result) "Registration success" else "User already exists"
        }
    }

    /**
     * Clears the authentication result message.
     */
    fun clearMessage() {
        _authResult.value = null
    }

    /**
     * Retrieves the saved user email from the session manager.
     * @return The user's email, or null if not found.
     */
    fun getSavedEmail(): String? = sessionManager.getUserEmail()

    /**
     * Checks if a PIN is set for the given email.
     * @param email The user's email.
     * @return True if a PIN is set, false otherwise.
     */
    suspend fun isPinSet(email: String): Boolean {
        return repository.isPinSet(email)
    }

    /**
     * Verifies if the provided PIN is correct for the given email.
     * @param email The user's email.
     * @param pin The PIN to verify.
     * @return True if the PIN is correct, false otherwise.
     */
    suspend fun verifyPin(email: String, pin: String): Boolean {
        return repository.verifyPin(email, pin)
    }

    /**
     * Saves a new PIN for the given email.
     * @param email The user's email.
     * @param pin The new PIN to save.
     */
    fun saveUserPin(email: String, pin: String) {
        viewModelScope.launch {
            repository.setUserPin(email, pin)
        }
    }

    /**
     * Logs the user out by clearing the session and resetting the ViewModel's state.
     */
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _email.value = ""
            _password.value = ""
            _authResult.value = "Logout success"
        }
    }

    /**
     * Saves the user's email to the session manager.
     * @param email The user's email to save.
     */
    fun saveUserSession(email: String) {
        viewModelScope.launch {
            sessionManager.saveUserEmail(email)
        }
    }
}

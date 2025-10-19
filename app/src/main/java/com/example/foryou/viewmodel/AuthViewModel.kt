package com.example.foryou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foryou.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _authResult = MutableStateFlow<String?>(null)
    val authResult = _authResult.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        viewModelScope.launch {
            val result = repository.loginUser(email.value, password.value)
            _authResult.value = if (result) "Login success" else "Invalid credentials"
        }
    }

    fun register() {
        viewModelScope.launch {
            val result = repository.registerUser(email.value, password.value)
            _authResult.value = if (result) "Registration success" else "User already exists"
        }
    }

    fun clearMessage() {
        _authResult.value = null
    }

    fun logout() {
        viewModelScope.launch {
            _email.value = ""
            _password.value = ""
            _authResult.value = "Logout success"
        }
    }
}

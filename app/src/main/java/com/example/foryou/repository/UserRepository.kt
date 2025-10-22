package com.example.foryou.repository

import com.example.foryou.data.User
import com.example.foryou.data.UserDao

/**
 * Repository for managing user data.
 * This class abstracts the data source (UserDao) from the rest of the app,
 * providing a clean API for user-related operations like registration, login, and PIN management.
 *
 * @param userDao The Data Access Object for users.
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Registers a new user in the database.
     * It first checks if a user with the given email already exists to prevent duplicates.
     *
     * @param email The user's email address.
     * @param password The user's password (ideally, this should be a hashed password).
     * @return `true` if the user was successfully registered, `false` if a user with that email already exists.
     */
    suspend fun registerUser(email: String, password: String): Boolean {
        if (userDao.getUserByEmail(email) != null) {
            return false // User already exists
        }
        userDao.insertUser(User(email = email, password = password))
        return true
    }

    /**
     * Authenticates a user by checking if the provided email and password match a record in the database.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return `true` if the credentials are valid, `false` otherwise.
     */
    suspend fun loginUser(email: String, password: String): Boolean {
        return userDao.getUser(email, password) != null
    }

    /**
     * Sets or updates the PIN for a specific user.
     *
     * @param email The email of the user whose PIN is to be set.
     * @param pin The new 4-digit PIN.
     */
    suspend fun setUserPin(email: String, pin: String) {
        userDao.updatePin(email, pin)
    }

    /**
     * Checks if a user has already set a PIN.
     *
     * @param email The user's email.
     * @return `true` if a PIN is set, `false` otherwise.
     */
    suspend fun isPinSet(email: String): Boolean {
        val pin = userDao.getPinByEmail(email)
        return !pin.isNullOrBlank()
    }

    /**
     * Verifies if the provided PIN matches the one stored for the user.
     *
     * @param email The user's email.
     * @param pin The PIN to verify.
     * @return `true` if the PIN is correct, `false` otherwise.
     */
    suspend fun verifyPin(email: String, pin: String): Boolean {
        val savedPin = userDao.getPinByEmail(email)
        return savedPin == pin
    }
}

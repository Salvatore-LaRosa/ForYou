package com.example.foryou.repository

import com.example.foryou.data.User
import com.example.foryou.data.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(email: String, password: String): Boolean {
        val existingUser = userDao.getUserByEmail(email)
        return if (existingUser == null) {
            userDao.insertUser(User(email = email, password = password))
            true
        } else false
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return userDao.getUser(email, password) != null
    }

    suspend fun setUserPin(email: String, pin: String) {
        userDao.updatePin(email, pin)
    }

    suspend fun isPinSet(email: String): Boolean {
        val pin = userDao.getPinByEmail(email)
        return !pin.isNullOrEmpty()
    }

    suspend fun verifyPin(email: String, pin: String): Boolean {
        val savedPin = userDao.getPinByEmail(email)
        return savedPin != null && savedPin == pin
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}

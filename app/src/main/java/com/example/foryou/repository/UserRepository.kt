package com.example.foryou.repository

import com.example.foryou.data.User
import com.example.foryou.data.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(email: String, password: String): Boolean {
        val existingUser = userDao.getUserByEmail(email)
        return if (existingUser == null) {
            userDao.insertUser(User(email = email, password = password))
            true
        } else {
            false // utente già esistente
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return userDao.getUser(email, password) != null
    }
}
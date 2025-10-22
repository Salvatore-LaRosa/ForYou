package com.example.foryou.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for the [User] entity.
 * Provides methods to interact with the users table in the database.
 */
@Dao
interface UserDao {

    /**
     * Inserts a new user into the database. If the user already exists, it will be replaced.
     *
     * @param user The [User] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    /**
     * Retrieves a user from the database based on their email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return A [User] object if a match is found, otherwise null.
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUser(email: String, password: String): User?

    /**
     * Retrieves a user from the database based on their email.
     *
     * @param email The user's email.
     * @return A [User] object if a match is found, otherwise null.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Updates the PIN for a specific user.
     *
     * @param email The email of the user to update.
     * @param pin The new PIN to set.
     */
    @Query("UPDATE users SET pinCode = :pin WHERE email = :email")
    suspend fun updatePin(email: String, pin: String)

    /**
     * Retrieves the PIN for a specific user.
     *
     * @param email The user's email.
     * @return The user's PIN, or null if it is not set.
     */
    @Query("SELECT pinCode FROM users WHERE email = :email LIMIT 1")
    suspend fun getPinByEmail(email: String): String?
}

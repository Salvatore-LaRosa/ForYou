package com.example.foryou.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manages the user's session by storing and retrieving session data from SharedPreferences.
 * This class is responsible for handling simple session information, such as the logged-in user's email.
 *
 * @param context The application context, used to access SharedPreferences.
 */
class SessionManager(context: Context) {

    // Private instance of SharedPreferences, scoped to this class.
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Companion object to hold constants, ensuring consistent key usage.
    companion object {
        private const val USER_EMAIL_KEY = "user_email"
    }

    /**
     * Saves the user's email to the session, effectively logging them in.
     *
     * @param email The email address of the user to save.
     */
    fun saveUserEmail(email: String) {
        // Uses the KTX extension function for a concise and safe edit.
        prefs.edit { putString(USER_EMAIL_KEY, email) }
    }

    /**
     * Retrieves the saved user's email from the session.
     *
     * @return The saved email address, or null if no user is logged in.
     */
    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL_KEY, null)
    }

    /**
     * Clears the user's session data, effectively logging them out.
     */
    fun clearSession() {
        prefs.edit { remove(USER_EMAIL_KEY) }
    }
}

package com.example.foryou.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val USER_EMAIL_KEY = "user_email"
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString(USER_EMAIL_KEY, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL_KEY, null)
    }

    fun clearSession() {
        prefs.edit().remove(USER_EMAIL_KEY).apply()
    }
}

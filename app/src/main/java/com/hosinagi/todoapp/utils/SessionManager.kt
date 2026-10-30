package com.hosinagi.todoapp.utils

import android.content.Context
import androidx.core.content.edit

object SessionManager {
    private const val PREFERENCES_NAME = "todo"
    private const val KEY_IS_LOGIN = "is_Login"
    private const val KEY_USERNAME = "username"

    fun saveLogin(context: Context, username: String) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_IS_LOGIN, true)
            putString(KEY_USERNAME, username)
        }
    }

    fun isLoggedIn(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_IS_LOGIN, false) &&
            !preferences.getString(KEY_USERNAME, null).isNullOrBlank()
    }

    fun getUsername(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_USERNAME, null)?.takeIf { it.isNotBlank() }
    }

    fun logout(context: Context) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit {
            clear()
        }
    }
}

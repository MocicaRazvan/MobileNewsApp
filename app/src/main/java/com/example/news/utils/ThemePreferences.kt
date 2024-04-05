package com.example.news.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ThemePreferences(
    private val context: Context
) {

    companion object {
        const val THEME_PREFERENCES = "com.example.news.THEME_PREFERENCES"
        const val THEME_SAVED = "com.example.news.THEME_SAVED"
    }

    private val sharedPreferences =
        context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)

    fun saveTheme(isDark: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(THEME_SAVED, isDark)
            apply()
        }

        change(isDark)
    }

    fun loadTheme() {
        val isDark = sharedPreferences.getBoolean(THEME_SAVED, false)
        change(isDark)
    }

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_SAVED, false)
    }

    private fun change(isChecked: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
package com.example.spandana.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager private constructor(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        
        @Volatile
        private var INSTANCE: ThemeManager? = null
        
        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun init() {
        val savedTheme = getSavedThemeMode()
        applyTheme(savedTheme)
    }
    
    fun toggleTheme() {
        val currentTheme = getSavedThemeMode()
        val newTheme = if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        saveThemeMode(newTheme)
        applyTheme(newTheme)
    }
    
    fun isDarkMode(): Boolean {
        return getSavedThemeMode() == AppCompatDelegate.MODE_NIGHT_YES
    }
    
    private fun getSavedThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    
    private fun saveThemeMode(themeMode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, themeMode).apply()
    }
    
    private fun applyTheme(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
    
    fun getThemeMode(): Int {
        return getSavedThemeMode()
    }
    
    fun setThemeMode(themeMode: Int) {
        saveThemeMode(themeMode)
        applyTheme(themeMode)
    }
}

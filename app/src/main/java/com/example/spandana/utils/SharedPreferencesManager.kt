package com.example.spandana.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Habits management
    fun saveHabits(habits: List<Any>) {
        val json = gson.toJson(habits)
        prefs.edit().putString("habits", json).apply()
    }

    fun getHabits(): List<Map<String, Any>> {
        val json = prefs.getString("habits", null)
        return if (json != null) {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Mood entries management
    fun saveMoodEntries(entries: List<Any>) {
        val json = gson.toJson(entries)
        prefs.edit().putString("mood_entries", json).apply()
    }

    fun getMoodEntries(): List<Map<String, Any>> {
        val json = prefs.getString("mood_entries", null)
        return if (json != null) {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // User settings
    fun saveWaterReminderInterval(interval: Int) {
        prefs.edit().putInt("water_reminder_interval", interval).apply()
    }

    fun getWaterReminderInterval(): Int {
        return prefs.getInt("water_reminder_interval", 60) // default 60 minutes
    }

    fun isWaterReminderEnabled(): Boolean {
        return prefs.getBoolean("water_reminder_enabled", true)
    }

    fun setWaterReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("water_reminder_enabled", enabled).apply()
    }
}
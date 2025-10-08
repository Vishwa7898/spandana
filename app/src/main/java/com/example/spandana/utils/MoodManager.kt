package com.example.spandana.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.spandana.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MoodManager private constructor(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("mood_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    companion object {
        @Volatile
        private var INSTANCE: MoodManager? = null
        
        fun getInstance(context: Context): MoodManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MoodManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Save mood entry
    fun saveMoodEntry(entry: MoodEntry) {
        val entries = getAllMoodEntries().toMutableList()
        entries.add(entry)
        
        val json = gson.toJson(entries)
        prefs.edit().putString("mood_entries", json).apply()
    }
    
    // Get all mood entries
    fun getAllMoodEntries(): List<MoodEntry> {
        val json = prefs.getString("mood_entries", null)
        return if (json != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    // Get mood entries for specific date
    fun getMoodEntriesForDate(date: String): List<MoodEntry> {
        return getAllMoodEntries().filter { it.date == date }
    }
    
    // Get today's mood entries
    fun getTodayMoodEntries(): List<MoodEntry> {
        val today = dateFormat.format(Date())
        return getMoodEntriesForDate(today)
    }
    
    // Get weekly mood entries
    fun getWeeklyMoodEntries(): List<MoodEntry> {
        val calendar = Calendar.getInstance()
        val weeklyEntries = mutableListOf<MoodEntry>()
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            weeklyEntries.addAll(getMoodEntriesForDate(date))
        }
        
        return weeklyEntries.sortedByDescending { it.timestamp }
    }
    
    // Get monthly mood entries
    fun getMonthlyMoodEntries(): List<MoodEntry> {
        val calendar = Calendar.getInstance()
        val monthlyEntries = mutableListOf<MoodEntry>()
        
        for (i in 29 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            monthlyEntries.addAll(getMoodEntriesForDate(date))
        }
        
        return monthlyEntries.sortedByDescending { it.timestamp }
    }
    
    // Get average mood for date range
    fun getAverageMoodForDateRange(startDate: String, endDate: String): Double {
        val entries = getAllMoodEntries().filter { entry ->
            entry.date >= startDate && entry.date <= endDate
        }
        
        return if (entries.isNotEmpty()) {
            entries.map { it.getMoodValue() }.average()
        } else {
            3.0 // Neutral
        }
    }
    
    // Get weekly average mood
    fun getWeeklyAverageMood(): Double {
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)
        
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(calendar.time)
        
        return getAverageMoodForDateRange(startDate, endDate)
    }
    
    // Get monthly average mood
    fun getMonthlyAverageMood(): Double {
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)
        
        calendar.add(Calendar.DAY_OF_YEAR, -29)
        val startDate = dateFormat.format(calendar.time)
        
        return getAverageMoodForDateRange(startDate, endDate)
    }
    
    // Delete mood entry
    fun deleteMoodEntry(entryId: String) {
        val entries = getAllMoodEntries().filter { it.id != entryId }
        val json = gson.toJson(entries)
        prefs.edit().putString("mood_entries", json).apply()
    }
    
    // Get mood statistics
    fun getMoodStatistics(): Map<String, Int> {
        val entries = getAllMoodEntries()
        val stats = mutableMapOf<String, Int>()
        
        entries.forEach { entry ->
            stats[entry.mood] = stats.getOrDefault(entry.mood, 0) + 1
        }
        
        return stats
    }
    
    // Get current user ID
    private fun getCurrentUserId(): String {
        val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return userPrefs.getString("user_uid", "guest") ?: "guest"
    }
}

package com.example.spandana.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.spandana.models.Habit
import com.example.spandana.models.HabitEntry
import com.example.spandana.models.HabitProgress
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class HabitManager private constructor(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("habit_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    companion object {
        @Volatile
        private var INSTANCE: HabitManager? = null
        
        fun getInstance(context: Context): HabitManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HabitManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Habit Management
    fun saveHabit(habit: Habit) {
        val habits = getAllHabits().toMutableList()
        val existingIndex = habits.indexOfFirst { it.id == habit.id }
        
        if (existingIndex >= 0) {
            habits[existingIndex] = habit
        } else {
            habits.add(habit)
        }
        
        val json = gson.toJson(habits)
        prefs.edit().putString("habits", json).apply()
    }
    
    fun getAllHabits(): List<Habit> {
        val json = prefs.getString("habits", null)
        return if (json != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun getActiveHabits(): List<Habit> {
        return getAllHabits().filter { it.isActive }
    }
    
    fun deleteHabit(habitId: String) {
        val habits = getAllHabits().filter { it.id != habitId }
        val json = gson.toJson(habits)
        prefs.edit().putString("habits", json).apply()
        
        // Also delete all entries for this habit
        deleteHabitEntries(habitId)
    }
    
    // Habit Entry Management
    fun saveHabitEntry(entry: HabitEntry) {
        val today = dateFormat.format(Date())
        val entries = getHabitEntriesForDate(today).toMutableList()
        
        val existingIndex = entries.indexOfFirst { it.habitId == entry.habitId }
        if (existingIndex >= 0) {
            entries[existingIndex] = entry
        } else {
            entries.add(entry)
        }
        
        val json = gson.toJson(entries)
        prefs.edit().putString("entries_$today", json).apply()
    }
    
    fun getHabitEntriesForDate(date: String): List<HabitEntry> {
        val json = prefs.getString("entries_$date", null)
        return if (json != null) {
            val type = object : TypeToken<List<HabitEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun getTodayHabitEntries(): List<HabitEntry> {
        val today = dateFormat.format(Date())
        return getHabitEntriesForDate(today)
    }
    
    fun deleteHabitEntries(habitId: String) {
        val allDates = getAllEntryDates()
        allDates.forEach { date ->
            val entries = getHabitEntriesForDate(date).filter { it.habitId != habitId }
            val json = gson.toJson(entries)
            prefs.edit().putString("entries_$date", json).apply()
        }
    }
    
    private fun getAllEntryDates(): List<String> {
        val allKeys = prefs.all.keys
        return allKeys.filter { it.startsWith("entries_") }
            .map { it.removePrefix("entries_") }
    }
    
    // Progress Tracking
    fun getHabitProgressForDate(habitId: String, date: String): HabitProgress {
        val habit = getAllHabits().find { it.id == habitId }
        val entry = getHabitEntriesForDate(date).find { it.habitId == habitId }
        
        val targetValue = habit?.targetValue ?: 1
        val completedValue = entry?.completedValue ?: 0
        val isCompleted = completedValue >= targetValue
        val completionPercentage = if (targetValue == 0) 0 else (completedValue * 100) / targetValue
        
        return HabitProgress(
            habitId = habitId,
            date = date,
            targetValue = targetValue,
            completedValue = completedValue,
            isCompleted = isCompleted,
            completionPercentage = completionPercentage
        )
    }
    
    fun getTodayProgress(): List<HabitProgress> {
        val today = dateFormat.format(Date())
        val activeHabits = getActiveHabits()
        
        return activeHabits.map { habit ->
            getHabitProgressForDate(habit.id, today)
        }
    }
    
    fun getWeeklyProgress(): Map<String, List<HabitProgress>> {
        val calendar = Calendar.getInstance()
        val weeklyProgress = mutableMapOf<String, List<HabitProgress>>()
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            
            val activeHabits = getActiveHabits()
            val dayProgress = activeHabits.map { habit ->
                getHabitProgressForDate(habit.id, date)
            }
            
            weeklyProgress[date] = dayProgress
        }
        
        return weeklyProgress
    }
    
    // Statistics
    fun getCompletionRateForDate(date: String): Int {
        val activeHabits = getActiveHabits()
        if (activeHabits.isEmpty()) return 0
        
        val completedHabits = activeHabits.count { habit ->
            val progress = getHabitProgressForDate(habit.id, date)
            progress.isCompleted
        }
        
        return (completedHabits * 100) / activeHabits.size
    }
    
    fun getTodayCompletionRate(): Int {
        val today = dateFormat.format(Date())
        return getCompletionRateForDate(today)
    }
    
    fun getStreakDays(habitId: String): Int {
        val calendar = Calendar.getInstance()
        var streak = 0
        
        while (true) {
            val date = dateFormat.format(calendar.time)
            val progress = getHabitProgressForDate(habitId, date)
            
            if (progress.isCompleted) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        
        return streak
    }
    
    // Get current user ID
    private fun getCurrentUserId(): String {
        val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return userPrefs.getString("user_uid", "guest") ?: "guest"
    }
}

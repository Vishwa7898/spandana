package com.example.spandana.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.spandana.models.HydrationEntry
import com.example.spandana.models.HydrationGoal
import com.example.spandana.models.HydrationStats
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class HydrationManager private constructor(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("hydration_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    companion object {
        @Volatile
        private var INSTANCE: HydrationManager? = null
        
        fun getInstance(context: Context): HydrationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HydrationManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Save hydration entry
    fun saveHydrationEntry(entry: HydrationEntry) {
        val today = dateFormat.format(Date())
        val stats = getTodayStats()
        
        val updatedEntries = stats.entries.toMutableList()
        updatedEntries.add(entry)
        
        val updatedStats = stats.copy(
            totalIntake = stats.totalIntake + entry.amount,
            entries = updatedEntries
        )
        
        saveTodayStats(updatedStats)
    }
    
    // Get today's hydration stats
    fun getTodayStats(): HydrationStats {
        val today = dateFormat.format(Date())
        val userId = getCurrentUserId()
        
        val json = prefs.getString("stats_$today", null)
        return if (json != null) {
            gson.fromJson(json, HydrationStats::class.java)
        } else {
            val goal = getUserGoal()
            HydrationStats(
                userId = userId,
                date = today,
                goal = goal.dailyGoal
            )
        }
    }
    
    // Save today's stats
    private fun saveTodayStats(stats: HydrationStats) {
        val json = gson.toJson(stats)
        prefs.edit().putString("stats_${stats.date}", json).apply()
    }
    
    // Get user's hydration goal
    fun getUserGoal(): HydrationGoal {
        val userId = getCurrentUserId()
        val json = prefs.getString("goal_$userId", null)
        
        return if (json != null) {
            gson.fromJson(json, HydrationGoal::class.java)
        } else {
            // Create default goal based on user profile
            val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val weight = userPrefs.getFloat("user_weight", 70f)
            val activityLevel = userPrefs.getString("user_activity_level", "moderate") ?: "moderate"
            val age = userPrefs.getInt("user_age", 30)
            
            val goal = HydrationGoal(
                userId = userId,
                weight = weight,
                activityLevel = activityLevel
            )
            
            val personalizedGoal = goal.copy(dailyGoal = calculatePersonalizedGoal(weight, age, activityLevel))
            saveUserGoal(personalizedGoal)
            personalizedGoal
        }
    }
    
    // Calculate personalized hydration goal based on user profile
    private fun calculatePersonalizedGoal(weight: Float, age: Int, activityLevel: String): Int {
        // Base calculation: 35ml per kg of body weight
        var baseAmount = (weight * 35).toInt()
        
        // Adjust for age
        when {
            age < 18 -> baseAmount = (baseAmount * 0.9).toInt() // Teens need less
            age > 65 -> baseAmount = (baseAmount * 0.8).toInt() // Elderly need less
        }
        
        // Adjust for activity level
        baseAmount = when (activityLevel.lowercase()) {
            "low" -> (baseAmount * 0.9).toInt()
            "high", "very_high" -> (baseAmount * 1.3).toInt()
            "extreme" -> (baseAmount * 1.5).toInt()
            else -> baseAmount // moderate
        }
        
        // Ensure minimum and maximum bounds
        return baseAmount.coerceIn(1000, 4000) // Between 1L and 4L
    }
    
    // Save user's hydration goal
    fun saveUserGoal(goal: HydrationGoal) {
        val json = gson.toJson(goal)
        prefs.edit().putString("goal_${goal.userId}", json).apply()
    }
    
    // Get weekly stats
    fun getWeeklyStats(): List<HydrationStats> {
        val stats = mutableListOf<HydrationStats>()
        val calendar = Calendar.getInstance()
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            
            val json = prefs.getString("stats_$date", null)
            if (json != null) {
                stats.add(gson.fromJson(json, HydrationStats::class.java))
            } else {
                val goal = getUserGoal()
                stats.add(HydrationStats(
                    userId = getCurrentUserId(),
                    date = date,
                    goal = goal.dailyGoal
                ))
            }
        }
        
        return stats
    }
    
    // Get monthly stats
    fun getMonthlyStats(): List<HydrationStats> {
        val stats = mutableListOf<HydrationStats>()
        val calendar = Calendar.getInstance()
        
        for (i in 29 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            
            val json = prefs.getString("stats_$date", null)
            if (json != null) {
                stats.add(gson.fromJson(json, HydrationStats::class.java))
            } else {
                val goal = getUserGoal()
                stats.add(HydrationStats(
                    userId = getCurrentUserId(),
                    date = date,
                    goal = goal.dailyGoal
                ))
            }
        }
        
        return stats
    }
    
    // Update reminder settings
    fun updateReminderSettings(enabled: Boolean, intervalMinutes: Int) {
        prefs.edit().apply {
            putBoolean("reminder_enabled", enabled)
            putInt("reminder_interval", intervalMinutes)
            apply()
        }
    }
    
    // Get reminder settings
    fun getReminderSettings(): Pair<Boolean, Int> {
        val enabled = prefs.getBoolean("reminder_enabled", true)
        val interval = prefs.getInt("reminder_interval", 1) // Changed from 60 to 1 minute for testing
        return Pair(enabled, interval)
    }
    
    // Update last reminder time
    fun updateLastReminderTime() {
        prefs.edit().putLong("last_reminder_time", System.currentTimeMillis()).apply()
    }
    
    // Get last reminder time
    fun getLastReminderTime(): Long {
        return prefs.getLong("last_reminder_time", 0L)
    }
    
    // Check if reminder should be shown
    fun shouldShowReminder(): Boolean {
        val (enabled, intervalMinutes) = getReminderSettings()
        if (!enabled) return false
        
        val lastReminder = getLastReminderTime()
        val now = System.currentTimeMillis()
        val intervalMillis = intervalMinutes * 60 * 1000L
        
        return (now - lastReminder) >= intervalMillis
    }
    
    // Get current user ID
    private fun getCurrentUserId(): String {
        val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return userPrefs.getString("user_uid", "guest") ?: "guest"
    }
    
    // Delete hydration entry
    fun deleteHydrationEntry(entryId: String) {
        val today = dateFormat.format(Date())
        val stats = getTodayStats()
        
        val entryToDelete = stats.entries.find { it.id == entryId }
        if (entryToDelete != null) {
            val updatedEntries = stats.entries.filter { it.id != entryId }
            val updatedStats = stats.copy(
                totalIntake = stats.totalIntake - entryToDelete.amount,
                entries = updatedEntries
            )
            
            saveTodayStats(updatedStats)
        }
    }
    
    // Get common container sizes
    fun getCommonContainerSizes(): List<Pair<String, Int>> {
        return listOf(
            "Small Glass" to 150,
            "Medium Glass" to 200,
            "Large Glass" to 250,
            "Small Bottle" to 300,
            "Medium Bottle" to 500,
            "Large Bottle" to 750,
            "Water Bottle" to 1000,
            "Custom" to 0
        )
    }
}

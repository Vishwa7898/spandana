package com.example.spandana.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class HydrationEntry(
    val id: String = "",
    val userId: String = "",
    val amount: Int = 0, // Amount in ml
    val timestamp: Long = System.currentTimeMillis(),
    val containerType: String = "glass", // glass, bottle, cup, etc.
    val notes: String = ""
) : Parcelable {
    
    fun getFormattedAmount(): String {
        return when {
            amount >= 1000 -> "${amount / 1000}L"
            else -> "${amount}ml"
        }
    }
    
    fun getFormattedTime(): String {
        val date = Date(timestamp)
        val hours = date.hours
        val minutes = date.minutes
        return String.format("%02d:%02d", hours, minutes)
    }
}

@Parcelize
data class HydrationGoal(
    val userId: String = "",
    val dailyGoal: Int = 2000, // Default 2L per day
    val weight: Float = 70f, // kg
    val activityLevel: String = "moderate", // low, moderate, high
    val lastUpdated: Long = System.currentTimeMillis()
) : Parcelable {
    
    fun calculatePersonalizedGoal(): Int {
        // Basic calculation: 35ml per kg of body weight
        // Adjust based on activity level
        val baseAmount = (weight * 35).toInt()
        return when (activityLevel.lowercase()) {
            "low" -> (baseAmount * 0.9).toInt()
            "high" -> (baseAmount * 1.2).toInt()
            else -> baseAmount // moderate
        }
    }
    
    fun getFormattedGoal(): String {
        return when {
            dailyGoal >= 1000 -> "${dailyGoal / 1000}L"
            else -> "${dailyGoal}ml"
        }
    }
}

@Parcelize
data class HydrationStats(
    val userId: String = "",
    val date: String = "", // YYYY-MM-DD format
    val totalIntake: Int = 0,
    val goal: Int = 2000,
    val entries: List<HydrationEntry> = emptyList(),
    val reminderCount: Int = 0,
    val lastReminderTime: Long = 0L
) : Parcelable {
    
    fun getProgressPercentage(): Int {
        return if (goal == 0) 0 else (totalIntake * 100) / goal
    }
    
    fun isGoalReached(): Boolean {
        return totalIntake >= goal
    }
    
    fun getFormattedTotal(): String {
        return when {
            totalIntake >= 1000 -> "${totalIntake / 1000}L"
            else -> "${totalIntake}ml"
        }
    }
    
    fun getFormattedGoal(): String {
        return when {
            goal >= 1000 -> "${goal / 1000}L"
            else -> "${goal}ml"
        }
    }
}

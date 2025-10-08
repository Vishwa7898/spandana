package com.example.spandana.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val targetValue: Int = 1,
    val unit: String = "times", // times, minutes, glasses, etc.
    val color: String = "#00FF94",
    val icon: String = "📝",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class HabitEntry(
    val id: String = "",
    val habitId: String = "",
    val userId: String = "",
    val completedValue: Int = 1,
    val date: String = "", // YYYY-MM-DD format
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
) : Parcelable {
    
    fun isCompleted(): Boolean {
        return completedValue > 0
    }
    
    fun getFormattedValue(): String {
        return "$completedValue"
    }
}

@Parcelize
data class HabitProgress(
    val habitId: String = "",
    val date: String = "",
    val targetValue: Int = 1,
    val completedValue: Int = 0,
    val isCompleted: Boolean = false,
    val completionPercentage: Int = 0
) : Parcelable {
    
    fun calculatePercentage(): Int {
        return if (targetValue == 0) 0 else (completedValue * 100) / targetValue
    }
    
    fun getProgressText(): String {
        return "$completedValue / $targetValue"
    }
}
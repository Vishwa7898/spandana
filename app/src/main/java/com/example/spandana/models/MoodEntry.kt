package com.example.spandana.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MoodEntry(
    val id: String = "",
    val userId: String = "",
    val mood: String = "Neutral",
    val emoji: String = "😐",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = "" // YYYY-MM-DD format
) : Parcelable {
    
    fun getFormattedTime(): String {
        val date = Date(timestamp)
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return timeFormat.format(date)
    }
    
    fun getFormattedDate(): String {
        val date = Date(timestamp)
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return dateFormat.format(date)
    }
    
    fun getMoodValue(): Int {
        return when (mood.lowercase()) {
            "happy" -> 5
            "good" -> 4
            "neutral" -> 3
            "sad" -> 2
            "angry" -> 1
            else -> 3
        }
    }
}

@Parcelize
data class MoodOption(
    val name: String = "",
    val emoji: String = "",
    val color: String = "#FFD700"
) : Parcelable

object MoodOptions {
    val options = listOf(
        MoodOption("Happy", "😊", "#FFD700"),
        MoodOption("Good", "😌", "#32CD32"),
        MoodOption("Neutral", "😐", "#FFA500"),
        MoodOption("Sad", "😢", "#4169E1"),
        MoodOption("Angry", "😠", "#FF4500"),
        MoodOption("Excited", "🤩", "#FF69B4"),
        MoodOption("Tired", "😴", "#808080"),
        MoodOption("Stressed", "😰", "#8B0000")
    )
}
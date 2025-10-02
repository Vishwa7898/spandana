package com.example.spandana.models

data class Habit(
    val name: String,
    val target: Int,
    var current: Int,
    val unit: String
) {
    fun calculatePercentage(): Int {
        return if (target == 0) 0 else (current * 100) / target
    }

    fun isCompleted(): Boolean {
        return current >= target
    }
}
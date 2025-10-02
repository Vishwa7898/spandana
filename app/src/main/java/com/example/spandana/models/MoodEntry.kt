package com.example.spandana.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoodEntry(
    val mood: String,
    val time: String,
    val date: String
) : Parcelable
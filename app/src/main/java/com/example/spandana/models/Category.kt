package com.example.spandana.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val name: String,
    val iconRes: Int,
    val color: String
) : Parcelable
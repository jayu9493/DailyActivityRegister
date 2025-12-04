package com.example.dailyactivityregister

import androidx.room.Entity
import java.io.Serializable

// If you want DailyLog to be a separate entity
data class DailyLog(
    val date: String,
    val progress: Map<String, Double> = mapOf()
) : Serializable
package com.example.dailyactivityregister

import java.io.Serializable

// Represents a single day's work entry
data class DailyLog(
    val date: String,
    // A map where the key is the task name (e.g., "Excavation")
    // and the value is the progress made on that specific day.
    val progress: Map<String, Double>
) : Serializable

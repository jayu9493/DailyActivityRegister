package com.example.dailyactivityregister

import java.io.Serializable

// Matches the Pydantic 'Task' model in main.py
data class ProjectTask(
    val name: String,
    val target: Double,
    var current: Double = 0.0,
    val unit: String,
    // We don't need the 'extensions' field in the Android app for now
) : Serializable

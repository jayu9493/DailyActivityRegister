package com.example.dailyactivityregister

import java.io.Serializable

data class ProjectTask(
    val name: String,
    val target: Double,
    var current: Double = 0.0,
    val unit: String // e.g., "km", "Nos.", "%"
) : Serializable

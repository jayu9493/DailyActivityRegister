package com.example.dailyactivityregister

import java.io.Serializable

data class ActivityEntry(
    val projectName: String,
    val date: String,
    val progressDetails: String,
    val manpower: String,
    val bottlenecks: String,
    val planForNextDay: String
) : Serializable

package com.example.dailyactivityregister

import java.io.Serializable

data class Project(
    val name: String, 
    val agency: String, 
    val activities: MutableList<ActivityEntry> = mutableListOf()
) : Serializable

package com.example.dailyactivityregister.network

import java.io.Serializable

// This class now correctly implements Serializable
data class ProjectCreateRequest(
    val project_name: String,
    val project_number: String?,
    val total_route_oh: Double,
    val total_route_ug: Double,
    val line_passing_villages: String?
) : Serializable

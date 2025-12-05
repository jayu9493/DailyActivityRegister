package com.example.dailyactivityregister.network

import java.io.Serializable

data class ProjectCreateRequest(
    val project_name: String,
    val project_number: String?,
    val total_route_oh: Double,
    val total_route_ug: Double,
    val line_passing_villages: String?,
    val subdivision: String?,  // NEW: Subdivision
    val agencies: List<AgencyRequest>?  // NEW: Agencies
) : Serializable

data class AgencyRequest(
    val name: String,
    val type_of_work: String?,
    val location_wise: String?,
    val po_number: String?,
    val suborder_no: String?
) : Serializable

package com.example.dailyactivityregister

import java.io.Serializable

// Matches the Pydantic 'Agency' model in main.py
data class Agency(
    val name: String,
    val type_of_work: String?,
    val location_wise: String?,
    val po_number: String?,
    val suborder_no: String?
) : Serializable

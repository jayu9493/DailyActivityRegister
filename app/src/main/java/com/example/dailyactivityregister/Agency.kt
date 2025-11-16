package com.example.dailyactivityregister

import java.io.Serializable

data class Agency(
    val name: String,
    val work_type: String,
    val location: String,
    val po_number: String
) : Serializable

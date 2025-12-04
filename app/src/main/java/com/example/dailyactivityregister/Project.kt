package com.example.dailyactivityregister

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val project_name: String,
    val project_number: String?,
    val requisition_number: String?,
    val suborder_number: String?,
    val commencement_date: String?,
    val ug_line_length: Double,
    val oh_line_length: Double,
    val line_passing_villages: String?,
    val subdivision: String?,  // NEW: For filtering by sub-division
    val agencies: List<Agency> = listOf(),
    val tasks: List<ProjectTask> = listOf(),
    val daily_logs: List<DailyLog> = listOf()
) : Serializable
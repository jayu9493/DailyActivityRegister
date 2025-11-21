package com.example.dailyactivityregister

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
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
    @TypeConverters(AgencyConverter::class)
    val agencies: MutableList<Agency> = mutableListOf(),
    @TypeConverters(ProjectTaskConverter::class)
    val tasks: MutableList<ProjectTask> = mutableListOf()
) : Serializable

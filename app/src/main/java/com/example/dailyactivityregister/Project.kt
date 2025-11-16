package com.example.dailyactivityregister

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val project_name: String,
    val project_number: String,
    val requisition_number: String,
    val commencement_date: String,
    val total_route_oh: Double,
    val total_route_ug: Double,
    val line_passing_villages: String,
    @TypeConverters(AgencyConverter::class)
    val agencies: MutableList<Agency> = mutableListOf(),
    @TypeConverters(ProjectTaskConverter::class)
    val tasks: MutableList<ProjectTask> = mutableListOf()
) : Serializable

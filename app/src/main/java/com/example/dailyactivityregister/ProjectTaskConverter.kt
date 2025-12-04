package com.example.dailyactivityregister

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProjectTaskConverter {
    private val gson = Gson()
    private val listType = object : TypeToken<List<ProjectTask>>() {}.type

    @TypeConverter
    fun stringToTaskList(value: String?): List<ProjectTask> {
        if (value.isNullOrBlank()) return emptyList()
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun taskListToString(list: List<ProjectTask>?): String {
        return gson.toJson(list ?: emptyList<ProjectTask>())
    }
}
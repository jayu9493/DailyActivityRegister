package com.example.dailyactivityregister

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProjectTaskConverter {
    @TypeConverter
    fun fromString(value: String): MutableList<ProjectTask> {
        val listType = object : TypeToken<MutableList<ProjectTask>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<ProjectTask>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
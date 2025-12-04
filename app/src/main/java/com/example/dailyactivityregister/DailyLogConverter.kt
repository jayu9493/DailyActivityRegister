package com.example.dailyactivityregister

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class DailyLogConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromDailyLogs(dailyLogs: List<DailyLog>): String {
        return gson.toJson(dailyLogs)
    }

    @TypeConverter
    fun toDailyLogs(dailyLogsString: String): List<DailyLog> {
        if (dailyLogsString.isBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<DailyLog>>() {}.type
            gson.fromJson(dailyLogsString, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Additional converters for the Map<String, Double> inside DailyLog
    @TypeConverter
    fun fromProgressMap(progress: Map<String, Double>): String {
        return gson.toJson(progress)
    }

    @TypeConverter
    fun toProgressMap(progressString: String): Map<String, Double> {
        if (progressString.isBlank()) return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Double>>() {}.type
            gson.fromJson(progressString, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
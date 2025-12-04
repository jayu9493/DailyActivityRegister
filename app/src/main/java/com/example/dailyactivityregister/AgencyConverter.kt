package com.example.dailyactivityregister

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AgencyConverter {
    private val gson = Gson()
    private val listType = object : TypeToken<List<Agency>>() {}.type

    @TypeConverter
    fun stringToAgencyList(value: String?): List<Agency> {
        if (value.isNullOrBlank()) return emptyList()
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun agencyListToString(list: List<Agency>?): String {
        return gson.toJson(list ?: emptyList<Agency>())
    }
}
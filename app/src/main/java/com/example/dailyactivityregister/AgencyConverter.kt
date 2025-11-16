package com.example.dailyactivityregister

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AgencyConverter {
    @TypeConverter
    fun fromString(value: String): MutableList<Agency> {
        val listType = object : TypeToken<MutableList<Agency>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<Agency>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
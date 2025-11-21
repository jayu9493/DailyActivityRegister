package com.example.dailyactivityregister

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DailyLogConverter {
    @TypeConverter
    fun fromString(value: String): MutableList<DailyLog> {
        val listType = object : TypeToken<MutableList<DailyLog>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<DailyLog>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
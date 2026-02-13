package com.example.handicapcalculator.database

import androidx.room.TypeConverter
import com.example.handicapcalculator.classes.Game
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Class for converting between a mutable list of games and a JSON string
class Converters {
    @TypeConverter
    fun fromMutableListToJson(list: MutableList<Game>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToMutableList(json: String?): MutableList<Game>? {
        val listType = object : TypeToken<MutableList<Game>>() {}.type
        return Gson().fromJson(json, listType)
    }
}
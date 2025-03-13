// app/src/main/java/com/example/whiskeytastingnote/data/database/Converters.kt
package com.example.whiskeytastingnote.data.database

import androidx.room.TypeConverter
import com.example.whiskeytastingnote.data.model.Aroma
import com.example.whiskeytastingnote.data.model.WhiskeyCharacter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Type converters for Room database
 */
class Converters {
    private val gson = Gson()

    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // List of Aroma converters
    @TypeConverter
    fun fromAromasList(value: List<Aroma>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAromasList(value: String): List<Aroma> {
        val listType = object : TypeToken<List<Aroma>>() {}.type
        return gson.fromJson(value, listType)
    }

    // List of WhiskeyCharacter converters
    @TypeConverter
    fun fromCharactersList(value: List<WhiskeyCharacter>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCharactersList(value: String): List<WhiskeyCharacter> {
        val listType = object : TypeToken<List<WhiskeyCharacter>>() {}.type
        return gson.fromJson(value, listType)
    }
}
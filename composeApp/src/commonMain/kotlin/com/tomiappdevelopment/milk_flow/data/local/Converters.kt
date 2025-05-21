package com.tomiappdevelopment.milk_flow.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.joinToString(separator = ",")  // simple CSV string
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.split(",")?.mapNotNull { it.toIntOrNull() }
    }
}

package com.soumik.weatherzone.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromDouble(value: Double?): String? {
        return if (value == null) null else String.format("%.2f", value)
    }

    @TypeConverter
    fun stringToDouble(value: String?): Double? {
        return value?.toDoubleOrNull()
    }
}

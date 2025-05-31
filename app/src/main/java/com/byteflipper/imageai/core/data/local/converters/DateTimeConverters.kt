package com.byteflipper.imageai.core.data.local.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class DateTimeConverters {
    
    @TypeConverter
    fun fromInstant(instant: Instant): String {
        return instant.toString()
    }
    
    @TypeConverter
    fun toInstant(timestamp: String): Instant {
        return Instant.parse(timestamp)
    }
} 
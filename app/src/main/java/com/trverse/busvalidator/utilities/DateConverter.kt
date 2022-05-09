package com.trverse.busvalidator.utilities

import androidx.room.TypeConverter
import java.util.*

object DateConverter {

    @TypeConverter
    fun toDate(timeStamp: Long?): Date? {
        if (timeStamp == null) {
            return null
        }
        return Date(timeStamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        if (date == null)
            return null
        return date.time
    }
}
package com.trverse.busvalidator.database.logs

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trverse.busvalidator.utilities.DateConverter
import java.util.*

@Entity(tableName = "ActivityLogs")
data class ActivityLogs(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val ActivityLog: String,
    val Number: String,
    val DeviceCode: String,
    val DateTime: String,
    val Description: String,
    @TypeConverters(DateConverter::class) val createdAt: Date?=Date()
) {
}
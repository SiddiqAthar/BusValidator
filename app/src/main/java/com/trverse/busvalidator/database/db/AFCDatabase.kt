package com.trverse.busvalidator.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trverse.busvalidator.database.dao.AFCDao
import com.trverse.busvalidator.database.dbmodel.QRTravelTransaction
import com.trverse.busvalidator.database.cardTransactionModels.CardTravelTransaction
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.models.settings.DeviceDetails
import com.trverse.busvalidator.models.settings.StationInfo
import com.trverse.busvalidator.models.userModel.UserInfo
import com.trverse.busvalidator.utilities.DateConverter

@Database(
    entities = [
        UserInfo::class,
        StationInfo::class,
        DeviceDetails::class,
        QRTravelTransaction::class,
        CardTravelTransaction::class,
        ActivityLogs::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AFCDatabase : RoomDatabase() {
    abstract val afcDatabaseDao: AFCDao

    companion object {
        @Volatile
        private var INSTANCE: AFCDatabase? = null

        fun getDatabase(context: Context): AFCDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AFCDatabase::class.java,
                    databaseName
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        const val databaseName = "bus_validator"
    }

}
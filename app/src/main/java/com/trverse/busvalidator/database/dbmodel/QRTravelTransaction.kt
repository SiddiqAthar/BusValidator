package com.trverse.busvalidator.database.dbmodel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trverse.busvalidator.App
import com.trverse.busvalidator.utilities.SharePrefData

@Entity(
    tableName = "QRTravelTransaction"
)
data class QRTravelTransaction(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val DeviceCode: String = SharePrefData(App.INSTANCE!!.applicationContext)
        .retrieveSyncObject()?.Validator_Code!!,
    val QRNumber: String,
    val TransactionDateTime: String,
    val QRJson: String,
    val LoggedInUser: String,
    val StationCode: String,
    val Lat: String,
    val Long: String,
    val LogType: String,
    var Sync: Boolean,
    val TransactionType: String,
    val FareAmount: String,
    val Type: String = "qr",
    val DirectionCode: String = SharePrefData(App.INSTANCE!!.applicationContext)
        .retrieveSyncObject()?.Validator_Direction ?: "N/A",
    val RouteCode: String = SharePrefData(App.INSTANCE!!.applicationContext)
        .retrieveSyncObject()?.Station_RouteID ?: "N/A",
    val BusNumber:String="BUS-0001",
    val DeviceType:String="validator"
)

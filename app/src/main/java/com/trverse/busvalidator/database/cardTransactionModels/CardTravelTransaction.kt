package com.trverse.busvalidator.database.cardTransactionModels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trverse.busvalidator.App
import com.trverse.busvalidator.utilities.SharePrefData

@Entity(
    tableName = "CardTravelTransaction"
)
data class CardTravelTransaction(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val DeviceCode: String,
    val TransactionType: String,
    val CardNumber: String,
    val TransactionDateTime: String,
    val StationCode: String,
    val LoggedInUser: String,
    val Lat: String,
    val Long: String,
    val Balance: String,
    var Sync: Boolean,
    // for checkout
    val FareAmount: String?,
    val FarePolicy: String?,
    val FarePolicyID: String?,
    val FarePolicyVersion: String?,
    val BalanceAfter: String?,
    val cardType: String?="",
    val cardDiscountPercent: String?="",
    val DeviceType:String="validator",
    val DirectionCode:String=SharePrefData(App.INSTANCE!!.applicationContext)
        .retrieveSyncObject()?.Validator_Direction ?: "N/A",
    val RouteCode:String=SharePrefData(App.INSTANCE!!.applicationContext)
        .retrieveSyncObject()?.Station_RouteID ?: "N/A",
    val BusNumber:String="BUS-0001",


)
package com.trverse.busvalidator.database.dbmodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "QRVerificationTransaction",
//    foreignKeys = [ForeignKey(
//        entity = UserInfo::class,
//        parentColumns = arrayOf("UserID"),
//        childColumns = arrayOf("LoggedInUser"),
//        onDelete = ForeignKey.CASCADE
//    ), ForeignKey(
//        entity = StationInfo::class,
//        parentColumns = arrayOf("StationCode"),
//        childColumns = arrayOf("StationCode"),
//        onDelete = ForeignKey.CASCADE
//    )]
)
data class QRVerificationTransaction(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val QRNumber: String,
    val VerificationTime: String,
    val QRJson: String,
    val LoggedInUser: String,
    val StationCode: String,
    val Lat: String,
    val Long: String,
    val LogType: String,
    val Sync: Boolean,
    val status: String
)

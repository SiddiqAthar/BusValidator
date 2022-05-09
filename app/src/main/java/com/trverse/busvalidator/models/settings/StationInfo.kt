package com.trverse.busvalidator.models.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stationInfo")
data class StationInfo(

    @SerializedName("StationID")
    var StationID: String,
    @SerializedName("OrganizationID") var OrganizationID: String,
    @SerializedName("PersonInChargeID") var PersonInChargeID: String,
    @SerializedName("PreviousStationID") var PreviousStationID: String,
    @SerializedName("RouteID") var RouteID: String,
    @SerializedName("TotalEquipments") var TotalEquipments: Int,
    @SerializedName("Latitude") var Latitude: String,
    @SerializedName("Longitude") var Longitude: String,
    @SerializedName("Name") var Name: String,
    @SerializedName("Details") var Details: String,
    @PrimaryKey(autoGenerate = false)
    @SerializedName("StationCode") var StationCode: String,
    @SerializedName("SerialNumber") var SerialNumber: String,
    @SerializedName("MaxSaleLimit") var MaxSaleLimit: Double,
    @SerializedName("CreationDate") var CreationDate: String,
    @SerializedName("UpdatedDate") var UpdatedDate: String,
    @SerializedName("CreatedBy") var CreatedBy: String,
    @SerializedName("LastUpdatedBy") var LastUpdatedBy: String,
    @SerializedName("IsActive") var IsActive: Boolean,
)
package com.trverse.busvalidator.models.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "deviceInfo")
data class DeviceDetails(
    @SerializedName("Validator_ID") @PrimaryKey(autoGenerate = false) val Validator_ID: String,
    @SerializedName("Validator_StationID") val Validator_StationID: String,
    @SerializedName("Validator_Name") val Validator_Name: String,
    @SerializedName("Validator_Code") val Validator_Code: String,
    @SerializedName("Validator_Description") val Validator_Description: String,
    @SerializedName("Validator_IP") val Validator_IP: String,
    @SerializedName("Validator_Direction") val Validator_Direction: String,
    @SerializedName("Validator_SDKSupport") val Validator_SDKSupport: String,
    @SerializedName("Station_Latitude") val Station_Latitude: String,
    @SerializedName("Station_Longitude") val Station_Longitude: String,
    @SerializedName("Station_Name") val Station_Name: String,
    @SerializedName("Station_Details") val Station_Details: String,
    @SerializedName("Station_StationCode") val Station_StationCode: String,
    @SerializedName("Station_MaxSaleLimit") val Station_MaxSaleLimit: Int,
    @SerializedName("Organization_Name") val Organization_Name: String,
    @SerializedName("Organization_ZipCode") val Organization_ZipCode: Int,
    @SerializedName("Organization_Mobile") val Organization_Mobile: String,
    @SerializedName("Organization_Telephone") val Organization_Telephone: String,
    @SerializedName("Organization_Skype") val Organization_Skype: String,
    @SerializedName("Station_RouteID") val Station_RouteID: String,
    @SerializedName("Valiodator_IsLogging") val Valiodator_IsLogging:Boolean,
    @SerializedName("Organization_Fax") val Organization_Fax: String,
    @SerializedName("Organization_Address") val Organization_Address: String,
    @SerializedName("Organization_Remarks") val Organization_Remarks: String,
    @SerializedName("Organization_OrganizationID") val Organization_OrganizationID: String,
    @SerializedName("FeeMap_Version") val FeeMap_Version: String,
)


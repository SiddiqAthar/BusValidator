package com.trverse.busvalidator.models.qr

import com.google.gson.annotations.SerializedName

data class QRResponseModel(
    @SerializedName("singleJourneyTicketID") val ticketID: String,
    @SerializedName("posid") val posid: String,
    @SerializedName("qrNumber") val qrNumber: String,
    @SerializedName("stationCode") val stationCode: String,
    @SerializedName("organizationCode") val organizationCode: String,
    @SerializedName("encryptedTicketID") val encryptedTicketID: String,
    @SerializedName("fareAmount") val fareAmount: Int,
    @SerializedName("isScanned") val isScanned: Boolean,
    @SerializedName("isEnter") val isEnter: Boolean,
    @SerializedName("scanByAppID") val scanByAppID: Int,
    @SerializedName("remarks") val remarks: String,
    @SerializedName("creationDate") val creationDate: String,
    @SerializedName("expiryTime") val expiryTime: String,
    @SerializedName("scanTime") val scanTime: String,
    @SerializedName("updatedDate") val updatedDate: String,
    @SerializedName("createdBy") val createdBy: Int,
    @SerializedName("lastUpdatedBy") val lastUpdatedBy: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("fileAsBase64") val fileAsBase64: String,
    @SerializedName("ticketCount") val ticketCount: Int
)
package com.trverse.busvalidator.models.qr

import com.google.gson.annotations.SerializedName

data class DecryptedQR(
    @SerializedName("TID") val TID: String,
    @SerializedName("FA") val fairAmount: String,
    @SerializedName("CD") val startTime: String,
    @SerializedName("ET") val endTime: String,
    @SerializedName("SC") val stationCode: String,
    @SerializedName("OC") val organizationCode: String
)
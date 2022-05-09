package com.trverse.busvalidator.models.heartBeat

import com.google.gson.annotations.SerializedName

data class HeartBeatModel(
    @SerializedName("SOFTWAREVERSION") val softwareVersion: String,
    @SerializedName("DEVICECODE")val validatorNumber: String,
    @SerializedName("FAREPOLICYVERSION")val farePolicyVersion: String,
    @SerializedName("DEVICELOCALTIME")val currentTime: String,
    @SerializedName("FARETABLEVERSION")val fareTableVersion: String,
    @SerializedName("OPERATIONMODE")val operationMode: String,
    @SerializedName("STATIONCODE")val stationCode: String,
    @SerializedName("ORGANIZATIONCODE")val organizationCode: String,
    @SerializedName("IP")val ip: String,
    @SerializedName("DEVICETYPE")val deviceType: String,
    @SerializedName("MACPSAM")val MAC: String,
    @SerializedName("ROUTECODE")val routeCode:String,
    @SerializedName("BLOCKLISTVERSION")val blackListed:String,
    @SerializedName("BUSNUMBER")val busNumber:String,
) {
}
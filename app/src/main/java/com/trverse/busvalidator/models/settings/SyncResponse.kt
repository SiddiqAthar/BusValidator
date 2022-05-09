package com.trverse.busvalidator.models.settings

import com.google.gson.annotations.SerializedName
import com.trverse.busvalidator.models.settings.DeviceDetails

data class SyncResponse(
    @SerializedName("PosDetails") val deviceDetails: DeviceDetails,
)

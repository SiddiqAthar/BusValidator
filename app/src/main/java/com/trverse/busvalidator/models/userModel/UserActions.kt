package com.trverse.busvalidator.models.userModel

import com.google.gson.annotations.SerializedName

data class UserActions(
    @SerializedName("ActionName") val ActionName: String,
    @SerializedName("ActionCode") val ActionCode: String,
)

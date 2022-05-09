package com.trverse.busvalidator.models.userModel

import com.google.gson.annotations.SerializedName

data class LoginResponseModel(
    @SerializedName("userInfo") val userInfo: UserInfo,
    @SerializedName("userRoles") val userRoles: ArrayList<UserRoles>,
    @SerializedName("userActions") val userActions: ArrayList<UserActions>
)

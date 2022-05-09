package com.trverse.busvalidator.models.encryptionModels

import com.google.gson.annotations.SerializedName

data class EncryptionResponse (
    @SerializedName("keysStoreID") var keysStoreID: Int,
    @SerializedName("publicKey") var publicKey: String,
    @SerializedName("privateKey") var privateKey: String,
    @SerializedName("source") var source: String,
    @SerializedName("creationDate") var creationDate: String,
    @SerializedName("updateDate") var updateDate: String,
    @SerializedName("isActive") var isActive: String,
     )
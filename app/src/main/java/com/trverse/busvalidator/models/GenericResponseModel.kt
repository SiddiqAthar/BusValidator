package com.trverse.busvalidator.models

import com.google.gson.annotations.SerializedName

data class GenericResponseModel<T>(
    @SerializedName("Name", alternate = ["name"]) val name: String,
    @SerializedName("Heading", alternate = ["heading"]) val heading: String,
    @SerializedName("Summary", alternate = ["summary"]) val summary: String,
    @SerializedName("Message", alternate = ["message"]) val message: String,
    @SerializedName("ExceptionMessage", alternate = ["exceptionMessage"]) val exception: String,
    @SerializedName("Status", alternate = ["status"]) val status: Int,
    @SerializedName("IsSuccess", alternate = ["isSuccess"]) val success: Boolean,
    @SerializedName("ResponseObject", alternate = ["responseObject"]) val result: T,
)
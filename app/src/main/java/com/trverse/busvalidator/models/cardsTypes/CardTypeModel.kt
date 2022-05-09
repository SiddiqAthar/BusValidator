package com.trverse.busvalidator.models.cardsTypes

import com.google.gson.annotations.SerializedName

data class CardTypeModel(
    @SerializedName("CardTypeID") val cardTypeID: String,
    @SerializedName("OrganizationID") val organizationID: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Code") val code: String,
    @SerializedName("IsPersonlaized") val isPersonlaized: Boolean,
    @SerializedName("CardPrice") val cardPrice: Int,
    @SerializedName("MinimumTopup") val minimumTopup: Int,
    @SerializedName("MaximumTopup") val maximumTopup: Int,
    @SerializedName("MaximumBalance") val maximumBalance: Int,
    @SerializedName("CardValidity") val cardValidity: Int,
    @SerializedName("Discount") val discount: Int,
    @SerializedName("CreationDate") val creationDate: String,
    @SerializedName("UpdatedDate") val updatedDate: String,
    @SerializedName("CreatedBy") val createdBy: String,
    @SerializedName("LastUpdatedBy") val lastUpdatedBy: String,
    @SerializedName("IsActive") val isActive: Boolean,
) {
}
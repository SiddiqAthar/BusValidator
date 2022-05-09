package com.trverse.busvalidator.models.farePolicyModels

import com.google.gson.annotations.SerializedName

data class PolicyRulesResponseModel(
    @SerializedName("PolicyRuleID") val policyRuleID: String,
    @SerializedName("FarePolicyID") val farePolicyID: String,
    @SerializedName("FarePolicyTypeID") val FarePolicyTypeID: String,
    @SerializedName("PolicyRuleParentID") val PolicyRuleParentID: String,
    @SerializedName("Code") val Code: String,
    @SerializedName("TypeCode") val TypeCode: String,
    @SerializedName("Name") val Name: String,
    @SerializedName("TypeName") val TypeName: String,
    @SerializedName("Amount") val Amount: Double,
    @SerializedName("MaxDurationMinutes") val MaxDurationMinutes: Int,
    @SerializedName("MaxKilometer") val MaxKilometer: Double,
    @SerializedName("MinDurationMinutes") val MinDurationMinutes: Int,
    @SerializedName("MinKilometer") val MinKilometer: Double,
    @SerializedName("Version") val Version: String,
    @SerializedName("OrganizationID") val OrganizationID: String,
    @SerializedName("Details") val Details: String,
    @SerializedName("CreationDate") val CreationDate: String,
    @SerializedName("UpdatedDate") val UpdatedDate: String,
    @SerializedName("CreatedBy") val CreatedBy: String,
    @SerializedName("LastUpdatedBy") val LastUpdatedBy: String,
    @SerializedName("IsActive") val IsActive: Boolean,
    )
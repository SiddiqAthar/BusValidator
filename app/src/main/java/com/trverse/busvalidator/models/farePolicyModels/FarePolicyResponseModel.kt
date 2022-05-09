package com.trverse.busvalidator.models.farePolicyModels

import com.google.gson.annotations.SerializedName

data class FarePolicyResponseModel(
    @SerializedName("FarePolicyID") val farePolicyID: String,
    @SerializedName("FarePolicyTypeID") val farePolicyTypeID: String,
    @SerializedName("FarePolicyParentID") val farePolicyParentID: String,
    @SerializedName("Name") val name: String,
    @SerializedName("TypeName") val typeName: String,
    @SerializedName("Code") val code: String,
    @SerializedName("TypeCode") val typeCode: String,
    @SerializedName("OrganizationID") val organizationID: String,
    @SerializedName("Version") val version: String,
    @SerializedName("Details") val details: String,
    @SerializedName("CreationDate") val creationDate: String,
    @SerializedName("UpdatedDate") val updatedDate: String,
    @SerializedName("CreatedBy") val createdBy: String,
    @SerializedName("LastUpdatedBy") val lastUpdatedBy: String,
    @SerializedName("IsApplicable") val isApplicable: Boolean,
    @SerializedName("IsActive") val isActive: Boolean,
    @SerializedName("policyRules") val policyRules:ArrayList<PolicyRulesResponseModel>,

    )
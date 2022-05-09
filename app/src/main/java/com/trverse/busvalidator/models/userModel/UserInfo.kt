package com.trverse.busvalidator.models.userModel

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "userInfo")
data class UserInfo @JvmOverloads constructor(
    @SerializedName("UserID")
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    @SerializedName("OrganizationID") val OrganizationID: String?,
    @SerializedName("DepartmentID") val DepartmentID: String?,
    @SerializedName("DesignationID") val DesignationID: String?,
    @SerializedName("TeamID") val TeamID: String?,
    @SerializedName("UserTypeID") val UserTypeID: String?,
    @SerializedName("Name") val Name: String?,
    @SerializedName("OrganizationName") val OrganizationName: String?,
    @SerializedName("DepartmentName") val DepartmentName: String?,
    @SerializedName("TeamName") val TeamName: String?,
    @SerializedName("CNIC") val CNIC: String?,
    @SerializedName("Email") val Email: String?,
    @SerializedName("Password") val Password: String?,
    @SerializedName("NewPassword") val NewPassword: String?,
    @SerializedName("UserTypeName") val UserTypeName: String?,
    @SerializedName("GenderID") val GenderID: String?,
    @SerializedName("GenderName") val GenderName: String?,
    @SerializedName("Mobile") val Mobile: String?,
    @SerializedName("DateOfBirth") val DateOfBirth: String?,
    @SerializedName("IsAllowLogin") val IsAllowLogin: Boolean,

    @SerializedName("Remarks") val Remarks: String?,
    @SerializedName("CreationDate") val CreationDate: String?,
    @SerializedName("UpdatedDate") val UpdatedDate: String?,
    @SerializedName("CreatedBy") val CreatedBy: String?,
    @SerializedName("IsActive") val IsActive: Boolean?,
) {
     @SerializedName("AssignRoles")
     @Ignore
     val AssignRoles: ArrayList<String?>? = null
}

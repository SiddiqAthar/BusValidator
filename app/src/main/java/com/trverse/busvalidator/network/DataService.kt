package com.trverse.busvalidator.network

import com.trverse.busvalidator.models.GenericResponseModel
import com.trverse.busvalidator.models.cardsTypes.CardTypeModel
import com.trverse.busvalidator.models.encryptionModels.EncryptionResponse
import com.trverse.busvalidator.models.farePolicyModels.FarePolicyResponseModel
import com.trverse.busvalidator.models.qr.QRResponseModel
import com.trverse.busvalidator.models.settings.StationInfo
import com.trverse.busvalidator.models.settings.SyncResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DataService {
    @GET("Application/OrganizationStation")
    fun getAllStations(@Query("organizationID") organizationId: String):
            Call<GenericResponseModel<ArrayList<StationInfo>>>

    @GET
    fun getSyncSettings(@Url url: String, @Query("macAddress") macAddress: String):
            Call<GenericResponseModel<SyncResponse>>

    @GET("Ticket/GetQR")
    fun getQR(
        @Query("body") requestBody: String,
        @Query("appid") appID: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("currentUserID") currentUserID: String
    ): Call<GenericResponseModel<QRResponseModel>>

    @GET("Ticket/RegenerateQR")
    fun regenerateQR(
        @Query("qRNumber") requestBody: String,
        @Query("appID") appID: String,
        @Query("userName") username: String,
        @Query("password") password: String,
        @Query("currentUserID") currentUserID: String
    ): Call<GenericResponseModel<QRResponseModel>>

    @POST("DataCollection")
    fun syncQRTransaction(
        @Query("RecordType") recordType: String,
        @Body data: Any, @Header("Content-Type") contentType: String

    ): Call<ResponseBody>

    @GET
    fun getRSAKey(
        @Url url: String,
        @Query("appid") appID: String,
        @Query("userName") username: String,
        @Query("password") password: String,
        @Query("signature") signature: String,
    ):
            Call<GenericResponseModel<EncryptionResponse>>

    @GET()
    fun getFarePolicy(@Url url: String, @Query("organizationID") organizationId: String):
            Call<GenericResponseModel<ArrayList<FarePolicyResponseModel>>>

    @GET
    fun getCardsTypes(@Url url: String, @Query("OrganizationID") organizationId: String):
            Call<GenericResponseModel<ArrayList<CardTypeModel>>>

    @GET
    fun checkQRUsage(@Url url:String,@Query("QRNumber") qrNumber:String,@Query("Status") status:String)
    :Call<GenericResponseModel<Any>>


}
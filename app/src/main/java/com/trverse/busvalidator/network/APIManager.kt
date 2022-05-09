package com.trverse.busvalidator.network;

import android.app.Dialog
import com.trverse.busvalidator.App
import com.trverse.busvalidator.dialogs.BussVadatorDialog
import com.trverse.busvalidator.enumirations.Errors
import com.trverse.busvalidator.models.GenericResponseModel
import com.trverse.busvalidator.models.cardsTypes.CardTypeModel
import com.trverse.busvalidator.models.encryptionModels.EncryptionResponse
import com.trverse.busvalidator.models.farePolicyModels.FarePolicyResponseModel
import com.trverse.busvalidator.models.settings.SyncResponse
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class APIManager() {
    var retrofit: Retrofit? = null

    init {
        retrofit = ClientInstance.create()
    }

    fun syncTransactionData(
        syncType: String,
        body: Any,
        callback: CallbackGeneric<ResponseBody>
    ) {
        val service: DataService? = retrofit?.create(DataService::class.java)
        val result = service?.syncQRTransaction(syncType, body, "application/json")
        sendDataSync(result, callback)
    }

    fun <T> getSyncSettings(
        macAddress: String, callback:
        CallbackGeneric<T>, requestCode: Int
    ) {
        val service: DataService? = retrofit?.create(DataService::class.java)
        val result =
            service?.getSyncSettings(
                "${URLType.BASE_URL.baseURL.url}Application/ValidatorSync",
                macAddress
            )
        sendResultGeneric(
            result,
            callback as CallbackGeneric<GenericResponseModel<SyncResponse>>,
            requestCode
        )
    }

    fun <T> checkQRUsage(
        qrNumber: String, status: String, callback:
        CallbackGeneric<T>, requestCode: Int
    ) {
        val service: DataService? = retrofit?.create(DataService::class.java)
        val result =
            service?.checkQRUsage(
                "${URLType.QR_USAGE_URL.baseURL.url}QR/IsQRUsed",
                qrNumber, status
            )
        sendResultGeneric(
            result,
            callback as CallbackGeneric<GenericResponseModel<Any>>,
            requestCode
        )
    }

    fun <T> getRSAKey(
        appID: String,
        username: String,
        password: String,
        signature: String,
        callback: CallbackGeneric<T>, requestCode: Int
    ) {
        val service: DataService? = retrofit?.create(DataService::class.java)
        val result =
            service?.getRSAKey(

                "${URLType.QR_SERVICE.baseURL.url}RSA/GetPrivateKey",
                appID, username, password, signature
            )
        sendResultGeneric(
            result,
            callback as CallbackGeneric<GenericResponseModel<EncryptionResponse>>, requestCode
        )
    }

    fun <T> getFarePolicy(
        organizationID: String, callback:
        CallbackGeneric<T>, requestCode: Int

    ) {
        val service: DataService? = retrofit?.create(DataService::class.java)
        val result =
            service?.getFarePolicy(
                "${URLType.BASE_URL.baseURL.url}Application/GetFarePolicyWithRules",
                organizationID
            )
        sendResultGeneric(
            result,
            callback as CallbackGeneric<GenericResponseModel<ArrayList<FarePolicyResponseModel>>>,
            requestCode
        )
    }

    fun <T> getCardsTypes(
        organizationID: String, callback:
        CallbackGeneric<T>, requestCode: Int

    ) {
        val service: DataService? = retrofit?.create(DataService::class.java)
        val result =
            service?.getCardsTypes(
                "${URLType.BASE_URL.baseURL.url}Application/GetCardTypes",
                organizationID
            )
        sendResultGeneric(
            result,
            callback as CallbackGeneric<GenericResponseModel<ArrayList<CardTypeModel>>>,
            requestCode
        )
    }

    private fun <T> sendResultGeneric(
        call: Call<T>?,
        result: CallbackGeneric<T>, requestCode: Int
    ) {
        var dialog: BussVadatorDialog? = null
        if (Objects.requireNonNull(App.INSTANCE)
                ?.getBaseActivity() != null && Objects.requireNonNull(
                App.INSTANCE?.getBaseActivity()
            )?.isFinishing == false
        ) {
            dialog = App.INSTANCE?.getBaseActivity()?.let { BussVadatorDialog(it) }
        }
        if (dialog != null && !dialog.isShowing) {
            dialog.show()
        }
        val finalDialog: BussVadatorDialog? = dialog
        call?.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                if (finalDialog!=null && finalDialog.isShowing) finalDialog.dismiss()
                if (response.code() == 200 || response.code() == 201) {
                    val genericResponseModel = response.body()
                    result.onResult(genericResponseModel as T, requestCode)
                } else if (response.code() == 404) {
                    result.onError(
                        Errors.QR_SYNC_ERROR.errorEnum.desc.toString(),
                        response.code()
                    )
                } else {
                    result.onError(
                        Errors.SERVER_RESPONDING.errorEnum.desc.toString(),
                        response.code()
                    )

                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (finalDialog!=null && finalDialog.isShowing) finalDialog.dismiss()
                var error = Errors.SERVER_RESPONDING
                when (t) {
                    is java.net.UnknownHostException -> {
                        error.errorEnum.desc?.let {
                            result.onError(it, requestCode)
                        }
                    }
                    is java.net.ConnectException -> {
                        error = Errors.NO_INTERNET
                        error.errorEnum.desc?.let {
                            result.onError(it, requestCode)
                        }
                    }
                    is JSONException -> {
                        error = Errors.INVALID_RESPONSE
                        error.errorEnum.desc?.let {
                            result.onError(it, requestCode)
                        }
                    }
                    else -> {
                        result.onError(t.localizedMessage, requestCode)
                    }
                }
            }
        })
    }

    private fun <T> sendDataSync(
        call: Call<T>?,
        result: CallbackGeneric<T>
    ) {

        call?.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                if (response.code() == 200 || response.code() == 201) {
                    val genericResponseModel = response.body()
                    result.onResult(genericResponseModel as T, response.code())
                } else if (response.code() == 404) {
                    result.onError(Errors.QR_SYNC_ERROR.errorEnum.desc.toString(), response.code())
                } else {
                    result.onError(
                        Errors.SERVER_RESPONDING.errorEnum.desc.toString(),
                        response.code()
                    )

                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                var error = Errors.SERVER_RESPONDING
                when (t) {
                    is java.net.UnknownHostException -> {
                        error.errorEnum.desc?.let {
                            result.onError(it, error.errorEnum.Code)
                        }
                    }
                    is java.net.ConnectException -> {
                        error = Errors.NO_INTERNET
                        error.errorEnum.desc?.let {
                            result.onError(it, error.errorEnum.Code)
                        }
                    }
                    is JSONException -> {
                        error = Errors.INVALID_RESPONSE
                        error.errorEnum.desc?.let {
                            result.onError(it, error.errorEnum.Code)
                        }
                    }
                    else -> {
                        result.onError(t.localizedMessage, error.errorEnum.Code)
                    }
                }
            }
        })
    }


}
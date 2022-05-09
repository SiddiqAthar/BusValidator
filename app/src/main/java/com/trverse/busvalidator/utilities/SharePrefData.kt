package com.trverse.busvalidator.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trverse.busvalidator.models.cardsTypes.CardTypeModel
import com.trverse.busvalidator.models.farePolicyModels.FarePolicyResponseModel
import com.trverse.busvalidator.models.settings.DeviceDetails
import com.trverse.busvalidator.models.settings.StationInfo
import com.trverse.busvalidator.network.URLType


class SharePrefData(context: Context) {


    private var spEditor: SharedPreferences.Editor? = null
    private var sp: SharedPreferences? = null

    companion object {

        val LOGGED_IN = "logged_in"
        val SYNC_TIME = "sync_time"
        val SYNC_SETT = "sync_sett"
        val SYNC_STATION = "sync_Station"
        val FARE_POLICY = "fare_policy"

        //
        val SYNC_URL = "sync_url"
        val SYNC_ENCRService = "sync_encrService"
        val SYNC_IPService = "sync_ipService"
        val SYNC_IPPort = "sync_ipPort"
        val SYNC_QRService = "sync_QRService"
        val RSA_ENC_URL = "rsaEncryptionServiceUrl"
        val SYNC_CardService = "sync_CardService"
        val FARE_TABLE_DOWNLOAD = "fareTableDownloaded"
        val CARD_TYPES = "cardsTypes"
    }

    init {
        sp = PreferenceManager.getDefaultSharedPreferences(context)
    }

    @SuppressLint("CommitPrefEdits")
    fun saveString(key: String, value: String) {
        spEditor = sp?.edit()
        spEditor?.putString(key, value)
        spEditor?.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun saveBoolean(key: String, value: Boolean) {
        spEditor = sp?.edit()
        spEditor?.putBoolean(key, value)
        spEditor?.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun saveInt(key: String, value: Int) {
        spEditor = sp?.edit()
        spEditor?.putInt(key, value)
        spEditor?.apply()
    }

    fun saveSyncUrl(url: String) {
        spEditor = sp?.edit()
        spEditor?.putString(SYNC_QRService, url)
        spEditor?.apply()

    }

    fun getSyncUrl(): String? {
        return sp?.getString(SYNC_QRService, URLType.DATA_SYNC_SERVICE.baseURL.url)
    }

    fun fareTableDownloadedStatus(status: Boolean) {
        spEditor = sp?.edit()
        spEditor?.putBoolean(FARE_TABLE_DOWNLOAD, status)
        spEditor?.apply()

    }

    fun getFareTableDownloadedStatus(): Boolean? {
        return sp?.getBoolean(FARE_TABLE_DOWNLOAD, false)
    }

    fun setRSAUrl(url: String) {
        spEditor = sp?.edit()
        spEditor?.putString(RSA_ENC_URL, url)
        spEditor?.apply()
    }

    fun getRSAUrl(): String? {
        return sp?.getString(RSA_ENC_URL, URLType.BASE_URL.baseURL.url)
    }


    fun saveLong(key: String, value: Long) {
        spEditor = sp?.edit()
        spEditor?.putLong(key, value)
        spEditor?.apply()
    }

    fun getLongValue(key: String): Long? {
        return sp?.getLong(key, 0)
    }

    fun getIntValue(key: String): Int? {
        return sp?.getInt(key, -1)
    }

    fun getStringValue(key: String, baseValue: String? = ""): String? {
        return sp?.getString(key, baseValue)
    }

    fun getBooleanValue(key: String): Boolean? {
        return sp?.getBoolean(key, false)
    }

    fun saveSyncObject(objectVal: DeviceDetails) {
        spEditor = sp?.edit()
        val gson = Gson()
        val json = gson.toJson(objectVal)
        spEditor?.putString(SYNC_SETT, json)
        spEditor?.apply()
    }

    fun retrieveSyncObject(): DeviceDetails? {
        spEditor = sp?.edit()
        val gson = Gson()
        val json: String? = sp?.getString(SYNC_SETT, "")
        val obj: DeviceDetails? = gson.fromJson(json, DeviceDetails::class.java)
        return obj
    }

    fun saveStationObject(objectVal: StationInfo) {
        spEditor = sp?.edit()
        val gson = Gson()
        val json = gson.toJson(objectVal)
        spEditor?.putString(SYNC_SETT, json)
        spEditor?.apply()
    }

    fun retrieveStationObject(): StationInfo? {
        spEditor = sp?.edit()
        val gson = Gson()
        val json: String? = sp?.getString(SYNC_STATION, "")
        val obj: StationInfo? = gson.fromJson(json, StationInfo::class.java)
        return obj
    }

    fun saveFarePolicyObject(objectVal: FarePolicyResponseModel) {
        spEditor = sp?.edit()
        val gson = Gson()
        val json = gson.toJson(objectVal)
        spEditor?.putString(FARE_POLICY, json)
        spEditor?.apply()
    }

    fun retrieveFarePolicyObject(): FarePolicyResponseModel? {
        spEditor = sp?.edit()
        val gson = Gson()
        val json: String? = sp?.getString(FARE_POLICY, "")
        val obj: FarePolicyResponseModel? = gson.fromJson(json, FarePolicyResponseModel::class.java)
        return obj
    }

    fun saveCardTypes(objectVal: ArrayList<CardTypeModel>) {
        spEditor = sp?.edit()
        val gson = Gson()
        val json = gson.toJson(objectVal)
        spEditor?.putString(CARD_TYPES, json)
        spEditor?.apply()
    }

    fun retriveCardsTpes(): ArrayList<CardTypeModel>? {
        spEditor = sp?.edit()
        val gson = Gson()
        val json: String? = sp?.getString(CARD_TYPES, "")
        val obj: ArrayList<CardTypeModel>? = gson.fromJson(
            json,
            object : TypeToken<ArrayList<CardTypeModel?>?>() {}.type
        )
        return obj
    }
}
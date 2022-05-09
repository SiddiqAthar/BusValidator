package com.trverse.busvalidator.database.workers

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.trverse.busvalidator.BuildConfig
import com.trverse.busvalidator.models.heartBeat.HeartBeatModel
import com.trverse.busvalidator.network.APIManager
import com.trverse.busvalidator.network.CallbackGeneric
import com.trverse.busvalidator.utilities.SharePrefData
import com.trverse.busvalidator.utilities.Utils
import com.trverse.busvalidator.utilities.heartBeat
import okhttp3.ResponseBody
import java.util.*
import java.util.concurrent.TimeUnit

class HeartbeatWorker(var ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {

        Log.d("heartBeat", "doWork: running")
        val apiManager = APIManager()
        val heartBeatModel = HeartBeatModel(
            BuildConfig.VERSION_NAME,
            SharePrefData(ctx).retrieveSyncObject()?.Validator_Code.toString(),
            SharePrefData(ctx).retrieveFarePolicyObject()?.version ?: "",
            Utils.currentDateTime(),
            SharePrefData(ctx).retrieveSyncObject()?.FeeMap_Version ?: "",
            SharePrefData(ctx).retrieveSyncObject()?.Validator_Direction ?: "",
            SharePrefData(ctx).retrieveSyncObject()?.Station_StationCode ?: "",
            SharePrefData(ctx).retrieveSyncObject()?.Organization_OrganizationID ?: "",
            Utils.getWifiIp(ctx) ?: "",
            "validator",
            Utils.getMacAddr(),
            SharePrefData(ctx).retrieveSyncObject()?.Station_RouteID ?: "N/A",
            SharePrefData(ctx).retrieveSyncObject()?.FeeMap_Version ?: "",
            "BUS-0021"
        )
        apiManager.syncTransactionData(heartBeat, heartBeatModel, object :
            CallbackGeneric<ResponseBody> {
            override fun onResult(response: ResponseBody, requestCode: Int) {
                runWorkerAtEveryFiveMints()
            }

            override fun onError(errorMessage: String, requestCode: Int) {
                runWorkerAtEveryFiveMints()
            }
        })

        return Result.success()
    }

    fun runWorkerAtEveryFiveMints() {
        Log.d("heartBeat", "runWorkerAtEveryFiveMints: running")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
        val dailyWorkRequest = OneTimeWorkRequestBuilder<HeartbeatWorker>()
            .setConstraints(constraints.build())
            .setInitialDelay(5, TimeUnit.MINUTES)
            .addTag("heartBeat").build()
        applicationContext.let {
            WorkManager.getInstance(it).enqueueUniqueWork(
                "heartBeat",
                ExistingWorkPolicy.REPLACE, dailyWorkRequest
            )
        }
    }
}
package com.trverse.busvalidator.utilities

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.trverse.busvalidator.activities.BaseActivity
import com.trverse.busvalidator.activities.MainActivity
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.enumirations.AppAction
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.log

class DownloadFileUtility(var baseActivity: BaseActivity, var url: String) : FetchListener {

    var fetch: Fetch? = null
    var fetchConfigs: FetchConfiguration? = null
    val TAG = "DownloadFileUtility"

    fun downloadFile() {

        fetchConfigs = FetchConfiguration.Builder(baseActivity)
            .build()
        fetch = Fetch.Impl.getInstance(fetchConfigs!!)
//        val urlDummy = "https://file-examples-com.github.io/uploads/2017/02/file_example_XLS_10.xls"
        val urlDummy = "http://103.173.186.85:9004/FeeMap/GetDistanceFile"
        val file = FileDataUtility.getSaveDir(baseActivity) + "FeeMap.csv"
        val request = Request(urlDummy, file)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        fetch?.enqueue(request, {
            Log.d(TAG, "downloadFile: ${it.file}")
        }, {
            Log.d(TAG, "downloadFile: ${it.throwable?.message}")

        })
        fetch?.addListener(this)


    }

    override fun onAdded(download: Download) {
        Log.d(TAG, "onAdded: ")
    }

    override fun onCancelled(download: Download) {
        Log.d(TAG, "onCancelled: ${download.error.throwable?.message}")
        SharePrefData(baseActivity).fareTableDownloadedStatus(false)

    }

    override fun onCompleted(download: Download) {
        Log.d(TAG, "onCompleted: downloaded")
        download.file
        SharePrefData(baseActivity).fareTableDownloadedStatus(true)
        baseActivity.getCVSFile()
    }

    override fun onDeleted(download: Download) {
        Log.d(TAG, "onDeleted: deleted")
        SharePrefData(baseActivity).fareTableDownloadedStatus(false)

    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
        Log.d(TAG, "onDownloadBlockUpdated: ")
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Log.d(TAG, "onError: error")
        SharePrefData(baseActivity).fareTableDownloadedStatus(false)

        try {
            if (logsEnable) {
                baseActivity.lifecycleScope.launch {
                    (baseActivity as MainActivity<Any>).qrViewModel?.saveActivityLog(
                        ActivityLogs(
                            0,
                            AppAction.FARE_TABLE_DOWNLOAD.appAction.Code,
                            error.value.toString(),
                            SharePrefData(baseActivity)
                                .retrieveSyncObject()?.Validator_Code ?: "0",
                            baseActivity.getCurrentTime(),
                            AppAction.FARE_TABLE_DOWNLOAD.appAction.desc ?: "N/A"
                        )
                    )
                }
            }
        } catch (e: Exception) {
        }


    }

    override fun onPaused(download: Download) {
        Log.d(TAG, "onPaused: ")
        SharePrefData(baseActivity).fareTableDownloadedStatus(false)

    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        Log.d(TAG, "onProgress: $etaInMilliSeconds")
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Log.d(TAG, "onQueued: ")
    }

    override fun onRemoved(download: Download) {
        Log.d(TAG, "onRemoved: removed")
        SharePrefData(baseActivity).fareTableDownloadedStatus(false)


    }

    override fun onResumed(download: Download) {
        Log.d(TAG, "onResumed: resume downloading")
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Log.d(TAG, "onStarted: started")

    }

    override fun onWaitingNetwork(download: Download) {
        baseActivity.showErroDialog(
            "Not Download",
            "Unable to download, Please check your internet connection"
        )
        SharePrefData(baseActivity).fareTableDownloadedStatus(false)

    }
}
package com.trverse.busvalidator.activities

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.cardlan.cardlanlib.ByteUtil
import com.cardlan.cardlanlib.scan.ScanWork
import com.google.gson.Gson
import com.trverse.busvalidator.App
import com.trverse.busvalidator.R
import com.trverse.busvalidator.database.dbmodel.QRTravelTransaction
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.database.workers.DataSyncWorker
import com.trverse.busvalidator.database.workers.HeartbeatWorker
import com.trverse.busvalidator.dialogs.ErrorSuccessDialog
import com.trverse.busvalidator.dialogs.UserInfoDialog
import com.trverse.busvalidator.encryption.AESEncryption
import com.trverse.busvalidator.encryption.RSAEncryption
import com.trverse.busvalidator.enumirations.APIRequestEnums
import com.trverse.busvalidator.enumirations.AppAction
import com.trverse.busvalidator.enumirations.DataType
import com.trverse.busvalidator.models.GenericResponseModel
import com.trverse.busvalidator.models.cardsTypes.CardTypeModel
import com.trverse.busvalidator.models.farePolicyModels.FarePolicyResponseModel
import com.trverse.busvalidator.models.qr.DecryptedQR
import com.trverse.busvalidator.models.settings.SyncResponse
import com.trverse.busvalidator.mycallbacks.CustomGenericCallback
import com.trverse.busvalidator.network.APIManager
import com.trverse.busvalidator.network.CallbackGeneric
import com.trverse.busvalidator.network.NetworkStatus
import com.trverse.busvalidator.network.NetworkStatusHelper
import com.trverse.busvalidator.packages.QRViewModel
import com.trverse.busvalidator.utilities.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import java.util.*


class MainActivity<T> : BaseActivity(), CallbackGeneric<GenericResponseModel<Any>>,
    ScanWork.ScanQRListenner {
    val TAG: String = "QRScan"
    var dialogQr: ErrorSuccessDialog? = null
    var qrViewModel: QRViewModel? = null
    var scanWorker: ScanWork? = null
    val lastTime = 0L
    var doubleBackToShowDialog = false

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            //todo hideUI
            hideSystemUI(dateTime)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        hideActionbarAndStatusbar()
        setContentView(R.layout.activity_main)
        val c = Calendar.getInstance()
        val datetime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val dateformat = SimpleDateFormat("hh:mm:ss aa")
            val time = SimpleDateFormat("DD-MMM-yyyy")
            dateformat.format(c.time) + "\n" + time.format(c.time)
        } else {

        }
        dateTime.text = datetime.toString()
        scanWorker = ScanWork()
        dialogQr = ErrorSuccessDialog(
            this@MainActivity,
            "QR Scanned"
        )
        qrViewModel = ViewModelProvider(this).get(QRViewModel::class.java)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            getAllPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.CAMERA,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        enQueueDailySync()
        Handler().postDelayed(Runnable {
            //todo uncomment

            GetDataFromCard(this)

        }, lastTime)

//        todo uncomment
        scanWorker?.setSerial("/dev/ttyHSL2")
        scanWorker?.name = "name"
        scanWorker?.isDaemon = true
        scanWorker?.setListenner(this)
        scanWorker?.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkStatusHelper(this).observe(this) {

                when (it) {
                    NetworkStatus.Available -> {
                        online.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200))
                        online.setText("Online")
                    }
                    NetworkStatus.Unavailable -> {
                        online.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                        online.setText("Offline")
                    }
                }

            }
        }

        lifecycleScope.launch {
            qrViewModel?.getTotalUnSyncTransactions()?.observe(
                this@MainActivity,
                androidx.lifecycle.Observer {
                    Log.d("count", "count ${it.toString()} ")
                    notUpdatedTransTv?.text = it.toString()
                })
        }
        syncData()

    }




    private fun syncData() {

        APIManager().getSyncSettings(
            getMacAddr(), this,
            APIRequestEnums.SYNC_PARAMETERS_SETTINGS.requestCode.requestCode
        )
    }


    private fun insertRecord(qrResp: DecryptedQR, qrJson: String) {
        val validatorDirection = SharePrefData(this).retrieveSyncObject()?.Validator_Direction
        Handler().postDelayed({
            if (dialogQr?.isShowing == true) {
                dialogQr?.dismiss()
            }
        }, 2000L)

        if (validatorDirection.equals("IN", true) ||
            validatorDirection.equals("OUT", true)
        ) {
            lifecycleScope.launch {
                val qrRecords = qrViewModel?.getTransactions(qrResp.TID)
                if (qrRecords.isNullOrEmpty()) {
                    val data = QRTravelTransaction(
                        QRNumber = qrResp.TID,
                        Lat = locationData?.latitude.toString(),
                        Long = locationData?.longitude.toString(),
                        LoggedInUser = "",
                        LogType = if (validatorDirection.equals("IN", true)) "checkin"
                        else "checkout",
                        QRJson = qrJson,
                        StationCode = qrResp.stationCode,
                        Sync = false,
                        TransactionDateTime = getCurrentTime(),
                        id = 0,
                        TransactionType = if (validatorDirection.equals("IN", true)) "checkin"
                        else "checkout",
                        FareAmount = qrResp.fairAmount
                    )
                    qrViewModel?.insertQRRecord(data)
                    if (!dialogQr!!.isShowing) {

                        dialogQr?.setTitle(
                            if (validatorDirection.equals("IN", true)) "Check-In \nSuccessfully"
                            else "Check-Out\nSuccessfully"
                        )
                        dialogQr?.setData(
                            if (validatorDirection.equals(
                                    "IN",
                                    true
                                )
                            ) "Welcome Please board the bus"
                            else "Please leave the bus"
                        )
                        dialogQr?.setSuccessTune()
                        dialogQr?.setSuccessImage()
                        dialogQr?.show()
                    }
                } else {
                    if (!dialogQr!!.isShowing) {

                        dialogQr?.setTitle("QR Already used\n")
                        dialogQr?.setData(
                            "Last used at \n ${qrRecords[0].TransactionDateTime}"
                        )
                        dialogQr?.playtune(R.raw.already_inused)
                        dialogQr?.show()
                    }
                }
            }
        } else {
            //TODO this else is for direction "Both", so it will handle all the scenario, in/out auto
            lifecycleScope.launch {
                val qrRecords = qrViewModel?.getTransactions(qrResp.TID)
                if (qrRecords.isNullOrEmpty()) {
                    val data = QRTravelTransaction(
                        QRNumber = qrResp.TID,
                        Lat = locationData?.latitude.toString(),
                        Long = locationData?.longitude.toString(),
                        LoggedInUser = "",
                        LogType = DataType.CHECKIN.name.lowercase(),
                        QRJson = qrJson,
                        StationCode = qrResp.stationCode,
                        Sync = false,
                        TransactionDateTime = getCurrentTime(),
                        id = 0,
                        TransactionType = DataType.CHECKIN.name.lowercase(),
                        FareAmount = qrResp.fairAmount
                    )

                    lifecycleScope.launch {
                        qrViewModel?.insertQRRecord(data)
                        if (logsEnable) {
                            qrViewModel?.saveActivityLog(
                                ActivityLogs(
                                    0,
                                    AppAction.QR_CHECKIN.appAction.Code,
                                    qrResp.TID,
                                    SharePrefData(this@MainActivity)
                                        .retrieveSyncObject()?.Validator_Code ?: "0",
                                    getCurrentTime(),
                                    AppAction.QR_CHECKIN.appAction.desc ?: "N/A"
                                )
                            )
                        }
                    }
                    if (!dialogQr!!.isShowing) {

                        dialogQr?.setTitle("Check-in\nSuccessfully")
                        dialogQr?.setData(
                            "Welcome Please board the bus"
                        )
                        dialogQr?.setSuccessTune()
                        dialogQr?.setSuccessImage()
                        dialogQr?.show()
                    }
                } else {
                    val checkOut: QRTravelTransaction? =
                        qrRecords.find { data -> data.TransactionType == DataType.CHECKOUT.name }
                    if (checkOut == null) {
                        val data = QRTravelTransaction(
                            QRNumber = qrResp.TID,
                            Lat = locationData?.latitude.toString(),
                            Long = locationData?.longitude.toString(),
                            LoggedInUser = "",
                            LogType = DataType.CHECKOUT.name.lowercase(),
                            QRJson = qrJson,
                            StationCode = qrResp.stationCode,
                            Sync = false,
                            TransactionDateTime = getCurrentTime(),
                            id = 0,
                            TransactionType = DataType.CHECKOUT.name.lowercase(),
                            FareAmount = qrResp.fairAmount
                        )

                        lifecycleScope.launch {
                            qrViewModel?.insertQRRecord(data)
                            if (logsEnable) {
                                qrViewModel?.saveActivityLog(
                                    ActivityLogs(
                                        0,
                                        AppAction.QR_CHECKOUT.appAction.Code,
                                        qrResp.TID,
                                        SharePrefData(this@MainActivity)
                                            .retrieveSyncObject()?.Validator_Code ?: "0",
                                        getCurrentTime(),
                                        AppAction.QR_CHECKOUT.appAction.desc ?: "N/A"
                                    )
                                )
                            }
                        }

                        if (!dialogQr!!.isShowing) {

                            dialogQr?.setTitle("Check-Out\nSuccessfully")
                            dialogQr?.setData(
                                "Please leave the bus"
                            )

                            dialogQr?.setSuccessTune()
                            dialogQr?.setSuccessImage()
                            dialogQr?.show()
                        }


                    } else {
                        if (logsEnable) {
                            lifecycleScope.launch {
                                qrViewModel?.saveActivityLog(
                                    ActivityLogs(
                                        0,
                                        AppAction.QR_USED.appAction.Code,
                                        qrResp.TID,
                                        SharePrefData(this@MainActivity)
                                            .retrieveSyncObject()?.Validator_Code ?: "0",
                                        getCurrentTime(),
                                        AppAction.QR_USED.appAction.desc ?: "N/A"
                                    )
                                )
                            }
                        }
                        if (!dialogQr!!.isShowing) {

                            dialogQr?.setTitle("QR Already used\n")
                            dialogQr?.setData(
                                "Last used at \n ${checkOut.TransactionDateTime}"
                            )
                            dialogQr?.playtune(R.raw.already_inused)
                            dialogQr?.show()
                        }

                    }
                }

            }
        }
    }

    private fun enQueueDailySync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
            val heartbeatWorker = OneTimeWorkRequestBuilder<HeartbeatWorker>()
                .setConstraints(constraints.build())
                .addTag("heartBeat").build()

            // send heartBeat
            WorkManager.getInstance(this).enqueueUniqueWork(
                "heartBeat",
                ExistingWorkPolicy.REPLACE, heartbeatWorker
            )


            NetworkStatusHelper(this@MainActivity).observe(this) {
                if (it == NetworkStatus.Available) {

                    val syncDataRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
                        .setConstraints(constraints.build())
                        .addTag("syncDataDaily").build()

                    // send heartBeat

                    bussValidatorApp?.applicationContext?.let {

                        WorkManager.getInstance(it).enqueueUniqueWork(
                            "syncDataDaily",
                            ExistingWorkPolicy.REPLACE, syncDataRequest
                        )


                    }
                    WorkManager.getInstance(this).getWorkInfosByTagLiveData("syncDataDaily")
                        .observe(this, androidx.lifecycle.Observer {
                            if (it.isNullOrEmpty()) {
                                return@Observer
                            }
                            val workInfo = it[0]
                            notUpdatedTransTv?.text =
                                totalUnSyncTransaction.toString()
                        })

                } else if (it == NetworkStatus.Unavailable) {

                }

            }
        }
    }

    override fun onResult(response: GenericResponseModel<Any>, requestCode: Int) {
        if (requestCode == APIRequestEnums.SYNC_PARAMETERS_SETTINGS.requestCode.requestCode) {
            if (response.success) {
                val syncResponse = (response.result as SyncResponse).deviceDetails
                SharePrefData(this@MainActivity).saveSyncObject(syncResponse)
                val date = Date(System.currentTimeMillis())
                val millis = date.time
                SharePrefData(this@MainActivity).saveLong(SharePrefData.SYNC_TIME, millis)
                currentStationTv.text = syncResponse.Station_StationCode
                busDirectionTv.text = syncResponse.Validator_Direction
                validatorNumTv.text = syncResponse.Validator_Code
                logsEnable = syncResponse.Valiodator_IsLogging
                retrieveFarePolicy(syncResponse.Organization_OrganizationID)


            } else {
                showMacAddressErrorDialog(response.message)

            }
        } else if (requestCode == APIRequestEnums.FARE_POLICY.requestCode.requestCode) {
            if (response.success) {
                // check applicable which policy is applicable true, then save that policy
                val farePolicyList = response.result as ArrayList<FarePolicyResponseModel>
                for (i in 0 until farePolicyList.size) {
                    if (farePolicyList.get(i).isApplicable) {
                        SharePrefData(this@MainActivity).saveFarePolicyObject(farePolicyList.get(i))
                        break
                    }
                }
                if (SharePrefData(this).retriveCardsTpes() == null) {
                    APIManager().getCardsTypes(
                        SharePrefData(this).retrieveSyncObject()!!.Organization_OrganizationID,
                        this,
                        APIRequestEnums.CARD_TYPES.requestCode.requestCode
                    )
                } else {
                    if (!SharePrefData(this@MainActivity).getFareTableDownloadedStatus()!!) {
                        if (PermissionUtils.hasPermissionGranted(
                                this@MainActivity,
                                arrayOf(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            )
                        )
                            DownloadFileUtility(this, "").downloadFile()
                        else {
                            PermissionUtils.checkAndRequestPermissions(
                                this@MainActivity, arrayOf(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ), 112
                            )
                        }
                    } else {
                        getCVSFile()
                    }
                }

            } else {
                Log.d("Response", response.message)
            }
        } else if (requestCode == APIRequestEnums.CARD_TYPES.requestCode.requestCode) {
            if (response.success) {
                val cardsArray = response.result as ArrayList<*>
                SharePrefData(this).saveCardTypes(cardsArray as ArrayList<CardTypeModel>)

                if (!SharePrefData(this@MainActivity).getFareTableDownloadedStatus()!!) {
                    if (PermissionUtils.hasPermissionGranted(
                            this@MainActivity,
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )
                    )
                        DownloadFileUtility(this, "").downloadFile()
                    else {
                        PermissionUtils.checkAndRequestPermissions(
                            this@MainActivity, arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), 112
                        )
                    }
                } else {
                    getCVSFile()
                }
            }
        } else if (requestCode == APIRequestEnums.QR_USAGE.requestCode.requestCode) {
            if (response.success) {
                // QR is Already used
            }
        }

    }

    override fun onError(errorMessage: String, requestCode: Int) {
        if (requestCode == APIRequestEnums.SYNC_PARAMETERS_SETTINGS.requestCode.requestCode) {
            if (SharePrefData(this@MainActivity).retrieveSyncObject() == null) {
                showMacAddressErrorDialog(errorMessage)
            } else {
                val date = Date(System.currentTimeMillis())
                val millis = date.time
                SharePrefData(this@MainActivity).saveLong(SharePrefData.SYNC_TIME, millis)
                currentStationTv.text =
                    SharePrefData(this@MainActivity).retrieveSyncObject()?.Station_StationCode
                busDirectionTv.text =
                    SharePrefData(this@MainActivity).retrieveSyncObject()?.Validator_Direction
                validatorNumTv.text =
                    SharePrefData(this@MainActivity).retrieveSyncObject()?.Validator_Code
                logsEnable =
                    SharePrefData(this@MainActivity).retrieveSyncObject()?.Valiodator_IsLogging!!

                retrieveFarePolicy(SharePrefData(this@MainActivity).retrieveSyncObject()?.Organization_OrganizationID!!)
            }
        } else if (requestCode == APIRequestEnums.SYNC_PARAMETERS_SETTINGS.requestCode.requestCode) {
            // locally check QR
        } else {
            showErroDialog("Error", errorMessage)
        }


    }

    private fun getAllPermissions(request: Array<String>) {

        val requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    val permissionName = it.key
                    val isGranted = it.value
                    if (isGranted) {
                        // Permission is granted
                        getLocation()
                    } else {
                        // Permission is denied
                    }
                }
            }
        requestPermission.launch(request)
    }

    override fun scanResult(result: String?) {
        Log.d(TAG, "scanResult: Encrypted Result")
        if (App.macMapp) {
            Log.d(TAG, "$result")
            if (SystemClock.elapsedRealtime() - lastTimeCheck < 4000) {
                Log.d(TAG, "scanResult: in less than 4 seconds")
                return
            }
            lastTimeCheck = SystemClock.elapsedRealtime()

            runOnUiThread {
                try {
                    Log.d(TAG, "scanResult: Beginning of Decryption")
                    val rsaAlgo = RSAEncryption(this@MainActivity)
                    rsaAlgo.loadPrivateKey()
                    val decryptedQrRSA = rsaAlgo.decrypt(ByteUtil.hexStringToString(result))

                    val decryptedQr = AESEncryption.getShared()
                        .decrypt(ByteUtil.hexStringToString(result)) // decrypt by AES
                    Log.d(TAG, "decrypted: $decryptedQr")

                    if (decryptedQr.isNullOrEmpty()) {
                        Log.d(TAG, "scanResult: Decrypted Result is Null or Empty")
                        if (logsEnable) {
                            Log.d(TAG, "scanResult: LogsEnabling Check")
                            lifecycleScope.launch {
                                Log.d(TAG, "scanResult: Saving logs of Result is null")
                                qrViewModel?.saveActivityLog(
                                    ActivityLogs(
                                        0,
                                        AppAction.QR_INVALID.appAction.Code,
                                        "N/A",
                                        SharePrefData(this@MainActivity)
                                            .retrieveSyncObject()?.Validator_Code ?: "0",
                                        getCurrentTime(),
                                        AppAction.QR_INVALID.appAction.desc ?: "N/A"
                                    )
                                )
                            }
                        }

                        if (!dialogQr!!.isShowing) {
                            dialogQr?.setTitle("Invalid\nQR Code")
                            dialogQr?.setData(
                                "Please scan again or\n" +
                                        "get a new QR code"
                            )
                            dialogQr?.setErrorImage()
                            dialogQr?.playtune(R.raw.error_tune)
                            dialogQr?.show()
                            Log.d(TAG, "scanResult: Invalid QR Code")
                        }
                    } else {
                        Log.d(TAG, "scanResult: Decrypted Result")
                        val gson = Gson()
                        val qrResp = gson.fromJson(decryptedQr, DecryptedQR::class.java)
                        if (qrResp.stationCode.equals(SharePrefData(this@MainActivity).retrieveSyncObject()?.Station_StationCode)) {
                            if (checkTimeLess(qrResp.endTime)) {
                                Log.d(TAG, "scanResult: Decrypted Result insert to local db start")
                                // verify QR from Server
                                insertRecord(qrResp, decryptedQr)
                                Log.d(
                                    TAG,
                                    "scanResult: Decrypted Result insertered to local db end"
                                )
//                            checkQrUsageFromServer(qrResp, decryptedQr)
                            } else {

                                if (!dialogQr!!.isShowing) {
                                    Log.d(TAG, "scanResult: QR Expired")
                                    dialogQr?.setTitle("QR Expired")
                                    dialogQr?.setData(
                                        "Your QR is expired\n Created\n ${qrResp.startTime}" +
                                                "\nExpired\n${qrResp.endTime}"
                                    )
                                    dialogQr?.playtune(R.raw.error_tune)
                                    dialogQr?.setErrorImage()
                                    dialogQr?.show()
                                }

                            }
                        } else  // bind station and QR Station code is not matched
                        {
                            showErroDialog(
                                "Wrong Station Bind",
                                "Your ticket station\ncode is not matched with \ndevice station"
                            )
                        }

                    }

                    Handler().postDelayed(Runnable {
                        if (dialogQr?.isShowing == true) {
                            Log.d(TAG, "scanResult: Dialog Dismissed")
                            dialogQr?.dismiss()
                        }
                    }, 2000) // delay to dismiss dialog after this seconds
                } catch (e: Exception) {
                    Log.d("scan", "scanResult: ${e.message}")
                }
            }
        } else {
            Log.d("data", "$result")
            if (SystemClock.elapsedRealtime() - lastTimeCheck < 4000) {

                return
            }
            runOnUiThread {
                Log.d(TAG, "scanResult: MAC not mapped")
                lastTimeCheck = SystemClock.elapsedRealtime()
                showMacAddressErrorDialog(null)

            }
        }

    }

/*
    private fun checkQrUsageFromServer(qrResp: DecryptedQR?, decryptedQr: String) {
        APIManager().checkQRUsage(
            qrResp!!.TID,
            SharePrefData(this@MainActivity).retrieveSyncObject()
                ?.Validator_Direction ?: "", object : CallbackGeneric<GenericResponseModel<Any>> {
                override fun onError(errorMessage: String, requestCode: Int) {
                    insertRecord(qrResp, decryptedQr)

                }

                override fun onResult(response: GenericResponseModel<Any>, requestCode: Int) {
                    if (response.success) {
                        if (!dialogQr!!.isShowing) {
                            dialogQr?.setTitle("QR Already used\n")
                            dialogQr?.setData(
                                response.message
                            )
                            dialogQr?.playtune(R.raw.already_inused)
                            dialogQr?.show()
                        }
                    } else {
                        insertRecord(qrResp, decryptedQr)

                    }
                }
            }, APIRequestEnums.QR_USAGE
                .requestCode.requestCode
        )
    }
*/

    override fun repatScan() {
        if (scanWorker?.isAlive == true) {
            Log.d(TAG, "scanWorker: is Alive")
            return
        } else {
            scanWorker?.name = "name"
            scanWorker?.isDaemon = true
            scanWorker?.start()
            Log.d(TAG, "scanWorker: is Restarting")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && doubleBackToShowDialog) {
            Log.d("keyDown", "onKeyDown: key down")
            if (App.macMapp) {
                UserInfoDialog(this@MainActivity, object : CustomGenericCallback {
                    override fun genericCallback(type: Int, data: Any) {
                        if (data is String) {
                            val stingArray = data.split(",")
                            if (stingArray[0] == "admin" && stingArray[1] == "admin") {
                                val bundle = Bundle()
                                bundle.putString("goto", "SyncData")
                                openActivity(GenericActivity::class.java, bundle)
                            } else if (stingArray[0] == "logs" && stingArray[1] == "admin") {
                                val bundle = Bundle()
                                bundle.putString("goto", "activityLogs")
                                openActivity(GenericActivity::class.java, bundle)
                            } else {
                                showToast("Invalid User or Password")
                            }
                        }
                    }

                }).show()


            } else {
                showMacAddressErrorDialog(null)
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && doubleBackToShowDialog) {
            if (App.macMapp) {
                val bundle = Bundle()
                bundle.putString("goto", "syncParamter")
                openActivity(GenericActivity::class.java, bundle)
            } else {
                showMacAddressErrorDialog(null)
            }

        } else {
            doubleBackToShowDialog = true
            Handler().postDelayed({
                doubleBackToShowDialog = false
            }, 2000)
        }
        return super.onKeyDown(keyCode, event)

    }

    private fun retrieveFarePolicy(organistionId: String) {
        if (SharePrefData(this).retrieveFarePolicyObject() == null) {
            APIManager().getFarePolicy(
                organistionId,
                this,
                APIRequestEnums.FARE_POLICY.requestCode.requestCode
            )
        } else if (SharePrefData(this).retriveCardsTpes() == null) {
            APIManager().getCardsTypes(
                organistionId,
                this,
                APIRequestEnums.CARD_TYPES.requestCode.requestCode
            )
        } else {
            if (!SharePrefData(this@MainActivity).getFareTableDownloadedStatus()!!)
                DownloadFileUtility(this, "").downloadFile()
            else getCVSFile()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 112 && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DownloadFileUtility(this, "").downloadFile()

            } else {
                showToast("Permissions not granted for download faretable")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SharePrefData(this).retrieveSyncObject() != null) {
            currentStationTv.text =
                SharePrefData(this@MainActivity).retrieveSyncObject()?.Station_StationCode
            busDirectionTv.text =
                SharePrefData(this@MainActivity).retrieveSyncObject()?.Validator_Direction
            validatorNumTv.text =
                SharePrefData(this@MainActivity).retrieveSyncObject()?.Validator_Code
            logsEnable =
                SharePrefData(this@MainActivity).retrieveSyncObject()?.Valiodator_IsLogging ?: true
        }
    }


}
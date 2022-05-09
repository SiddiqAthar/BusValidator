package com.trverse.busvalidator.utilities

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.cardlan.cardlanlib.ByteUtil
import com.trverse.busvalidator.App
import com.trverse.busvalidator.activities.BaseActivity
import com.trverse.busvalidator.activities.MainActivity
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.dialogs.SuccessDialog
import com.trverse.busvalidator.enumirations.AppAction
import kotlinx.coroutines.launch
import vpos.apipackage.CardLanStandardBus
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.N)
class GetDataFromCard(var baseActivity: BaseActivity) {
    private val TAG: String="CardData"
    private var standardBus: CardLanStandardBus? = null
    val lastTime = 1000L
    var result: String? = null
    var picc_type = 'B'.toByte()
    var blkNo: Byte = 4
    var dialog: SuccessDialog? = null
    init
    {
        standardBus = CardLanStandardBus()
        coroutineCard()
    }

    fun getCardData(): ArrayList<Any>? {

        try {
             standardBus?.callInitDev()
            val serialNo = standardBus?.cardResetBytes
            if (serialNo == null || serialNo.isEmpty()) {
                // error in card initialization sleep for few sec
                SystemClock.sleep(100)
                result = null
                return null
            } else {
                // response than read balance and check if it is valid check-in / check-out
                val cardSerialNoHexString =
                    ByteUtil.bytearrayToHexString(serialNo, serialNo.size)
                //card password
                val key = "PhGxoY".toByteArray()
                result = cardSerialNoHexString

                val checkInStatus =
                    standardBus?.callReadOneSectorDataFromCard(picc_type, 8, key, serialNo)
                val balance =
                    standardBus?.callReadOneSectorDataFromCard(picc_type, 10, key, serialNo)
                val currentStationCode =
                    standardBus?.callReadOneSectorDataFromCard(picc_type, 9, key, serialNo)
                var str = ArrayList<Any>()
                if (balance != null && checkInStatus != null) {

                    val checkInStatus = Integer.parseInt(String(checkInStatus))
                    val balance = Integer.parseInt(String(balance))
                    str = ArrayList<Any>()
                    str.add(true)
                    str.add(cardSerialNoHexString)
//                    str.add(checkInStatus)

                    if (balance > 50) {
                        if (SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Direction.equals(
                                "Both",
                                true
                            )
                        ) {
                            if (checkInStatus == 0) {
                                // check in already change to check-out no need to deduct money
                                val unpadded = "1"
                                val padded =
                                    "0000000000000000".substring(unpadded.length) + unpadded
                                blkNo = 8
                                val ret = standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    blkNo,
                                    key,
                                    serialNo,
                                    padded.toByteArray()
                                )

                                // write station code on sector 9
                                val unpaddedCurrentStation =
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode

                                val paddedCurrentStation =
                                    "0000000000000000".substring(unpaddedCurrentStation?.length!!) + unpaddedCurrentStation

                                standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    9,
                                    key,
                                    serialNo,
                                    paddedCurrentStation.toByteArray()
                                )

                                Log.d("Write Travel Status:: ", if (ret == 0) "Success" else "fail")

                                str.add(0)
                                str.add(balance)
                                str.add(balance)// for new balance


                                //saving logs
                                val log = ActivityLogs(
                                    0,
                                    AppAction.CARD_CHECKIN.appAction.Code,
                                    cardSerialNoHexString,
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code
                                        ?: "0",
                                    baseActivity.getCurrentTime(),
                                    AppAction.CARD_CHECKIN.appAction.desc ?: ""
                                )
                                saveLog(log)
                            }


                            else {
                                // check out change to check-in deduct money and save it back to the card
                                val unpadded = "0"
                                val checkInPad =
                                    "0000000000000000".substring(unpadded.length) + unpadded
                                 blkNo = 8
                                val ret = standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    blkNo,
                                    key,
                                    serialNo,
                                    checkInPad.toByteArray()
                                )
                                var startStation = "0006"
                                if (currentStationCode != null) {
                                    startStation = String(currentStationCode)
                                }
                                val amountTobeDeduct =
                                    baseActivity.getCalculatedAmoutFromFareTable(
                                        startStation.takeLast(4),
                                        SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode!!
                                    )
                                val _bal = "${(balance - amountTobeDeduct.Amount.toInt())}"
                                Log.d(
                                    "GetDataFromCard",
                                    "start Staion is ${startStation.takeLast(4)}"
                                )
                                Log.d(
                                    "GetDataFromCard",
                                    "current Staion is ${SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode}"
                                )
                                Log.d(
                                    "GetDataFromCard", "total Kms ${
                                        baseActivity.getDistanceBetweenTwoStations(
                                            startStation.takeLast(4),
                                            SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode!!
                                        )
                                    }"
                                )
                                Log.d(
                                    "GetDataFromCard",
                                    "Deducted Amount ${amountTobeDeduct.toString()}"
                                )
                                val balancePad =
                                    "0000000000000000".substring(_bal.length) + _bal
                                blkNo = 10
                                val bal = standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    blkNo,
                                    key,
                                    serialNo,
                                    balancePad.toByteArray()
                                )
                                Log.d("Write Travel Status:: ", if (ret == 0) "Success" else "fail")
                                Log.d(
                                    "Write Balance Status:: ",
                                    if (bal == 0) "Success" else "fail"
                                )
                                str.add(1)
                                str.add(balance)// old balance
                                str.add(_bal)// new balance
                                str.add(amountTobeDeduct.Amount.toString())// amount deducted from old to new
                                str.add(amountTobeDeduct.policyRuleID)// policyRule from slab
                                str.add(amountTobeDeduct.Version)// version from slab


                                //saving logs
                                val log = ActivityLogs(
                                    0,
                                    AppAction.CARD_CHECKOUT.appAction.Code,
                                    cardSerialNoHexString,
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code
                                        ?: "0",
                                    baseActivity.getCurrentTime(),
                                    AppAction.CARD_CHECKOUT.appAction.desc ?: ""
                                )
                                saveLog(log)

                            }
                        } else if (SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Direction.equals(
                                "In",
                                true
                            )
                        ) {
                            if (checkInStatus == 0) {
                                // check in already change to check-out no need to deduct money
                                val unpadded = "1"
                                val padded =
                                    "0000000000000000".substring(unpadded.length) + unpadded
                                blkNo = 8
                                val ret = standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    blkNo,
                                    key,
                                    serialNo,
                                    padded.toByteArray()
                                )

                                // write station code on sector 9
                                val unpaddedCurrentStation =
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode

                                val paddedCurrentStation =
                                    "0000000000000000".substring(unpaddedCurrentStation?.length!!) + unpaddedCurrentStation

                                standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    9,
                                    key,
                                    serialNo,
                                    paddedCurrentStation.toByteArray()
                                )

                                Log.d("Write Travel Status:: ", if (ret == 0) "Success" else "fail")

                                str.add(0)
                                str.add(balance)
                                str.add(balance)// for new balance

                                Log.d(TAG, "getCardData: CheckIn")

                                //saving logs
                                val log = ActivityLogs(
                                    0,
                                    AppAction.CARD_CHECKIN.appAction.Code,
                                    cardSerialNoHexString,
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code
                                        ?: "0",
                                    baseActivity.getCurrentTime(),
                                    AppAction.CARD_CHECKIN.appAction.desc ?: ""
                                )
                                saveLog(log)
                            } else {

                                //saving logs
                                val log = ActivityLogs(
                                    0,
                                    AppAction.CARD_ALREADY_CHECKIN.appAction.Code,
                                    cardSerialNoHexString,
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code
                                        ?: "0",
                                    baseActivity.getCurrentTime(),
                                    AppAction.CARD_ALREADY_CHECKIN.appAction.desc ?: ""
                                )
                                saveLog(log)

                                Log.d(TAG, "getCardData: Already CheckIn")

                                str = ArrayList<Any>()
                                str.add(false)
                                str.add(cardSerialNoHexString)
                                str.add("You are already check-in")
                                return str
                            }
                        } else if (SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Direction.equals(
                                "Out",
                                true
                            )
                        ) {
                            if (checkInStatus == 1) {
                                // check out change to check-in deduct money and save it back to the card
                                val unpadded = "0"
                                val checkInPad =
                                    "0000000000000000".substring(unpadded.length) + unpadded
                                blkNo = 8
                                val ret = standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    blkNo,
                                    key,
                                    serialNo,
                                    checkInPad.toByteArray()
                                )
                                var startStation = "0006"
                                if (currentStationCode != null) {
                                    startStation = String(currentStationCode)
                                }
                                val amountTobeDeduct =
                                    baseActivity.getCalculatedAmoutFromFareTable(
                                        startStation.takeLast(4),
                                        SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode!!
                                    )
                                val _bal = "${(balance - amountTobeDeduct.Amount.toInt())}"
                                Log.d(
                                    "GetDataFromCard",
                                    "start Staion is ${startStation.takeLast(4)}"
                                )
                                Log.d(
                                    "GetDataFromCard",
                                    "current Staion is ${SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode}"
                                )
                                Log.d(
                                    "GetDataFromCard", "total Kms ${
                                        baseActivity.getDistanceBetweenTwoStations(
                                            startStation.takeLast(4),
                                            SharePrefData(baseActivity).retrieveSyncObject()?.Station_StationCode!!
                                        )
                                    }"
                                )
                                Log.d(
                                    "GetDataFromCard",
                                    "Deducted Amount ${amountTobeDeduct.toString()}"
                                )
                                val balancePad =
                                    "0000000000000000".substring(_bal.length) + _bal
                                blkNo = 10
                                val bal = standardBus?.callWriteOneSertorDataToCard(
                                    picc_type,
                                    blkNo,
                                    key,
                                    serialNo,
                                    balancePad.toByteArray()
                                )
                                Log.d("Write Travel Status:: ", if (ret == 0) "Success" else "fail")
                                Log.d(
                                    "Write Balance Status:: ",
                                    if (bal == 0) "Success" else "fail"
                                )
                                str.add(1)
                                str.add(balance)// old balance
                                str.add(_bal)// new balance
                                str.add(amountTobeDeduct.Amount.toString())// amount deducted from old to new
                                str.add(amountTobeDeduct.policyRuleID)// amount deducted from old to new
                                str.add(amountTobeDeduct.Version)// amount deducted from old to new

                                Log.d(TAG, "getCardData: Checkout")

                                //saving logs
                                val log = ActivityLogs(
                                    0,
                                    AppAction.CARD_CHECKOUT.appAction.Code,
                                    cardSerialNoHexString,
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code
                                        ?: "0",
                                    baseActivity.getCurrentTime(),
                                    AppAction.CARD_CHECKOUT.appAction.desc ?: ""
                                )
                                saveLog(log)


                            } else {

                                //saving logs
                                val log = ActivityLogs(
                                    0,
                                    AppAction.CARD_ALREADY_CHECKOUT.appAction.Code,
                                    cardSerialNoHexString,
                                    SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code
                                        ?: "0",
                                    baseActivity.getCurrentTime(),
                                    AppAction.CARD_ALREADY_CHECKOUT.appAction.desc ?: ""
                                )
                                saveLog(log)


                                Log.d(TAG, "getCardData: You are already checkout")

                                str = ArrayList<Any>()
                                str.add(false)
                                str.add(cardSerialNoHexString)
                                str.add("You are already checkout")
                                return str
                            }
                        }
                    } else {

                        //todo remove Auto Recharge
                        // for testing Purpose Auto Recharge
                        /*val _bal = "${(balance + 300)}"
                        val balancePad =
                            "0000000000000000".substring(_bal.length) + _bal
                        blkNo = 10
                        val bal = standardBus?.callWriteOneSertorDataToCard(
                            picc_type,
                            blkNo,
                            key,
                            serialNo,
                            balancePad.toByteArray()
                        )*/

                        //saving logs
                        val log = ActivityLogs(
                            0,
                            AppAction.CARD_INSUFFiCIENT.appAction.Code,
                            cardSerialNoHexString,
                            SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code ?: "0",
                            baseActivity.getCurrentTime(),
                            AppAction.CARD_INSUFFiCIENT.appAction.desc ?: ""
                        )
                        saveLog(log)


                        Log.d(TAG, "getCardData: Insufficient balance Please Recharge")
                        str = ArrayList<Any>()
                        str.add(false)
                        str.add(cardSerialNoHexString)
                        str.add("Insufficient balance\nPlease Recharge")
                        str.add(balance)
                        return str
                    }
                } else {
                    str = ArrayList<Any>()
                    str.add(false)
                    str.add(cardSerialNoHexString)
                    str.add("Invalid Card Please try again")
                    Log.d(TAG, "getCardData: Invalid Card Please try again")

                    //saving logs
                    val log = ActivityLogs(
                        0,
                        AppAction.CARD_INVALID.appAction.Code,
                        "N/A",
                        SharePrefData(baseActivity).retrieveSyncObject()?.Validator_Code ?: "0",
                        baseActivity.getCurrentTime(),
                        AppAction.CARD_INVALID.appAction.desc ?: ""
                    )
                    saveLog(log)



                    return str
                }


                return str
            }
        } catch (e: Exception) {
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun coroutineCard() {
        baseActivity.lifecycleScope.launch {

            val cardResult = getCardData()
            if (!cardResult.isNullOrEmpty()) {
                val c = Calendar.getInstance()
                val dateformat = SimpleDateFormat("MMM, hh:mm:ss aa")
                val date = dateformat.format(c.time)
                if (cardResult[0] == false) {
                    if (cardResult.size > 3) {
                        dialog = SuccessDialog(
                            baseActivity,
                            cardResult[2] as String, cardResult[1] as String,
                            date, "${cardResult[3]}", true
                        )
                    } else {
                        dialog = SuccessDialog(
                            baseActivity,
                            cardResult[2] as String, cardResult[1] as String,
                            date, "-", true
                        )

                    }
                } else {
                    val check = if (cardResult[2] == 0) "Check-In" else "Check-Out"
                    if (cardResult[2] == 0) {
                        dialog = SuccessDialog(
                            baseActivity,
                            "Welcome to Trverse\nYou may board the bus now.", check,
                            date, "${cardResult[3]}", false
                        )
                    } else {
                        dialog = SuccessDialog(
                            baseActivity,
                            "Thankyou for choosing Trverse\n Please leave the Bus", check,
                            date, "${cardResult[4]}", false
                        )
                    }
                }
                if (App.macMapp) {
                    if (cardResult[0] == true)
                        (baseActivity as MainActivity<Any>).qrViewModel?.insertCardTravelTransaction(
                            cardResult
                        )
                    dialog?.show()
                } else {
                    baseActivity.showMacAddressErrorDialog(null)
                }
                Handler().postDelayed({
                    dialog?.dismiss()
                    coroutineCard()
                }, lastTime)
            } else {
                Handler().postDelayed({
                    coroutineCard()
                }, lastTime)
            }
        }
    }

    fun saveLog(log: ActivityLogs) {
        if (logsEnable) {
            baseActivity.lifecycleScope.launch {
                (baseActivity as MainActivity<Any>).qrViewModel?.saveActivityLog(log)
            }
        }
    }
}
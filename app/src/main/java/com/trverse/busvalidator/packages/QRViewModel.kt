package com.trverse.busvalidator.packages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.trverse.busvalidator.App
import com.trverse.busvalidator.database.cardTransactionModels.CardTravelTransaction
import com.trverse.busvalidator.database.db.AFCDatabase
import com.trverse.busvalidator.database.dbmodel.QRTravelTransaction
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.database.repository.Repository
import com.trverse.busvalidator.utilities.SharePrefData
import com.trverse.busvalidator.utilities.Utils

class QRViewModel(application: Application) : AndroidViewModel(application) {

    val dao = AFCDatabase.getDatabase(application).afcDatabaseDao
    val repo = Repository(dao)

    suspend fun getTransactions(qr: String): List<QRTravelTransaction> {
        return repo.getTransactionByQR(qr)
    }

    suspend fun getAllActivityLogs(): List<ActivityLogs> {
        return repo.getAllActivityLogs().reversed()
    }

    suspend fun insertQRRecord(qr: QRTravelTransaction) {
        repo.saveTransaction(qr)
    }

    suspend fun saveActivityLog(log: ActivityLogs) {
        repo.removeOldLogsData()
        repo.saveActivityLog(log)
    }

//    suspend fun getUnSyncCount() = repo.getAllUnSyncCount()

    suspend fun insertCardTravelTransaction(arrayList: ArrayList<Any>) {
        var cardTravelTransaction: CardTravelTransaction? = null
        val syncData = SharePrefData(getApplication()).retrieveSyncObject()
        if (arrayList.get(2) == 0) {
            // 0 for checkIn
            cardTravelTransaction = CardTravelTransaction(
                0, syncData?.Validator_Code ?: "",
                /*arrayList.get(2).toString()*/"checkin", arrayList.get(1).toString(), Utils.currentDateTime(),
                syncData?.Station_StationCode ?: "", syncData?.Validator_ID ?: "",
                "-", "-", arrayList.get(3).toString(), false, null,
                null, null, null, null
            )
        } else {
            cardTravelTransaction = CardTravelTransaction(
                0,
                syncData?.Validator_Code ?: "",
                /*arrayList.get(2).toString()*/"checkout" /*forStatus*/,
                arrayList.get(1).toString() /*forCardNumber*/,
                Utils.currentDateTime(),
                syncData?.Station_StationCode ?: "",
                syncData?.Validator_ID ?: "",
                "-",
                "-",
                arrayList.get(3).toString() /*forOldBalance*/,
                false,
                arrayList.get(5).toString() /*forAmountTobeDeduct*/,
                App.INSTANCE?.applicationContext?.let {
                    SharePrefData(it).retrieveFarePolicyObject()?.code
                },
                arrayList.get(6).toString() /*forApplicablePolicyRuleId*/,
                arrayList.get(7).toString() /*forApplicablePolicyRuleVersion*/,
                arrayList.get(4).toString() /*forNewBalnce*/
            )
        }
        repo.saveCardTransaction(cardTravelTransaction)
    }

    suspend fun getTotalUnSyncTransactions() = repo.getTotalUnSyncTransactions()

}
package com.trverse.busvalidator.database.repository

import com.trverse.busvalidator.database.cardTransactionModels.CardTravelTransaction
import com.trverse.busvalidator.database.dao.AFCDao
import com.trverse.busvalidator.database.dbmodel.QRTravelTransaction
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.models.userModel.UserInfo

class Repository(private val dao: AFCDao) {
    fun getUser() =
        dao.getUserInfo()

    fun getAllUserInfo() =
        dao.getAllUserInfo()

   suspend fun getAllActivityLogs() =
        dao.getAllActivityLogs()

    suspend fun saveUserInfo(userInfo: UserInfo) =
        dao.saveUser(userInfo)

    suspend fun deleteUsers() = dao.deleteUser()

    suspend fun saveTransaction(data: QRTravelTransaction) = dao.saveTransaction(data)

    suspend fun saveActivityLog(data: ActivityLogs) = dao.saveActivityLog(data)

    suspend fun getTransactionByQR(qr: String) = dao.getTransactionByQR(qr)

    fun getAllTransactionQR() = dao.getAllTransactionQR()

    fun getAllUnSyncQRTrans() = dao.getUnSyncQRTransaction()

    fun getAllCardSyncTransaction() = dao.getAllCardSyncTransaction()

//    fun getAllUnSyncCount() = dao.getUnSyncCount()

    suspend fun updateQrTransaction(qrTravelTransaction: QRTravelTransaction) =
        dao.updateQrTransaction(qrTravelTransaction)

    suspend fun updateCardTransaction(cardTravelTransaction: CardTravelTransaction) =
        dao.updateCardTransaction(cardTravelTransaction)

    suspend fun deleteTransaction(qrNo: String) = dao.deleteQRTransaction(qrNo)
    suspend fun removeOldQRTransaction() = dao.removeOldQRTransaction()


    suspend fun deleteCardTransaction(id: Int) = dao.deleteCardTransaction(id)

    suspend fun saveCardTransaction(data: CardTravelTransaction) = dao.saveCardTransaction(data)

    suspend fun getTotalUnSyncTransactions() = dao.getTotalUnSyncTransactions()

    suspend fun getTotalUnSyncTransactionsNoLiveData() = dao.getTotalUnSyncTransactionsNoLiveData()

    suspend fun getUnSyncCardTransaction() = dao.getUnSyncCardTransaction()

    suspend fun removeOldLogsData() = dao.removeOldLogsData()
}
package com.trverse.busvalidator.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.trverse.busvalidator.database.cardTransactionModels.CardTravelTransaction
import com.trverse.busvalidator.database.dbmodel.QRTravelTransaction
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.models.settings.StationInfo
import com.trverse.busvalidator.models.userModel.UserInfo

@Dao
interface AFCDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStations(users: List<StationInfo>)

    @Query("DELETE FROM userInfo")
    suspend fun deleteUser()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTransaction(qrData: QRTravelTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveActivityLog(qrData: ActivityLogs)

    @Transaction
    @Query("Select * from userInfo Limit 1")
    fun getUserInfo(): LiveData<UserInfo>

    @Transaction
    @Query("Select * from userInfo")
    fun getAllUserInfo(): LiveData<List<UserInfo>>

    @Transaction
    @Query("Select * from ActivityLogs")
   suspend fun getAllActivityLogs(): List<ActivityLogs>

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("Select * from qrtraveltransaction  where QRNumber  =:qrNo ")
    suspend fun getTransactionByQR(qrNo: String): List<QRTravelTransaction>

    @Transaction
    @Query("Select * from qrtraveltransaction WHERE Sync ==1")
    fun getAllTransactionQR(): List<QRTravelTransaction>

    @Transaction
    @Query("Select * from qrtraveltransaction WHERE Sync ==0")
    fun getUnSyncQRTransaction(): List<QRTravelTransaction>

/*
    @Transaction
    @Query("Select * from qrtraveltransaction WHERE Sync ==0")
    fun getUnSyncCount(): LiveData<List<QRTravelTransaction>>
*/

    @Query("DELETE FROM qrtraveltransaction WHERE QRNumber = :qrNo")
    suspend fun deleteQRTransaction(qrNo: String)

    @Query("DELETE FROM cardtraveltransaction WHERE id = :id")
    suspend fun deleteCardTransaction(id: Int)

    @Update
    suspend fun updateQrTransaction(qrTravelTransaction: QRTravelTransaction)

    @Query("DELETE FROM qrtraveltransaction WHERE QRNumber = :qrNo")
    suspend fun deleteQrTran(qrNo: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCardTransaction(cardData: CardTravelTransaction)

    @Transaction
    @Query("SELECT (Select COUNT(*) from qrtraveltransaction WHERE Sync ==0)+(Select COUNT(*) from cardtraveltransaction WHERE Sync ==0)")
    fun getTotalUnSyncTransactions(): LiveData<Int>

    @Transaction
    @Query("SELECT (Select COUNT(*) from qrtraveltransaction WHERE Sync ==0)+(Select COUNT(*) from cardtraveltransaction WHERE Sync ==0)")
    fun getTotalUnSyncTransactionsNoLiveData(): Int

    @Transaction
    @Query("Select * from cardtraveltransaction WHERE Sync ==0")
    fun getUnSyncCardTransaction(): List<CardTravelTransaction>

    @Update
    suspend fun updateCardTransaction(qrTravelTransaction: CardTravelTransaction)


    @Transaction
    @Query("Select * from cardtraveltransaction WHERE Sync ==1")
    fun getAllCardSyncTransaction(): List<CardTravelTransaction>


    @Query("DELETE FROM ActivityLogs WHERE id IN (SELECT id FROM ActivityLogs ORDER BY createdAt DESC LIMIT 1 OFFSET 1000)")
    suspend fun removeOldLogsData()

}
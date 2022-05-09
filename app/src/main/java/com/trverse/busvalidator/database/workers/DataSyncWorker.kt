package com.trverse.busvalidator.database.workers

import android.content.Context
import android.widget.Toast
import androidx.work.*
import com.trverse.busvalidator.App
import com.trverse.busvalidator.database.db.AFCDatabase
import com.trverse.busvalidator.database.repository.Repository
import com.trverse.busvalidator.network.APIManager
import com.trverse.busvalidator.network.CallbackGeneric
import com.trverse.busvalidator.utilities.cardTransaction
import com.trverse.busvalidator.utilities.qrTransaction
import com.trverse.busvalidator.utilities.totalUnSyncTransaction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.util.*
import java.util.concurrent.TimeUnit


class DataSyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val appContext = applicationContext
        // val notificationManager = makeStatusNotification("Syncing Data to Server", appContext)
        // query suspended from DB and then read values to sync to the server
        val dao = AFCDatabase.getDatabase(appContext).afcDatabaseDao
        val repo = Repository(dao)
        val qrTransactionItem = repo.getAllUnSyncQRTrans()
        val cardUnSyncTransaction = repo.getUnSyncCardTransaction()
        totalUnSyncTransaction = repo.getTotalUnSyncTransactionsNoLiveData()
        val apiManager = APIManager()
        if (qrTransactionItem.isNotEmpty()) {
            val trans = qrTransactionItem[0]
            apiManager.syncTransactionData(
                qrTransaction,
                trans,
                object : CallbackGeneric<ResponseBody> {
                    override fun onResult(response: ResponseBody, requestCode: Int) {
                        GlobalScope.launch {
//                            repo.deleteTransaction(trans.QRNumber)
                            trans.Sync = true
                            repo.updateQrTransaction(trans)
                        }
                        return
                    }

                    override fun onError(errorMessage: String, requestCode: Int) {
                        Toast.makeText(App.context, errorMessage, Toast.LENGTH_LONG).show()
                        return
                    }

                }
            )
            return Result.retry()
        }

        else if (!cardUnSyncTransaction.isEmpty()) {
            val cardTransactionItem = cardUnSyncTransaction[0]
            apiManager.syncTransactionData(
                cardTransaction,
                cardTransactionItem, object : CallbackGeneric<ResponseBody> {
                    override fun onResult(response: ResponseBody, requestCode: Int) {
                        GlobalScope.launch {
                            cardTransactionItem.Sync = true
                            repo.updateCardTransaction(cardTransactionItem)
                        }
                        return
                    }

                    override fun onError(errorMessage: String, requestCode: Int) {
                        Toast.makeText(App.context, errorMessage, Toast.LENGTH_LONG).show()


                    }
                })
            return Result.retry()

        }

        else {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            dueDate.set(Calendar.HOUR_OF_DAY, 22)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)
            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }

            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
            val dailyWorkRequest = OneTimeWorkRequestBuilder<RemoveTransactionWorker>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag("removeData").build()
            appContext.let {
                WorkManager.getInstance(it).enqueueUniqueWork(
                    "removeData",
                    ExistingWorkPolicy.REPLACE, dailyWorkRequest
                )
            }

            return Result.success()

        }


    }

}
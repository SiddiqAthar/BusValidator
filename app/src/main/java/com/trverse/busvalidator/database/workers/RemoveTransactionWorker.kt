package com.trverse.busvalidator.database.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trverse.busvalidator.database.db.AFCDatabase
import com.trverse.busvalidator.database.repository.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RemoveTransactionWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {

        val appContext = applicationContext
        // val notificationManager = makeStatusNotification("Syncing Data to Server", appContext)
        // query suspended from DB and then read values to sync to the server
        val dao = AFCDatabase.getDatabase(appContext).afcDatabaseDao
        val repo = Repository(dao)
        val qrTransaction = repo.getAllTransactionQR()
        val cardTravelTransaction = repo.getAllCardSyncTransaction()
        if (qrTransaction.isNotEmpty()) {
            var trans = qrTransaction[0]
            GlobalScope.launch {
                repo.deleteTransaction(trans.QRNumber)
            }
            return Result.retry()
        } else if (cardTravelTransaction.isNotEmpty()) {
            val cardTransaction = cardTravelTransaction[0]
            GlobalScope.launch {
                repo.deleteCardTransaction(cardTransaction.id)
            }
            return Result.retry()
        } else
            return Result.success()
    }

}
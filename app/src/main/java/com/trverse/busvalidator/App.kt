package com.trverse.busvalidator

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.trverse.busvalidator.activities.BaseActivity

class App : Application(), Configuration.Provider {
    private var baseActivity: BaseActivity? = null
    private var fareMatrixArray = arrayListOf<Array<String>>()


    companion object {
        var INSTANCE: App? = null
        var context: Context? = null
        var macMapp: Boolean = true

    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        context = this
    }


    fun setActivity(baseActivity: BaseActivity) {
        this.baseActivity = baseActivity
    }

    fun getBaseActivity(): BaseActivity? {
        return baseActivity
    }

    fun setFareMatrixArray(fareMatrixArray: ArrayList<Array<String>>) {
        this.fareMatrixArray = fareMatrixArray
    }

    fun getFareMatrixArray(): ArrayList<Array<String>> {
        return fareMatrixArray
    }

    @SuppressLint("RestrictedApi")
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(WorkerFactory.getDefaultWorkerFactory())
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
}
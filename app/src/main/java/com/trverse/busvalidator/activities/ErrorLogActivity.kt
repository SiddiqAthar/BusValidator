package com.trverse.busvalidator.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.trverse.busvalidator.R
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.enumirations.AppAction
import com.trverse.busvalidator.packages.QRViewModel
import com.trverse.busvalidator.utilities.SharePrefData
import com.trverse.busvalidator.utilities.Utils
import kotlinx.android.synthetic.main.activity_error_log.*
import kotlinx.android.synthetic.main.mtoolbar_layout.*
import kotlinx.android.synthetic.main.mtoolbar_layout.toolbar
import kotlinx.coroutines.launch
import java.util.*

class ErrorLogActivity : BaseActivity() {
    private var qrViewModel: QRViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_log)
        toolbar.title = "Error Occurred"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val th = intent.getSerializableExtra("error") as Throwable?
        Log.d("myException", "onCreate: ${th?.message} ")
        qrViewModel = ViewModelProvider(this).get(QRViewModel::class.java)
        errorTV.text = th?.stackTraceToString()
        reportErrorBT.setOnClickListener {
            try {
                lifecycleScope.launch {
                    qrViewModel?.saveActivityLog(
                        ActivityLogs(
                            0,
                            AppAction.APP_CRASH.appAction.Code,
                            "N/A",
                            SharePrefData(this@ErrorLogActivity)
                                .retrieveSyncObject()?.Validator_Code ?: "0",
                            getCurrentTime(),
                            "${th?.message} File Name is : ${th?.stackTrace?.get(0)?.fileName} " +
                                    ", Method Name is ${th?.stackTrace?.get(0)?.methodName}" +
                                    ", Line Number is ${th?.stackTrace?.get(0)?.lineNumber}"
                        )

                    )

                    qrViewModel?.saveActivityLog(
                        ActivityLogs(
                            0,
                            AppAction.APP_RESTART.appAction.Code,
                            "N/A",
                            SharePrefData(this@ErrorLogActivity)
                                .retrieveSyncObject()?.Validator_Code ?: "0",
                            getCurrentTime(),
                            "${AppAction.APP_RESTART.appAction.desc}"
                        )
                    )

                    Utils.restartApp(this@ErrorLogActivity)

                }
            } catch (e: Exception) {
                Log.d("ex-1", "onCreate: ${e.message}")
            }
        }
//        onClickReportError(th)

    }

    fun onClickReportError(th: Throwable?) {
        try {
            showErroDialog(
                "Report Error",  /*"Sorry for inconvenience we will fix this soon."*/
                Objects.requireNonNull(
                    th!!.message!!
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
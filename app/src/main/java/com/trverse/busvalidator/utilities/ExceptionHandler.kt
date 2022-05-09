package com.trverse.busvalidator.utilities

import android.content.Intent
import android.os.Build
import com.trverse.busvalidator.activities.BaseActivity
import com.trverse.busvalidator.activities.ErrorLogActivity


class ExceptionHandler(private val aksaActivity: BaseActivity) : Thread.UncaughtExceptionHandler {
    private val LINE_SEPARATOR = "\n"
    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val intent = Intent(aksaActivity, ErrorLogActivity::class.java)
        intent.putExtra("error", exception)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        aksaActivity.startActivity(intent)
    }
}
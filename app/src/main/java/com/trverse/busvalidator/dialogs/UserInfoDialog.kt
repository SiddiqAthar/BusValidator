package com.trverse.busvalidator.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.trverse.busvalidator.R
import com.trverse.busvalidator.mycallbacks.CustomGenericCallback
import kotlinx.android.synthetic.main.user_info_dialog.*

class UserInfoDialog(context: Context, callback: CustomGenericCallback?) :
    Dialog(context, R.style.ErrorDialog) {
    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        setContentView(R.layout.user_info_dialog)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window!!.attributes)
        lp.width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = lp
        window!!.setBackgroundDrawableResource(R.color.transparent)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        submitBtn.setOnClickListener {
            if (validateInput()) {
                callback?.genericCallback(
                    1,
                    etEmail.text.toString() + "," + etPassword.text.toString()
                )
                dismiss()
            }
        }
        hideSystemUI()

    }

    private fun validateInput(): Boolean {

        if (etEmail.text.toString().isEmpty()) {
            etEmail.error = "Please enter email address"
            emailET.requestFocus()
            return false
        } else if (etPassword.text.toString().isEmpty()) {
            etPassword.error = "Please enter password"
            etPassword.requestFocus()
            return false
        } else {
            etEmail.error = null
            etPassword.error = null
            return true
        }

    }

    private fun hideSystemUI() {
        val decorView = window?.decorView
        decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
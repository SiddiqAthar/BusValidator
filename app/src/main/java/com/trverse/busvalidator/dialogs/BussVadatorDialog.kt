package com.trverse.busvalidator.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.trverse.busvalidator.R
import kotlinx.android.synthetic.main.afc_dialog.*

class BussVadatorDialog(context: Context) : Dialog(context) {
    init {

        setContentView(R.layout.afc_dialog)
        setCancelable(false)
        setOnCancelListener(null)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        animationView.playAnimation()
    }


    override fun dismiss() {
        super.dismiss()
        animationView.cancelAnimation()
    }
}
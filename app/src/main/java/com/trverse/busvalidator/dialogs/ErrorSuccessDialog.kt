package com.trverse.busvalidator.dialogs

import android.app.Dialog
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.trverse.busvalidator.R
import kotlinx.android.synthetic.main.success_dialog_layout.*

class ErrorSuccessDialog(
    context: Context,
    title: String
) : Dialog(context, R.style.ErrorDialog),
    View.OnClickListener {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        setContentView(R.layout.success_dialog_layout)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = lp
        window!!.setBackgroundDrawableResource(R.color.transparent)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        lbl_success_title.setText(title)
        hideSystemUI()
        cancelBtn.setOnClickListener {
            dismiss()
        }

    }

    override fun onClick(view: View?) {
    }

    override fun dismiss() {
        super.dismiss()

    }

    fun setData(data: String) {
        lbl_success_desc.setText(data)

    }

    fun setSuccessTune() {
        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen1.startTone(ToneGenerator.TONE_SUP_CONFIRM, 1000)
    }

    fun setTitle(data: String) {
        lbl_success_title.setText(data)

    }

    fun playtune(tone: Int) {
        val mediaPlayer = MediaPlayer.create(context, tone)
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
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

    fun setErrorImage() {
        phone_image.setImageResource(R.drawable.ic_expired)
    }

    fun setSuccessImage() {
        phone_image.setImageResource(R.drawable.ic_success)
    }

    fun showCancelBtn() {
        cancelBtn.visibility = View.VISIBLE
    }
}
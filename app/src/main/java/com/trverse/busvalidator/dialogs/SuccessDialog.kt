package com.trverse.busvalidator.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.trverse.busvalidator.R
import kotlinx.android.synthetic.main.dialog_success.*
import android.media.ToneGenerator

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


public class SuccessDialog(
    context: Context,
    title: String,
    msg: String,
    date: String,
    balance: String,
    error: Boolean
) : Dialog(context, R.style.SuccessDialog) {


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.dialog_success)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = lp
        window!!.setBackgroundDrawableResource(R.color.transparent)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        successTxt.text = title
        checkInStatus.text = "Status : ${msg}"
        balanceTxt.text = "Balance : ${balance}"
        timeLbl.text = date
        if (error) {
            try {
                img.setImageResource(R.drawable.ic_error)
//                 playtune(R.raw.error_tune)
            }catch (e:Exception){}
        } else {
            try {
                img.setImageResource(R.drawable.ic_success)
                val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                toneGen1.startTone(ToneGenerator.TONE_SUP_CONFIRM, 1000)
            } catch (e: Exception) {

            }
//            playtune(R.raw.success_notification)

        }
        //todo hideUI
        hideSystemUI()

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

        /*   if(window!=null) {
               WindowCompat.setDecorFitsSystemWindows(window!!, false)
               WindowInsetsControllerCompat(window!!, successTxt).let { controller ->
                   controller.hide(WindowInsetsCompat.Type.systemBars())
                   controller.systemBarsBehavior =
                       WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
               }
           }*/
    }

    fun playtune(tone: Int) {
        val mediaPlayer = MediaPlayer.create(context, tone)
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }


}

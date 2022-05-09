//package com.trverse.busvalidator.dialogs
//
//import android.app.Dialog
//import android.content.Context
//import android.view.Window
//import android.view.WindowManager
//import androidx.annotation.Nullable
//import com.tonyodev.fetch2.Download
//import com.trverse.busvalidator.R
//import com.trverse.busvalidator.utilities.Utils
////import kotlinx.android.synthetic.main.dialog_download_file_layout.*
//
//class DownloadFileDialog(context: Context) : Dialog(context) {
//    init {
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setCancelable(true)
////        setContentView(R.layout.dialog_download_file_layout)
//
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(window!!.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//        window!!.attributes = lp
//        window!!.setBackgroundDrawableResource(R.color.transparent)
//        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//    }
//
//    fun setFileTitle(title: String) {
//        titleTextView.text = title
//    }
//
//    fun setRemainingTime(
//        etaInMilliseconds: Long
//    ) {
//        if (etaInMilliseconds == -1L) {
//            remaining_TextView.text = ""
//        } else {
//            remaining_TextView.text = Utils.getETAString(context, etaInMilliseconds)
//        }
//    }
//
//    fun setProgress(
//        @Nullable download: Download,
//        etaInMilliseconds: Long,
//        downloadedBytesPerSecond: Long
//    ) {
//        if (download.progress == -1)
//            progressBar.progress = 0
//        progressBar.progress = download.progress
//        progress_TextView.text = download.progress.toString() + "%"
//
//
//
//    }
//}
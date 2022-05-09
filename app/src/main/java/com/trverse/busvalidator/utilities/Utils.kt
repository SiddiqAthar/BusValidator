package com.trverse.busvalidator.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.getSystemService
import com.trverse.busvalidator.R
import com.trverse.busvalidator.activities.MainActivity
import java.io.File
import java.io.IOException
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    fun getMimeType(context: Context, uri: Uri): String {
        val cR = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        var type = mime.getExtensionFromMimeType(cR.getType(uri))
        if (type == null) {
            type = "*/*"
        }
        return type
    }

    fun deleteFileAndContents(file: File) {
        if (file.exists()) {
            if (file.isDirectory) {
                val contents = file.listFiles()
                if (contents != null) {
                    for (content in contents) {
                        deleteFileAndContents(content)
                    }
                }
            }
            file.delete()
        }
    }

    @SuppressLint("StringFormatInvalid")
    fun getETAString(context: Context, etaInMilliSeconds: Long): String {
        if (etaInMilliSeconds < 0) {
            return ""
        }
        var seconds = (etaInMilliSeconds / 1000).toInt()
        val hours = (seconds / 3600).toLong()
        seconds -= (hours * 3600).toInt()
        val minutes = (seconds / 60).toLong()
        seconds -= (minutes * 60).toInt()
        return if (hours > 0) {
            context.getString(R.string.download_eta_hrs, hours, minutes, seconds)
        } else if (minutes > 0) {
            context.getString(R.string.download_min, minutes, seconds)
        } else {
            context.getString(R.string.download_eta_sec, seconds)
        }
    }

    @SuppressLint("StringFormatInvalid")
    fun getDownloadSpeedString(context: Context, downloadedBytesPerSecond: Long): String {
        if (downloadedBytesPerSecond < 0) {
            return ""
        }
        val kb = downloadedBytesPerSecond.toDouble() / 1000.toDouble()
        val mb = kb / 1000.toDouble()
        val decimalFormat = DecimalFormat(".##")
        return if (mb >= 1) {
            context.getString(R.string.download_speed_mb, decimalFormat.format(mb))
        } else if (kb >= 1) {
            context.getString(R.string.download_speed_kb, decimalFormat.format(kb))
        } else {
            context.getString(R.string.download_speed_bytes, downloadedBytesPerSecond)
        }
    }

    fun createFile(filePath: String?): File {
        val file = File(filePath)
        if (!file.exists()) {
            val parent = file.parentFile
            if (!parent.exists()) {
                parent.mkdirs()
            }
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun getProgress(downloaded: Long, total: Long): Int {
        return if (total < 1) {
            -1
        } else if (downloaded < 1) {
            0
        } else if (downloaded >= total) {
            100
        } else {
            (downloaded.toDouble() / total.toDouble() * 100).toInt()
        }
    }

    fun currentDateTime(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        return sdf.format(Date())
    }

    fun getWifiIp(context: Context): String? {
        return context.getSystemService<WifiManager>().let {
            when {
                it == null -> "No wifi available"
                !it.isWifiEnabled -> "Wifi is disabled"
                it.connectionInfo == null -> "Wifi not connected"
                else -> {
                    val ip = it.connectionInfo.ipAddress
                    ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "." + (ip shr 24 and 0xFF))
                }
            }
        }
    }

    fun getMacAddr(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.getName().equals("wlan0", ignoreCase = true)) continue

                val macBytes = nif.getHardwareAddress() ?: return ""

                val res1 = StringBuilder()
                for (b in macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b))
                }

                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
            Log.d("Mac: ", ex.message.toString())
        }

        return "02:00:00:00:00:00"
    }

    fun restartApp(context: Context) {
        if (context is Activity) {
            context.finishAffinity()
        }
        val mStartActivity = Intent(context, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(
            context,
            mPendingIntentId,
            mStartActivity,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
        System.exit(0)
    }
}
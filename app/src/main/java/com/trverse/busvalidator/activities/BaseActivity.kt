package com.trverse.busvalidator.activities

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.location.*
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.opencsv.CSVReader
import com.trverse.busvalidator.App
import com.trverse.busvalidator.App.Companion.context
import com.trverse.busvalidator.R
import com.trverse.busvalidator.dialogs.ErrorSuccessDialog
import com.trverse.busvalidator.models.farePolicyModels.PolicyRulesResponseModel
import com.trverse.busvalidator.utilities.ExceptionHandler
import com.trverse.busvalidator.utilities.FileDataUtility
import com.trverse.busvalidator.utilities.SharePrefData
import org.xml.sax.Locator
import java.io.FileReader
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*


open class BaseActivity : AppCompatActivity() {

    var bussValidatorApp: App? = null
    var locator: Locator? = null
    var lat = ""
    var lng = ""
    var src = ""
    val gcd = Geocoder(context)
    var locationData: Location? = null
    var mCurrentFragment: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bussValidatorApp = application as App?;
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

    }

    override fun onResume() {
        super.onResume()
        bussValidatorApp?.setActivity(this)

    }


    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    open fun openActivity(calledActivity: Class<*>?, @Nullable bundle: Bundle?) {
        val myIntent = Intent(this, calledActivity)
        if (bundle != null) myIntent.putExtras(bundle)
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this.startActivity(myIntent)
    }


    fun closeKeyboard() {
        val inputManager =
            applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager != null && this.currentFocus != null) inputManager.hideSoftInputFromWindow(
            this.currentFocus!!.windowToken,
            InputMethodManager.RESULT_UNCHANGED_SHOWN
        )
    }


    open fun onDismiss(param: Any?) {
//
//        if (param != null && param is GenericResponseModel<*>) {
//            if (param.result is TempBookingResponseModel) {
//                onBackPressed()
//            } else if (param.error!=null && param.error.equals("crash")) {
//                finishAndRemoveTask()
//            }
//        }else if(param != null && param is String){
//            if(param.equals("backToDashboard")){
//                onBackPressed()
//            }
//        }
    }

    fun showErroDialog(title: String, msg: String) {

        val dialog = ErrorSuccessDialog(this, title)
        if (!msg.isNullOrEmpty()) {
            dialog.setData(msg)
        }
        dialog.show()
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        return sdf.format(Date())
    }


    fun showMacAddressErrorDialog(response: String?) {
        App.macMapp = false
        val dialog = ErrorSuccessDialog(this, "MAC Address not mapped")
        dialog.setData(response ?: "MAC address not mapped, kindly contact with administrator")
        dialog.setErrorImage()
        dialog.playtune(R.raw.error_tune)
        dialog.showCancelBtn()
        dialog.show()
    }

    fun getLocationName(latitude: Double, longitude: Double): String {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())

        if (latitude != null && longitude != null) {

            try {
                addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses.size == 0) return ""
                val address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                val postalCode = addresses[0].postalCode
                val knownName = addresses[0].featureName
                return address
            } catch (e: Exception) {
                Log.d("err", "getLocationName: ${e.message}")
            }
            return ""
        } else {
            showToast("Enable GPS")
            return "";
        }
    }


    fun copyText(text: String) {
        val myClipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label", text)
        myClipboard.setPrimaryClip(myClip)
    }

    fun checkTimeLess(timeString: String): Boolean {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")
        val date: Date = dateFormat.parse(timeString)
        val current: Date = dateFormat.parse(dateFormat.format(Date()))
        return current.before(date)


    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val location2 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        val location3 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        val longitude: Double? = location?.longitude
        val latitude: Double? = location?.latitude

        return location
//        val mLocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
//        val providers: List<String> = mLocationManager.getProviders(true)
//        var bestLocation: Location? = null
//        for (provider in providers) {
//            val l: Location = mLocationManager.getLastKnownLocation(provider) ?: continue
//            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
//                // Found best last known location: %s", l);
//                bestLocation = l
//            }
//        }
//        return bestLocation
    }


    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationData = location
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    }

    fun finishAllFragmentsExceptFirstOne() {
        var manager = supportFragmentManager
        if (manager.backStackEntryCount > 1) {
            for (i in 0 until manager.backStackEntryCount - 1) {
                manager.popBackStack()
            }

            val backEntry = supportFragmentManager.getBackStackEntryAt(0)
            val tag = backEntry.name
            mCurrentFragment = supportFragmentManager.findFragmentByTag(tag)

            showHideFragment(mCurrentFragment!!)
        }
    }

    fun indexOf(adapter: Adapter, value: String): Int {
        var index = 0
        val count = adapter.count
        while (index < count) {
            when (adapter.getItem(index)) {
                is String -> {
                    val selectedItem = adapter.getItem(index) as String
                    if (selectedItem.equals(value, true) || selectedItem.equals(
                            value, true
                        )
                    ) {
                        return index
                    }
                }
            }
            ++index
        }
        return -1
    }


    fun showHideFragment(fragment: Fragment) {
        val manager = supportFragmentManager

        val t = manager.beginTransaction()
        for (i in 0 until manager.fragments.size) {
            val f = manager.fragments[i]
            t.hide(f)
        }
        t.show(fragment).commit()
    }

    fun replaceFragment(fragment: Fragment) {
        val frag = Fragment()

        if (mCurrentFragment != null && mCurrentFragment!!::class.java.equals(
                fragment::class.java
            )
        ) {

            return
        }
        val manager = supportFragmentManager
        mCurrentFragment = fragment

        try {

            val t = manager.beginTransaction()
            val currentFrag = manager.findFragmentByTag(fragment::class.java.simpleName)

            if (fragment.isAdded) {
                //                manager.popBackStackImmediate(fragment::class.java.simpleName, 0)
                t.remove(frag).commit();
                return
            }

            if (currentFrag != null && currentFrag::class.java.simpleName.equals(
                    fragment::class.java.simpleName
                )
            ) {
                showHideFragment(currentFrag)
                manager.popBackStackImmediate(mCurrentFragment!!::class.java.simpleName, 1)
            }
            t.add(R.id.container, fragment, fragment::class.java.simpleName)
                .addToBackStack(fragment::class.java.simpleName).commit()


        } catch (e: Exception) {
//            LogsUtils.showDLogs("ex", "repalceFrag funcation Exception in base Activity ")
            val t = manager.beginTransaction()
            t.add(R.id.container, fragment, fragment::class.java.simpleName)
                .addToBackStack(fragment::class.java.simpleName).commit()


        }
        showHideFragment(fragment)
    }

    override fun onBackPressed() {
        performBackStackManagement()
    }

    fun performBackStackManagement() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount == 1) {
            finish()
        } else {
            val index = supportFragmentManager.backStackEntryCount - 2
            if (index >= 0) {
                val backEntry = supportFragmentManager.getBackStackEntryAt(index)
                val tag = backEntry.name
                mCurrentFragment = supportFragmentManager.findFragmentByTag(tag)

                showHideFragment(mCurrentFragment!!)
            }
            super.onBackPressed()
        }
    }

      fun hideSystemUI(view: View) {


    /*// Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        *//*   val decorView = window.decorView
           decorView.systemUiVisibility =
               (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                       // content doesn't resize when the system bars hide and show.
                       or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                       or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                       or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                       or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                       or View.SYSTEM_UI_FLAG_FULLSCREEN)*//*

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, view).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }*/


    }

    fun hideActionbarAndStatusbar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val decorView: View = window.decorView
        val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        val actionBar: ActionBar? = actionBar
        actionBar?.hide()


    }

    fun showSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)


        /*
        WindowCompat.setDecorFitsSystemWindows(window, true)
          WindowInsetsControllerCompat(window, dateTime).show(WindowInsetsCompat.Type.systemBars())*/
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

                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
            Log.d("Mac: ", ex.message.toString())
        }

        return "02:00:00:00:00:00"
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


    fun getDistanceBetweenTwoStations(startStation: String, endStation: String): Float {

        /**
         * @param startStation is start station where user will enter in bus
         * @param endStation is end station where user leave the bus
         * this funcation will give you total Kms user travelled according to fareMatrixTable
         */
        try {
            if (bussValidatorApp?.getFareMatrixArray().isNullOrEmpty())
                getCVSFile()
            val rowIndex = bussValidatorApp?.getFareMatrixArray()?.get(0)?.indexOf(startStation)
            val columIndex = bussValidatorApp?.getFareMatrixArray()?.get(0)?.indexOf(endStation)
            return bussValidatorApp?.getFareMatrixArray()?.get(rowIndex!!)?.get(columIndex!!)
                ?.toFloat()!!
        } catch (e: java.lang.Exception) {
            return -1003.0f;
        }
    }


    fun getCVSFile() {
        /**
         * this function is used for load fareMatrix Table into device cache
         */
        try {

            val reader =
                CSVReader(FileReader(FileDataUtility.getSaveDir(this) + "FeeMap.csv"))
            var nextLine: Array<String>

            while (reader.readNext().also { nextLine = it } != null) {
                if (!nextLine.get(0).isEmpty())
                    bussValidatorApp?.getFareMatrixArray()?.add(nextLine)
            }

            Log.d("TAG", "getCVSFile: ok")
        } catch (e: Exception) {
            Log.d("TAG", "getCVSFile: ${e.message}")
//            getDistanceBetweenTwoStations("10|1", "6|1")

        }

    }

    /**
     * @param startStation is stating station which is user start travelling
     * @param endStation is ending station where is user destination
     * this function will give you amount which will be deducted from user card according
     * to user farePolicyRules and FareMatrix Table
     */
    fun getCalculatedAmoutFromFareTable(
        startStation: String,
        endStation: String
    ): PolicyRulesResponseModel {
        val policyRules = SharePrefData(this).retrieveFarePolicyObject()?.policyRules

        try {

            val kms = getDistanceBetweenTwoStations(startStation, endStation)
            for (i in policyRules!!) {
                if (kms <= i.MaxKilometer.toFloat()) {
                    return i
                }
            }
            return policyRules.get(0)
        } catch (e: Exception) {
            return policyRules?.get(0)!!;
        }


    }
}






package com.trverse.busvalidator.utilities

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

@TargetApi(Build.VERSION_CODES.M)
object PermissionUtils {
    // TODO: handle never ask use case
    // https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
    // region PERMISSION_CONSTANTS
    const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1199

    // calendar
    const val PERMISSION_READ_CALENDAR = 1
    const val PERMISSION_WRITE_CALENDAR = 2

    // camera
    const val PERMISSION_CAMERA = 3

    // contacts
    const val PERMISSION_GET_ACCOUNTS = 4
    const val PERMISSION_READ_CONTACTS = 5
    const val PERMISSION_WRITE_CONTACTS = 6

    // location
    const val PERMISSION_LOCATION = 7

    // microphone
    const val PERMISSION_RECORD_AUDIO = 8

    // phone
    const val PERMISSION_READ_PHONE_STATE = 9
    const val PERMISSION_CALL_PHONE = 10
    const val PERMISSION_READ_CALL_LOG = 11
    const val PERMISSION_WRITE_CALL_LOG = 12
    const val PERMISSION_ADD_VOICE_EMAIL = 13
    const val PERMISSION_USE_SIP = 14
    const val PERMISSION_PROCESS_OUTGOING_CALLS = 15

    // sensors
    const val PERMISSION_BODY_SENSORS = 16

    // sms
    const val PERMISSION_SEND_SMS = 17
    const val PERMISSION_RECEIVE_SMS = 18
    const val PERMISSION_READ_SMS = 19
    const val PERMISSION_RECEIVE_WAP_PUSH = 20
    const val PERMISSION_RECEIVE_MMS = 21

    // storage
    const val PERMISSION_READ_EXTERNAL_STORAGE = 22
    const val PERMISSION_EXTERNAL_STORAGE = 23

    // endregion
    fun hasPermissionGranted(context: Context?, permissions: Array<String?>): Boolean {
        var hasGranted = false
        for (permission in permissions) {
            hasGranted = ActivityCompat.checkSelfPermission(
                context!!,
                permission!!
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasGranted) return false
        }
        return hasGranted
    }

    fun shouldShowPermissionRationale(activity: Activity?, permissions: Array<String?>): Boolean {
        var shouldShowRequestPermissionRationale = false
        for (permission in permissions) {
            shouldShowRequestPermissionRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!, permission!!
                )
            if (!shouldShowRequestPermissionRationale) return false
        }
        return shouldShowRequestPermissionRationale
    }

    fun requestPermission(`object`: Any, permissionId: Int, permission: Array<String?>?) {
        if (`object` is Activity) ActivityCompat.requestPermissions(
            `object`,
            permission!!,
            permissionId
        ) else {
            val fragment = `object` as Fragment
            if (fragment.isAdded && fragment.activity != null) fragment.requestPermissions(
                permission!!,
                permissionId
            )
        }
    }

    fun verifyPermission(grantResults: IntArray): Boolean {
        // at least one result must be checked.
        if (grantResults.size < 1) return false
        // verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    fun requestPermission(context: Context?, permission: String, requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(
                context!!, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) return true else {
            if (context is AppCompatActivity) {
                context.requestPermissions(arrayOf(permission), requestCode)
            }
        }
        return false
    }

    /**
     * Check and ask for disabled permissions
     * @param activity  Activity calling the method
     * @param permissions   permissions array needed to be checked
     * @param requestCode   request code associated with the request call
     * @return  flag specifying permission are enabled or not
     */
    fun checkAndRequestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        val requiredPerm: MutableList<String> = ArrayList()
        for (permission in permissions) if (ContextCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) requiredPerm.add(permission)
        if (requiredPerm.size == 0) return true
        var mPermission: Array<String?>? = arrayOfNulls(requiredPerm.size)
        mPermission = requiredPerm.toTypedArray()
        if (mPermission != null) activity.requestPermissions(mPermission, requestCode)
        return false
    }

    fun hasLocationPermissionGranted(context: Context?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions(activity: Activity?, requestCode: Int) {
        ActivityCompat.requestPermissions(activity!!, locationPermissions, requestCode)
    }

    //    public static boolean checkPlayServices(Context context) {
    val locationPermissions: Array<String>
        get() = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    //        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
    //        int resultCode = api.isGooglePlayServicesAvailable(context);
    //        if (resultCode != ConnectionResult.SUCCESS) {
    //            if (api.isUserResolvableError(resultCode))
    //                api.getErrorDialog(((Activity) context), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
    //            else {
    //                Toast.makeText(context, "This device is not supported.", Toast.LENGTH_LONG).show();
    //                ((Activity) context).finish();
    //            }
    //            return false;
    //        }
    //        return true;
    //    }
}
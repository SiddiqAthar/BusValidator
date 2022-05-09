package com.trverse.busvalidator.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.trverse.busvalidator.R
import com.trverse.busvalidator.fragments.busValidatorSettings.SyncFragment
import com.trverse.busvalidator.fragments.logs.ShowLogsFragment
import com.trverse.busvalidator.fragments.parametersFragment.ParamtersFragment

class GenericActivity : BaseActivity() {
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        hideActionbarAndStatusbar()
        setContentView(R.layout.activity_generic_activty)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setTitle("Sync Settings")
        if (intent.extras != null) {
            val bundle = intent.extras
            if (bundle?.getString("goto") == "SyncData") {
                setTitle("Sync Settings")
                replaceFragment(SyncFragment())
            } else if (bundle?.getString("goto") == "syncParamter") {
                setTitle("Sync Parameters")
                replaceFragment(ParamtersFragment())
            } else if (bundle?.getString("goto") == "activityLogs") {
                setTitle("Activity Logs")
                replaceFragment(ShowLogsFragment())
            }
        } else {
            replaceFragment(SyncFragment())
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
//            hideSystemUI(container)
        }
    }
}
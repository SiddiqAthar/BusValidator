package com.trverse.busvalidator.fragments.parametersFragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trverse.busvalidator.BuildConfig
import com.trverse.busvalidator.R
import com.trverse.busvalidator.activities.BaseActivity
import com.trverse.busvalidator.dialogs.ErrorSuccessDialog
import com.trverse.busvalidator.network.URL
import com.trverse.busvalidator.network.URLType
import com.trverse.busvalidator.utilities.SharePrefData
import kotlinx.android.synthetic.main.fragment_paramters.*

class ParamtersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paramters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        try {
            synUrlEd.setText(SharePrefData(requireContext()).getSyncUrl())
            synUrlEd.setSelection(synUrlEd.length())
            ipAddressEd.setText((requireActivity() as BaseActivity).getWifiIp(requireContext()))
            encryptionEd.setText(SharePrefData(requireContext()).getRSAUrl())
            softwareVersionEd.setText(BuildConfig.VERSION_NAME)
            syncBtn.setOnClickListener {
                if (!synUrlEd.text.toString().substring(synUrlEd.text.toString().length - 1)
                        .equals("/")
                ) {
                    synUrlEd.setText(synUrlEd.text.toString() + "/")
                }
                SharePrefData(requireContext()).saveSyncUrl(synUrlEd.text.toString())
                URLType.DATA_SYNC_SERVICE.baseURL = URL(synUrlEd.text.toString())
                val dialog = ErrorSuccessDialog(requireContext(), "Sync Parameters")
                dialog.setData("Your Sync Parameters has been saved.")
                dialog.setSuccessImage()
                dialog.setSuccessTune()
                dialog.showCancelBtn()
                dialog.show()
            }
        } catch (e: Exception) {

        }
    }
}
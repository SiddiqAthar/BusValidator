package com.trverse.busvalidator.fragments.busValidatorSettings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.trverse.busvalidator.R
import com.trverse.busvalidator.activities.BaseActivity
import com.trverse.busvalidator.encryption.RSAEncryption
import com.trverse.busvalidator.enumirations.APIRequestEnums
import com.trverse.busvalidator.models.GenericResponseModel
import com.trverse.busvalidator.models.cardsTypes.CardTypeModel
import com.trverse.busvalidator.models.encryptionModels.EncryptionResponse
import com.trverse.busvalidator.models.farePolicyModels.FarePolicyResponseModel
import com.trverse.busvalidator.models.settings.DeviceDetails
import com.trverse.busvalidator.models.settings.SyncResponse
import com.trverse.busvalidator.network.APIManager
import com.trverse.busvalidator.network.CallbackGeneric
import com.trverse.busvalidator.network.URLType
import com.trverse.busvalidator.utilities.DownloadFileUtility
import com.trverse.busvalidator.utilities.SharePrefData
import com.trverse.busvalidator.utilities.SharePrefData.Companion.SYNC_TIME
import com.trverse.busvalidator.utilities.logsEnable
import kotlinx.android.synthetic.main.fragment_sync.*
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*

class SyncFragment : Fragment(), CallbackGeneric<GenericResponseModel<Any>> {
    var fareTableOldVerser = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sync, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        val syncData = SharePrefData(requireContext()).retrieveSyncObject()
        syncData?.let {
            etOrganization.setText(it.Organization_Name)
            validatorCodeEd.setText(it.Validator_Code)
            validatorSDKSupportEd.setText(it.Validator_SDKSupport)
            etMobileNo.setText(it.Organization_Mobile)
            etMacAddress.setText((requireActivity() as BaseActivity).getMacAddr())
            etSkype.setText(it.Organization_Skype)
            etStation.setText(it.Station_Name)
            etDescription.setText("" + it.Validator_Description)
            etStationCode.setText(it.Station_StationCode)
            validatorDirectionEd.setText(it.Validator_Direction)
            orgTelephoneEd.setText(it.Organization_Telephone)
            orgFaxEd.setText(it.Organization_Fax)
            orgAddressEd.setText(it.Organization_Address)
            etMaxTopUp.setText(it.Station_MaxSaleLimit.toString())
            farePolicyTV.setText("Fee Map Version\n ${it.FeeMap_Version}")
            fareTableOldVerser = it.FeeMap_Version
            val myDate =
                SharePrefData(requireContext()).getLongValue(SYNC_TIME)?.let { it1 -> Date(it1) }
            if (myDate != null) {
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                lastSycnTV.text = "Last sync at ${formatter.format(myDate)}"
            } else lastSycnTV.text = "Last sync at -"
        }

        syncBtn.setOnClickListener {
            APIManager().getSyncSettings(
                /* "E4:08:E7:EA:96:E9"*/ (requireActivity() as BaseActivity).getMacAddr(),
                this@SyncFragment,
                APIRequestEnums.SYNC_PARAMETERS_SETTINGS.requestCode.requestCode
            )

            /*   APIManager().getRSAKey(
                   "1001",
                   "test@mail.com",
                   "123",
                   "123",
                   this,
                   APIRequestEnums.RSA_ENCRYPTION_KEY.requestCode.requestCode
               )*/
        }
    }

    override fun onError(errorMessage: String, requestCode: Int) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        (requireActivity() as BaseActivity).showMacAddressErrorDialog(errorMessage)

    }

    override fun onResult(response: GenericResponseModel<Any>, requestCode: Int) {
        if (requestCode == APIRequestEnums.SYNC_PARAMETERS_SETTINGS.requestCode.requestCode) {
            if (response.success && response.result is SyncResponse) {
                val syncResponse = (response.result as SyncResponse).deviceDetails
                SharePrefData(requireContext()).saveSyncObject(syncResponse)
                val date = Date(System.currentTimeMillis())
                val millis = date.time
                SharePrefData(requireContext()).saveLong(SYNC_TIME, millis)
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                lastSycnTV.text = "Last sync at ${formatter.format(date)}"
                etOrganization.setText(syncResponse.Organization_Name)
                validatorCodeEd.setText(syncResponse.Validator_Code)
                validatorSDKSupportEd.setText(syncResponse.Validator_SDKSupport)
                etMobileNo.setText(syncResponse.Organization_Mobile)
                etMacAddress.setText((requireActivity() as BaseActivity).getMacAddr())
                etSkype.setText(syncResponse.Organization_Skype)
                etStation.setText(syncResponse.Station_Name)
                etDescription.setText("" + syncResponse.Validator_Description)
                etStationCode.setText(syncResponse.Station_StationCode)
                validatorDirectionEd.setText(syncResponse.Validator_Direction)
                orgTelephoneEd.setText(syncResponse.Organization_Telephone)
                orgFaxEd.setText(syncResponse.Organization_Fax)
                orgAddressEd.setText(syncResponse.Organization_Address)
                etMaxTopUp.setText("" + syncResponse.Station_MaxSaleLimit)
                farePolicyTV.setText("Fee Map Version\n ${syncResponse.FeeMap_Version}")
                logsEnable = syncResponse.Valiodator_IsLogging
                retrieveFarePolicy(syncResponse.Organization_OrganizationID)


            } else {
                (requireActivity() as BaseActivity).showMacAddressErrorDialog(response.message)
            }
        } else if (requestCode == APIRequestEnums.RSA_ENCRYPTION_KEY.requestCode.requestCode) {
            if (response.success) {
                val encryptionResponse = response.result as EncryptionResponse
                val rsaEncryption = RSAEncryption(requireContext())
                rsaEncryption.loadPrivateKeyFromXMl(encryptionResponse.privateKey)
                rsaEncryption.decrypt("")
            }

        } else if (requestCode == APIRequestEnums.FARE_POLICY.requestCode.requestCode) {
            if (response.success) {
                // check applicable which policy is applicable true, then save that policy
                val farePolicyList = response.result as ArrayList<FarePolicyResponseModel>
                for (i in 0 until farePolicyList.size) {
                    if (farePolicyList.get(i).isApplicable) {
                        SharePrefData(requireContext()).saveFarePolicyObject(farePolicyList.get(i))
                        break
                    }
                }
                APIManager().getCardsTypes(
                    activity?.let { SharePrefData(it).retrieveSyncObject() }!!.Organization_OrganizationID,
                    this,
                    APIRequestEnums.CARD_TYPES.requestCode.requestCode
                )
            } else {
                Log.d("Response", response.message)
            }
        } else if (requestCode == APIRequestEnums.CARD_TYPES.requestCode.requestCode) {
            if (response.success) {
                val cardsArray = response.result as ArrayList<*>
                activity?.let { SharePrefData(it).saveCardTypes(cardsArray as ArrayList<CardTypeModel>) }

                if (SharePrefData(requireContext()).retrieveSyncObject()?.FeeMap_Version != fareTableOldVerser) {
                    DownloadFileUtility(requireActivity() as BaseActivity, "").downloadFile()
                }
            }
        }
    }

    private fun retrieveFarePolicy(organizationOrganizationid: String) {
        APIManager().getFarePolicy(
            organizationOrganizationid,
            this,
            APIRequestEnums.FARE_POLICY.requestCode.requestCode
        )
    }

}
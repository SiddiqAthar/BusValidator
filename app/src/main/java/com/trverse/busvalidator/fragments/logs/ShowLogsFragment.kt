package com.trverse.busvalidator.fragments.logs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.trverse.busvalidator.R
import com.trverse.busvalidator.adapters.ActivityGenericAdapter
import com.trverse.busvalidator.database.logs.ActivityLogs
import com.trverse.busvalidator.packages.QRViewModel
import kotlinx.android.synthetic.main.fragment_show_logs.*
import kotlinx.coroutines.launch
import java.util.*

class ShowLogsFragment : Fragment() {
    private val TAG: String? = ShowLogsFragment::class.simpleName
    val qrViewModel: QRViewModel by viewModels<QRViewModel>()
    val adapter: ActivityGenericAdapter = ActivityGenericAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            logsRecyclerView.adapter = adapter
            lifecycleScope.launch {
                adapter.submitList(qrViewModel.getAllActivityLogs())
            }
        } catch (e: Exception) {
            Log.e(TAG, "onViewCreated: ${e.message} ")
        }
    }
}
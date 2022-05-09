package com.trverse.busvalidator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trverse.busvalidator.R
import com.trverse.busvalidator.database.logs.ActivityLogs

class ActivityGenericAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(MyDiffUtils()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.logs_item_layout, parent, false)
        return MyLogsHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyLogsHolder && getItem(position) is ActivityLogs) {
            holder.bindView(getItem(position) as ActivityLogs)
        }
    }

    class MyDiffUtils : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is ActivityLogs && newItem is ActivityLogs) {
                return oldItem.id == newItem.id && oldItem.DateTime == newItem.DateTime
            }
            return false
        }

    }

    class MyLogsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityLogsTv = itemView.findViewById<TextView>(R.id.activityLogsTv)
        val numberTv = itemView.findViewById<TextView>(R.id.numberTv)
        val deviceCodeTv = itemView.findViewById<TextView>(R.id.deviceCodeTv)
        val dateTimeTv = itemView.findViewById<TextView>(R.id.dateTimeTv)
        val descriptionsTv = itemView.findViewById<TextView>(R.id.descriptionsTv)

        fun bindView(logs: ActivityLogs) {
            activityLogsTv.text = logs.ActivityLog
            numberTv.text = logs.Number
            deviceCodeTv.text = logs.DeviceCode
            dateTimeTv.text = logs.DateTime
            descriptionsTv.text = logs.Description
        }
    }
}
package com.example.spandana.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spandana.R
import com.example.spandana.models.RecentActivity
import java.text.SimpleDateFormat
import java.util.*

class RecentActivityAdapter : ListAdapter<RecentActivity, RecentActivityAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = getItem(position)
        holder.bind(activity)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvActivityTitle)
        private val tvDescription: TextView = view.findViewById(R.id.tvActivityDescription)
        private val tvTime: TextView = view.findViewById(R.id.tvActivityTime)
        private val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

        fun bind(activity: RecentActivity) {
            tvTitle.text = activity.title
            tvDescription.text = activity.description
            tvTime.text = sdf.format(Date(activity.timestamp))
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecentActivity>() {
            override fun areItemsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
                return oldItem.timestamp == newItem.timestamp && oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
                return oldItem == newItem
            }
        }
    }
}

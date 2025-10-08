package com.example.spandana.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spandana.R
import com.example.spandana.models.RecentActivity

class RecentActivityAdapter : ListAdapter<RecentActivity, RecentActivityAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvActivityIcon: android.widget.TextView = itemView.findViewById(R.id.tvActivityIcon)
        private val tvActivityTitle: android.widget.TextView = itemView.findViewById(R.id.tvActivityTitle)
        private val tvActivityDescription: android.widget.TextView = itemView.findViewById(R.id.tvActivityDescription)
        private val tvActivityTime: android.widget.TextView = itemView.findViewById(R.id.tvActivityTime)
        
        fun bind(activity: RecentActivity) {
            tvActivityIcon.text = activity.icon
            tvActivityTitle.text = activity.title
            tvActivityDescription.text = activity.description
            tvActivityTime.text = activity.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateActivities(newActivities: List<RecentActivity>) {
        submitList(newActivities)
    }
}

class ActivityDiffCallback : DiffUtil.ItemCallback<RecentActivity>() {
    override fun areItemsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
        return oldItem.title == newItem.title && oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
        return oldItem == newItem
    }
}

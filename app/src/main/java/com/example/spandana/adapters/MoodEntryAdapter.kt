package com.example.spandana.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spandana.databinding.ItemMoodEntryBinding
import com.example.spandana.models.MoodEntry

class MoodEntryAdapter : ListAdapter<MoodEntry, MoodEntryAdapter.MoodEntryViewHolder>(MoodEntryDiffCallback()) {

    inner class MoodEntryViewHolder(private val binding: ItemMoodEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry) {
            binding.moodType.text = moodEntry.mood
            binding.moodTime.text = moodEntry.time
            binding.moodDate.text = moodEntry.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val binding = ItemMoodEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MoodEntryDiffCallback : DiffUtil.ItemCallback<MoodEntry>() {
    override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
        return oldItem.time == newItem.time && oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
        return oldItem == newItem
    }
}
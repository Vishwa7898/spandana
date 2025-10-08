package com.example.spandana.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spandana.databinding.ItemHabitBinding
import com.example.spandana.models.Habit
import com.example.spandana.models.HabitProgress
import com.example.spandana.utils.HabitManager
import java.text.SimpleDateFormat
import java.util.*

class HabitAdapter(
    private val onHabitClick: (Habit, Boolean) -> Unit,
    private val habitManager: HabitManager
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback()) {

    // ViewHolder class එක correctly define කරන්න
    inner class HabitViewHolder(
        private val binding: ItemHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.tvHabitName.text = habit.name
            binding.tvHabitDescription.text = habit.description
            
            // Get today's progress
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val progress = habitManager.getHabitProgressForDate(habit.id, today)
            
            binding.progressBarHabit.progress = progress.completionPercentage
            binding.tvProgressText.text = progress.getProgressText()
            
            // Update complete button state
            if (progress.isCompleted) {
                binding.btnComplete.text = "✓"
                binding.btnComplete.isEnabled = false
            } else {
                binding.btnComplete.text = "✓"
                binding.btnComplete.isEnabled = true
            }
            
            // Set click listeners
            binding.btnComplete.setOnClickListener {
                onHabitClick(habit, true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = getItem(position)
        holder.bind(habit)
    }
}

// DiffUtil callback class එක ස්වාධීනව create කරන්න
class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
    override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
        return oldItem == newItem
    }
}
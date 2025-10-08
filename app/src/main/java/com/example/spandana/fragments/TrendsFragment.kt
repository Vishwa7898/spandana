package com.example.spandana.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spandana.R
import com.example.spandana.databinding.FragmentTrendsBinding
import com.example.spandana.models.MoodEntry
import com.example.spandana.utils.HabitManager
import com.example.spandana.utils.HydrationManager
import com.example.spandana.utils.MoodManager
import java.text.SimpleDateFormat
import java.util.*

class TrendsFragment : Fragment() {

    private var _binding: FragmentTrendsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var habitManager: HabitManager
    private lateinit var hydrationManager: HydrationManager
    private lateinit var moodManager: MoodManager
    private lateinit var recentActivityAdapter: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitManager = HabitManager.getInstance(requireContext())
        hydrationManager = HydrationManager.getInstance(requireContext())
        moodManager = MoodManager.getInstance(requireContext())
        
        setupToolbar()
        setupRecyclerView()
        loadTrendsData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        recentActivityAdapter = RecentActivityAdapter()
        binding.recyclerViewRecentActivity.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentActivityAdapter
        }
    }

    private fun loadTrendsData() {
        // Load habits completion rate
        val habitsCompletionRate = habitManager.getTodayCompletionRate()
        binding.tvHabitsCompletion.text = "$habitsCompletionRate%"
        
        // Load mood average
        val weeklyMoodAverage = moodManager.getWeeklyAverageMood()
        val moodEmoji = getMoodEmoji(weeklyMoodAverage)
        binding.tvMoodAverage.text = moodEmoji
        
        // Load weekly statistics
        loadWeeklyStatistics()
        
        // Load recent activity
        loadRecentActivity()
    }

    private fun loadWeeklyStatistics() {
        // Weekly habits completed
        val weeklyProgress = habitManager.getWeeklyProgress()
        val totalHabitsCompleted = weeklyProgress.values.sumOf { progressList ->
            progressList.count { it.isCompleted }
        }
        binding.tvWeeklyHabits.text = totalHabitsCompleted.toString()
        
        // Weekly water intake
        val weeklyHydrationStats = hydrationManager.getWeeklyStats()
        val totalWaterIntake = weeklyHydrationStats.sumOf { it.totalIntake }
        val waterInLiters = totalWaterIntake / 1000
        binding.tvWeeklyWater.text = "${waterInLiters}L"
        
        // Weekly mood entries
        val weeklyMoodEntries = moodManager.getWeeklyMoodEntries()
        binding.tvWeeklyMoods.text = weeklyMoodEntries.size.toString()
    }

    private fun loadRecentActivity() {
        val recentActivities = mutableListOf<RecentActivity>()
        
        // Add recent mood entries
        val recentMoods = moodManager.getAllMoodEntries().take(3)
        recentMoods.forEach { mood ->
            recentActivities.add(
                RecentActivity(
                    type = "mood",
                    title = "Mood Entry",
                    description = "${mood.emoji} ${mood.mood}",
                    time = mood.getFormattedTime(),
                    icon = mood.emoji
                )
            )
        }
        
        // Add recent habit completions
        val todayHabits = habitManager.getTodayHabitEntries()
        todayHabits.take(3).forEach { habitEntry ->
            val habit = habitManager.getAllHabits().find { it.id == habitEntry.habitId }
            if (habit != null) {
                recentActivities.add(
                    RecentActivity(
                        type = "habit",
                        title = "Habit Completed",
                        description = habit.name,
                        time = formatTime(habitEntry.timestamp),
                        icon = habit.icon
                    )
                )
            }
        }
        
        // Add recent water intake
        val todayHydration = hydrationManager.getTodayStats()
        if (todayHydration.entries.isNotEmpty()) {
            val recentWater = todayHydration.entries.take(2)
            recentWater.forEach { entry ->
                recentActivities.add(
                    RecentActivity(
                        type = "hydration",
                        title = "Water Intake",
                        description = "${entry.getFormattedAmount()} - ${entry.containerType}",
                        time = entry.getFormattedTime(),
                        icon = "💧"
                    )
                )
            }
        }
        
        // Sort by time and take most recent
        recentActivities.sortByDescending { it.time }
        recentActivityAdapter.updateActivities(recentActivities.take(10))
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(date)
    }

    private fun getMoodEmoji(average: Double): String {
        return when {
            average >= 4.5 -> "😊"
            average >= 3.5 -> "😌"
            average >= 2.5 -> "😐"
            average >= 1.5 -> "😢"
            else -> "😠"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data class for recent activity
    data class RecentActivity(
        val type: String,
        val title: String,
        val description: String,
        val time: String,
        val icon: String
    )

    // RecyclerView Adapter
    private inner class RecentActivityAdapter : RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder>() {

        private var activities = listOf<RecentActivity>()

        fun updateActivities(newActivities: List<RecentActivity>) {
            activities = newActivities
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recent_activity, parent, false)
            return ActivityViewHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
            holder.bind(activities[position])
        }

        override fun getItemCount(): Int = activities.size

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
    }
}
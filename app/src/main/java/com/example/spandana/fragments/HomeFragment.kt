package com.example.spandana.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.spandana.R
import com.example.spandana.activities.HabitsActivity
import com.example.spandana.activities.HydrationActivity
import com.example.spandana.adapters.RecentActivityAdapter
import com.example.spandana.models.RecentActivity
import com.example.spandana.databinding.FragmentHomeBinding
import com.example.spandana.utils.HabitManager
import com.example.spandana.utils.HydrationManager
import com.example.spandana.utils.MoodManager
import com.example.spandana.utils.ThemeManager
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recentActivityAdapter: RecentActivityAdapter
    private lateinit var themeManager: ThemeManager
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = sdf.format(Date())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        themeManager = ThemeManager.getInstance(requireContext())
        setupClickListeners()
        setupRecyclerView()
        updateProgress()
    }

    private fun setupClickListeners() {
        // Theme toggle using menu item instead of navigation icon
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_dark_mode -> {
                    themeManager.toggleTheme()
                    true
                }
                else -> false
            }
        }
        // Quick Actions click listeners
        binding.quickActionMood.setOnClickListener {
            val moodFragment = MoodJournalFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, moodFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.quickActionWater.setOnClickListener {
            val intent = Intent(requireContext(), HydrationActivity::class.java)
            startForResult.launch(intent)  // Use startForResult instead of startActivity
        }

        binding.quickActionHabits.setOnClickListener {
            val intent = Intent(requireContext(), HabitsActivity::class.java)
            startForResult.launch(intent)  // Use startForResult instead of startActivity
        }
    }

    private fun setupRecyclerView() {
        recentActivityAdapter = RecentActivityAdapter()
        binding.recyclerViewRecentActivity.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentActivityAdapter
        }
        loadRecentActivities()
    }

    private fun updateProgress() {
        try {
            // Get today's date for accurate tracking
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Update Habits Progress
            val habitManager = HabitManager.getInstance(requireContext())
            val activeHabits = habitManager.getActiveHabits()
            val totalHabits = activeHabits.size
            val completedHabits = activeHabits.count { habit ->
                val progress = habitManager.getHabitProgressForDate(habit.id, today)
                progress.isCompleted
            }
            val habitsProgress = if (totalHabits > 0) {
                (completedHabits.toFloat() / totalHabits.toFloat() * 100).toInt()
            } else 0
            binding.progressHabits.progress = habitsProgress
            binding.tvHabitsProgress.text = "$completedHabits/$totalHabits completed"

            // Update Water Progress
            val hydrationManager = HydrationManager.getInstance(requireContext())
            val todayStats = hydrationManager.getTodayStats()
            val waterGoal = todayStats.goal
            val waterIntake = todayStats.totalIntake
            val waterProgress = if (waterGoal > 0) {
                (waterIntake.toFloat() / waterGoal.toFloat() * 100).toInt()
            } else 0
            binding.progressWater.progress = waterProgress
            binding.tvWaterProgress.text = "$waterIntake/$waterGoal glasses"

            // Update Mood Status
            val moodManager = MoodManager.getInstance(requireContext())
            val todayMoodEntries = moodManager.getTodayMoodEntries()
            val currentMood = todayMoodEntries.lastOrNull()
            binding.tvMoodStatus.text = when {
                currentMood?.mood.isNullOrEmpty() -> "Not set yet"
                else -> currentMood?.mood ?: "Not set yet"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Set default values if there's an error
            binding.progressHabits.progress = 0
            binding.progressWater.progress = 0
            binding.tvHabitsProgress.text = "0/0 completed"
            binding.tvWaterProgress.text = "0/0 glasses"
            binding.tvMoodStatus.text = "Not set yet"
        }
    }

    // Register for activity results to update progress after activities
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            updateProgress()
            loadRecentActivities()
        }
    }

    private fun loadRecentActivities() {
        val activities = mutableListOf<RecentActivity>()

        // Load from SharedPreferences or your database
        val recentWater = sharedPreferences.getInt("water_intake_$today", 0)
        if (recentWater > 0) {
            activities.add(RecentActivity("Water Intake", "Drank $recentWater glasses of water", System.currentTimeMillis()))
        }

        val recentMood = sharedPreferences.getString("mood_$today", null)
        recentMood?.let {
            activities.add(RecentActivity("Mood Check", "Feeling $it", System.currentTimeMillis()))
        }

        val completedHabits = sharedPreferences.getInt("completed_habits_$today", 0)
        if (completedHabits > 0) {
            activities.add(RecentActivity("Habits", "Completed $completedHabits habits", System.currentTimeMillis()))
        }

        // Sort by most recent
        activities.sortByDescending { it.timestamp }
        recentActivityAdapter.submitList(activities)
    }

    override fun onResume() {
        super.onResume()
        updateProgress()  // Refresh progress when returning to the fragment
        loadRecentActivities()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
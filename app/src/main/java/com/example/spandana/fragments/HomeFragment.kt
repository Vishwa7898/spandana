package com.example.spandana.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spandana.R
import com.example.spandana.adapters.HabitAdapter
import com.example.spandana.databinding.FragmentHomeBinding
import com.example.spandana.models.Habit
import com.example.spandana.utils.SharedPreferencesManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var prefs: SharedPreferencesManager

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

        prefs = SharedPreferencesManager(requireContext())
        setupRecyclerView()
        loadData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter { habit, isCompleted ->
            updateHabitProgress(habit, isCompleted)
        }

        binding.habitsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }

    private fun loadData() {
        // Sample data - in real app, load from SharedPreferences
        val habits = listOf(
            Habit("Drink Water", 8, 0, "glass"),
            Habit("Meditate", 10, 0, "minutes"),
            Habit("Walk", 5000, 0, "steps"),
            Habit("Exercise", 30, 0, "minutes"),
            Habit("Sleep", 8, 0, "hours")
        )
        habitAdapter.submitList(habits)

        // Calculate and display progress
        val progress = calculateOverallProgress(habits)
        binding.dailyProgressText.text = "$progress%"
        binding.progressBar.progress = progress
    }

    private fun calculateOverallProgress(habits: List<Habit>): Int {
        if (habits.isEmpty()) return 0
        val totalProgress = habits.sumBy { it.calculatePercentage() }
        return totalProgress / habits.size
    }

    private fun updateHabitProgress(habit: Habit, isCompleted: Boolean) {
        // Update habit progress and save to SharedPreferences
        val newProgress = if (isCompleted) habit.target else maxOf(0, habit.current - 1)
        // Save to SharedPreferences and update UI
    }

    private fun setupClickListeners() {
        binding.moodCardView.setOnClickListener {
            // Navigate to Mood Journal
            val moodFragment = MoodJournalFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, moodFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.activityCardView.setOnClickListener {
            // Show activity details
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
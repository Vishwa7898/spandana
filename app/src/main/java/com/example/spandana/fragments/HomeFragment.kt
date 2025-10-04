package com.example.spandana.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spandana.R
import com.example.spandana.databinding.FragmentHomeBinding
import com.example.spandana.utils.ThemeManager
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeManager: ThemeManager

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

        themeManager = ThemeManager.getInstance(requireContext())
        setupWeekDays()
        setupClickListeners()
        updateProgressData()
    }

    private fun setupWeekDays() {
        val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
        
        binding.weekDaysContainer.removeAllViews()
        
        for (i in 0..6) {
            val dayView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_day_selector, binding.weekDaysContainer, false)
            
            val dayText = dayView.findViewById<TextView>(R.id.tv_day)
            val dayNumber = dayView.findViewById<TextView>(R.id.tv_day_number)
            
            dayText.text = days[i]
            dayNumber.text = "${calendar.get(Calendar.DAY_OF_MONTH) - currentDay + i}"
            
            // Highlight current day
            if (i == currentDay) {
                dayView.setBackgroundResource(R.drawable.day_selector_selected)
                dayText.setTextColor(resources.getColor(R.color.text_primary, null))
                dayNumber.setTextColor(resources.getColor(R.color.text_primary, null))
            } else {
                dayView.setBackgroundResource(R.drawable.day_selector_unselected)
                dayText.setTextColor(resources.getColor(R.color.text_secondary, null))
                dayNumber.setTextColor(resources.getColor(R.color.text_secondary, null))
            }
            
            binding.weekDaysContainer.addView(dayView)
        }
    }

    private fun updateProgressData() {
        // Update progress values
        binding.tvExerciseTime.text = "20 min"
        binding.tvCompletedGoals.text = "3/5"
        binding.tvConsecutiveDays.text = "5 days"
        
        // Set circular progress
        binding.circularProgress.progress = 70
    }

    private fun setupClickListeners() {
        // Theme toggle
        binding.toolbar.setNavigationOnClickListener {
            themeManager.toggleTheme()
        }

        // Start goal button
        binding.btnStartGoal.setOnClickListener {
            // Navigate to categories or start workout
        }

        // Start workout button
        binding.btnStartWorkout.setOnClickListener {
            // Start AI recommended workout
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
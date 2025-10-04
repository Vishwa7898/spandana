package com.example.spandana.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spandana.R
import com.example.spandana.databinding.FragmentCategoriesBinding
import com.example.spandana.utils.ThemeManager

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeManager: ThemeManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themeManager = ThemeManager.getInstance(requireContext())
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Theme toggle
        binding.toolbar.setNavigationOnClickListener {
            themeManager.toggleTheme()
        }

        // Category cards click listeners
        binding.cardExercise.setOnClickListener {
            onCategoryClicked("Exercise")
        }

        binding.cardSleep.setOnClickListener {
            onCategoryClicked("Sleep")
        }

        binding.cardNutrition.setOnClickListener {
            onCategoryClicked("Nutrition")
        }

        binding.cardMindfulness.setOnClickListener {
            onCategoryClicked("Mindfulness")
        }

        binding.cardHealth.setOnClickListener {
            onCategoryClicked("Health")
        }

        binding.cardMood.setOnClickListener {
            onCategoryClicked("Mood")
        }
    }

    private fun onCategoryClicked(categoryName: String) {
        // Category click කල විට කරන actions
        when(categoryName) {
            "Exercise" -> {
                // Exercise/Habits fragment එකට navigate කිරීම
                // For now, just show a toast or navigate to home
            }
            "Sleep" -> {
                // Sleep tracking fragment එකට navigate කිරීම
            }
            "Nutrition" -> {
                // Nutrition/Water tracking fragment එකට navigate කිරීම
            }
            "Mindfulness" -> {
                // Mindfulness fragment එකට navigate කිරීම
            }
            "Health" -> {
                // Health tracking fragment එකට navigate කිරීම
            }
            "Mood" -> {
                // Mood journal fragment එකට navigate කිරීම
                val moodFragment = MoodJournalFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, moodFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
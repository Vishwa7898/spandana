package com.example.spandana.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spandana.R
import com.example.spandana.activities.MeditationActivity
import com.example.spandana.fragments.MoodJournalFragment
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

        // Category cards click listeners
        binding.cardHabits.setOnClickListener {
            onCategoryClicked("Habits")
        }

        binding.cardSleep.setOnClickListener {
            onCategoryClicked("Sleep")
        }

        binding.cardMood.setOnClickListener {
            onCategoryClicked("Mood")
        }

        binding.cardExercise.setOnClickListener {
            onCategoryClicked("Exercise")
        }

        binding.cardWater.setOnClickListener {
            onCategoryClicked("Water")
        }

        binding.cardMeditation.setOnClickListener {
            onCategoryClicked("Meditation")
        }
    }

    private fun onCategoryClicked(categoryName: String) {
        when(categoryName) {
            "Habits" -> {
                val intent = Intent(requireContext(), com.example.spandana.activities.HabitsActivity::class.java)
                startActivity(intent)
            }
            "Sleep" -> {
                // Sleep tracking implementation
            }
            "Mood" -> {
                val fragment = MoodJournalFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            "Exercise" -> {
                // Exercise tracking implementation
            }
            "Water" -> {
                val intent = Intent(requireContext(), com.example.spandana.activities.HydrationActivity::class.java)
                startActivity(intent)
            }
            "Meditation" -> {
                val intent = Intent(requireContext(), MeditationActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
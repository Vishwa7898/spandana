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
        // Theme toggle
        binding.toolbar.setNavigationOnClickListener {
            themeManager.toggleTheme()
        }

        // Category cards click listeners
        binding.cardHabits.setOnClickListener {
            onCategoryClicked("Habits")
        }

        binding.cardSleep.setOnClickListener {
            onCategoryClicked("Sleep")
        }

        binding.cardMindfulness.setOnClickListener {
            onCategoryClicked("Mindfulness")
        }

        binding.cardMood.setOnClickListener {
            onCategoryClicked("Mood")
        }

        binding.cardHydration.setOnClickListener {
            onCategoryClicked("Hydration")
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
            "Habits" -> {
                // Navigate to Habits Activity
                val intent = Intent(requireContext(), com.example.spandana.activities.HabitsActivity::class.java)
                startActivity(intent)
            }
            "Sleep" -> {
                // Sleep tracking fragment එකට navigate කිරීම
            }
            "Mindfulness" -> {
                // Navigate to Meditation Activity
                val intent = Intent(requireContext(), MeditationActivity::class.java)
                startActivity(intent)
            }
            "Mood" -> {
                // Navigate to Mood Journal Fragment
                val fragment = MoodJournalFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            "Hydration" -> {
                // Navigate to Hydration Activity
                val intent = Intent(requireContext(), com.example.spandana.activities.HydrationActivity::class.java)
                startActivity(intent)
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
package com.example.spandana.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spandana.R
import com.example.spandana.activities.MindfulnessActivity
import com.example.spandana.adapters.CategoriesAdapter
import com.example.spandana.databinding.FragmentCategoriesBinding
import com.example.spandana.models.Category

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoriesAdapter: CategoriesAdapter

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

        setupRecyclerView()
        loadCategories()
    }

    private fun setupRecyclerView() {
        categoriesAdapter = CategoriesAdapter { category ->
            // Category එක click කල විට කරන actions
            onCategoryClicked(category)
        }

        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriesAdapter
        }
    }

    private fun loadCategories() {
        val categories = listOf(
            Category("Habits", R.drawable.ic_habits, "#007AFF"),
            Category("Body Measurements", R.drawable.ic_body_measurements, "#5856D6"),
            Category("Cycle Tracking", R.drawable.ic_cycle_tracking, "#FF2D55"),
            Category("Hearing", R.drawable.ic_hearing, "#AF52DE"),
            Category("Heart", R.drawable.ic_heart, "#FF3B30"),
            Category("Mindfulness", R.drawable.ic_mindfulness, "#5856D6"),
            Category("Mobility", R.drawable.ic_mobility, "#FF9500"),
            Category("Respiratory", R.drawable.ic_respiratory, "#FF2D55"),
            Category("Sleep", R.drawable.ic_sleep, "#007AFF"),
            Category("Symptoms", R.drawable.ic_symptoms, "#FF9500"),
            Category("Vitals", R.drawable.ic_vitals, "#FF3B30"),
            Category("Drink Water", R.drawable.ic_water, "#FF2D55")
        )
        categoriesAdapter.submitList(categories)
    }

    private fun onCategoryClicked(category: Category) {
        // Category click කල විට කරන actions
        when(category.name) {
            "Mindfulness" -> {
                // Navigate to MindfulnessActivity
                val intent = Intent(requireContext(), MindfulnessActivity::class.java)
                startActivity(intent)
            }
            "Habits" -> {
                // Habits fragment එකට navigate කිරීම
                android.widget.Toast.makeText(requireContext(), "Habits feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
            }
            "Drink Water" -> {
                // Water tracking fragment එකට navigate කිරීම
                android.widget.Toast.makeText(requireContext(), "Water tracking feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Other categories
                android.widget.Toast.makeText(requireContext(), "${category.name} feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
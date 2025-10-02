package com.example.spandana.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spandana.adapters.MoodEntryAdapter
import com.example.spandana.databinding.FragmentMoodJournalBinding
import com.example.spandana.models.MoodEntry
import com.example.spandana.utils.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalFragment : Fragment() {

    private var _binding: FragmentMoodJournalBinding? = null
    private val binding get() = _binding!!
    private lateinit var moodAdapter: MoodEntryAdapter
    private lateinit var prefs: SharedPreferencesManager
    private var selectedMood: String = "Neutral"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesManager(requireContext())
        setupRecyclerView()
        loadMoodEntries()
        setupMoodSelection()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodEntryAdapter()
        binding.pastEntriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }
    }

    private fun loadMoodEntries() {
        // Sample mood entries - real app එකේ SharedPreferences වලින් load කරන්න
        val entries = listOf(
            MoodEntry("Content", "10:00 AM", "Today"),
            MoodEntry("Neutral", "2:00 PM", "Today"),
            MoodEntry("Happy", "8:00 PM", "Today")
        )
        moodAdapter.submitList(entries)
    }

    private fun setupMoodSelection() {
        // Mood selection listeners setup කිරීම
        binding.happyMood.setOnClickListener {
            selectMood("Happy")
            selectedMood = "Happy"
        }

        binding.contentMood.setOnClickListener {
            selectMood("Content")
            selectedMood = "Content"
        }

        binding.neutralMood.setOnClickListener {
            selectMood("Neutral")
            selectedMood = "Neutral"
        }

        binding.sadMood.setOnClickListener {
            selectMood("Sad")
            selectedMood = "Sad"
        }

        binding.angryMood.setOnClickListener {
            selectMood("Angry")
            selectedMood = "Angry"
        }

        // Default selection
        selectMood("Neutral")
    }

    private fun selectMood(mood: String) {
        // සියලුම moods reset කිරීම
        resetAllMoods()

        // Select the chosen mood
        when(mood) {
            "Happy" -> {
                binding.happyMood.isSelected = true
                binding.happyMood.alpha = 1.0f
            }
            "Content" -> {
                binding.contentMood.isSelected = true
                binding.contentMood.alpha = 1.0f
            }
            "Neutral" -> {
                binding.neutralMood.isSelected = true
                binding.neutralMood.alpha = 1.0f
            }
            "Sad" -> {
                binding.sadMood.isSelected = true
                binding.sadMood.alpha = 1.0f
            }
            "Angry" -> {
                binding.angryMood.isSelected = true
                binding.angryMood.alpha = 1.0f
            }
        }
    }

    private fun resetAllMoods() {
        // සියලුම moods reset කිරීම
        val moodViews = listOf(
            binding.happyMood,
            binding.contentMood,
            binding.neutralMood,
            binding.sadMood,
            binding.angryMood
        )

        moodViews.forEach { view ->
            view.isSelected = false
            view.alpha = 0.6f
        }
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            saveMoodEntry()
        }
    }

    private fun saveMoodEntry() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        val currentDate = dateFormat.format(Date())

        val newEntry = MoodEntry(selectedMood, currentTime, currentDate)

        // Save to SharedPreferences and update UI
        // prefs.saveMoodEntry(newEntry)

        // For now, just navigate back
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
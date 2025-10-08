package com.example.spandana.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spandana.adapters.MoodEntryAdapter
import com.example.spandana.databinding.FragmentMoodJournalBinding
import com.example.spandana.models.MoodEntry
import com.example.spandana.models.MoodOptions
import com.example.spandana.utils.MoodManager
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalFragment : Fragment() {

    private var _binding: FragmentMoodJournalBinding? = null
    private val binding get() = _binding!!
    private lateinit var moodAdapter: MoodEntryAdapter
    private lateinit var moodManager: MoodManager
    private var selectedMood: String = "Neutral"
    private var selectedEmoji: String = "😐"

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

        moodManager = MoodManager.getInstance(requireContext())
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
        // Load mood entries from MoodManager
        val entries = moodManager.getAllMoodEntries().sortedByDescending { it.timestamp }
        moodAdapter.submitList(entries)
    }

    private fun setupMoodSelection() {
        // Mood selection listeners setup කිරීම
        binding.happyMood.setOnClickListener {
            selectMood("Happy", "😊")
        }

        binding.contentMood.setOnClickListener {
            selectMood("Good", "😌")
        }

        binding.neutralMood.setOnClickListener {
            selectMood("Neutral", "😐")
        }

        binding.sadMood.setOnClickListener {
            selectMood("Sad", "😢")
        }

        binding.angryMood.setOnClickListener {
            selectMood("Angry", "😠")
        }

        // Default selection
        selectMood("Neutral", "😐")
    }

    private fun selectMood(mood: String, emoji: String) {
        // සියලුම moods reset කිරීම
        resetAllMoods()

        // Update selected mood and emoji
        selectedMood = mood
        selectedEmoji = emoji

        // Select the chosen mood
        when(mood) {
            "Happy" -> {
                binding.happyMood.isSelected = true
                binding.happyMood.alpha = 1.0f
            }
            "Good" -> {
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        
        // Create new mood entry
        val newEntry = MoodEntry(
            userId = getCurrentUserId(),
            mood = selectedMood,
            emoji = selectedEmoji,
            notes = binding.moodNotes.text.toString(),
            timestamp = System.currentTimeMillis(),
            date = currentDate
        )

        // Save to MoodManager
        moodManager.saveMoodEntry(newEntry)

        // Show success message
        Toast.makeText(requireContext(), "Mood saved successfully! 😊", Toast.LENGTH_SHORT).show()

        // Reload entries and clear form
        loadMoodEntries()
        binding.moodNotes.text?.clear()
        
        // Reset to default mood
        selectMood("Neutral", "😐")
    }

    private fun getCurrentUserId(): String {
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        return prefs.getString("user_uid", "guest") ?: "guest"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
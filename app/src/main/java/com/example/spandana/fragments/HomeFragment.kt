package com.example.spandana.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.spandana.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.appbar.MaterialToolbar
import com.example.spandana.adapters.RecentActivityAdapter
import com.example.spandana.models.RecentActivity
import com.example.spandana.utils.ThemeManager

class HomeFragment : Fragment() {

    // UI Components
    private lateinit var tvUserName: TextView
    private lateinit var tvUserStreak: TextView
    private lateinit var tvHabitsProgress: TextView
    private lateinit var tvWaterProgress: TextView
    private lateinit var tvMoodStatus: TextView
    private lateinit var tvMotivationalQuote: TextView
    private lateinit var recyclerViewRecentActivity: RecyclerView
    private lateinit var cardMeditation: MaterialCardView
    private lateinit var cardExercise: MaterialCardView
    private lateinit var cardSleep: MaterialCardView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        initializeViews(view)
        setupToolbar(view)
        setupClickListeners(view)
        loadUserData()
        setupRecyclerView(view)
    }

    private fun initializeViews(view: View) {
        // TextView initialization
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserStreak = view.findViewById(R.id.tvUserStreak)
        tvHabitsProgress = view.findViewById(R.id.tvHabitsProgress)
        tvWaterProgress = view.findViewById(R.id.tvWaterProgress)
        tvMoodStatus = view.findViewById(R.id.tvMoodStatus)
        tvMotivationalQuote = view.findViewById(R.id.tvMotivationalQuote)

        // RecyclerView initialization
        recyclerViewRecentActivity = view.findViewById(R.id.recyclerViewRecentActivity)

        // CardView initialization
        cardMeditation = view.findViewById(R.id.cardMeditation)
        cardExercise = view.findViewById(R.id.cardExercise)
        cardSleep = view.findViewById(R.id.cardSleep)

        // Toolbar initialization
        toolbar = view.findViewById(R.id.toolbar)
    }

    private fun setupToolbar(view: View) {
        // Set up toolbar menu
        toolbar.inflateMenu(R.menu.toolbar_menu)
        
        // Dark mode toggle click listener
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_dark_mode -> {
                    toggleDarkMode()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners(view: View) {
        // Meditation card click
        cardMeditation.setOnClickListener {
            showMessage("Meditation clicked")
            // Navigate to meditation screen
        }

        // Exercise card click
        cardExercise.setOnClickListener {
            showMessage("Exercise clicked")
            // Navigate to exercise screen
        }

        // Sleep card click
        cardSleep.setOnClickListener {
            showMessage("Sleep clicked")
            // Navigate to sleep screen
        }
    }

    private fun loadUserData() {
        // Get user name from SharedPreferences
        val userName = sharedPreferences.getString("userName", "User") ?: "User"
        val streakDays = 7 // Replace with actual streak
        val habitsCompleted = 3
        val totalHabits = 5
        val waterConsumed = 1.2

        // Update UI with loaded data
        tvUserName.text = userName
        tvUserStreak.text = "🔥 $streakDays day streak"
        tvHabitsProgress.text = "$habitsCompleted/$totalHabits"
        tvWaterProgress.text = "${waterConsumed}L"
        tvMoodStatus.text = "Good"

        // Set welcome message with user name
        toolbar.title = "Welcome back, $userName! 👋"

        // Set motivational quote
        val motivationalQuotes = listOf(
            "Every small step counts towards your wellness journey!",
            "Consistency is the key to success!",
            "You're doing great! Keep going!",
            "Small habits lead to big changes!"
        )
        val randomQuote = motivationalQuotes.random()
        tvMotivationalQuote.text = randomQuote
    }

    private fun setupRecyclerView(view: View) {
        // Create sample data for recent activity
        val recentActivities = listOf(
            RecentActivity("meditation", "Completed Morning Meditation", "30 minutes of mindfulness practice", "30 mins ago", "🧘"),
            RecentActivity("water", "Logged Water Intake", "500ml of water consumed", "1 hour ago", "💧"),
            RecentActivity("mood", "Mood Logged", "Feeling happy and energetic", "2 hours ago", "😊"),
            RecentActivity("exercise", "Daily Exercise", "Completed 30-minute workout", "3 hours ago", "💪"),
            RecentActivity("habit", "Reading Habit", "Read for 20 minutes", "5 hours ago", "📚")
        )

        // Setup RecyclerView with adapter
        val adapter = RecentActivityAdapter()
        adapter.updateActivities(recentActivities)
        recyclerViewRecentActivity.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRecentActivity.adapter = adapter
        recyclerViewRecentActivity.setHasFixedSize(true)
    }

    private fun openWaterLogging() {
        // Implement water logging functionality
        // You can show a dialog or navigate to another activity
        val dialog = WaterLoggingDialogFragment()
        dialog.show(parentFragmentManager, "WaterLoggingDialog")
    }

    private fun openMoodLogging() {
        // Implement mood logging functionality
        // You can show a dialog or navigate to another activity
        val dialog = MoodLoggingDialogFragment()
        dialog.show(parentFragmentManager, "MoodLoggingDialog")
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun toggleDarkMode() {
        // Use ThemeManager to toggle theme
        val themeManager = ThemeManager.getInstance(requireContext())
        themeManager.toggleTheme()
        
        // Show feedback to user
        val message = if (themeManager.isDarkMode()) "Dark mode enabled" else "Light mode enabled"
        showMessage(message)
        
        // The theme change will be applied immediately by ThemeManager
    }
}


// Water Logging Dialog Fragment (Sample)
class WaterLoggingDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Log Water Intake")
            .setMessage("How much water did you drink?")
            .setPositiveButton("Save") { dialog, _ ->
                // Save water intake logic
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}

// Mood Logging Dialog Fragment (Sample)
class MoodLoggingDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setMessage("Select your current mood")
            .setPositiveButton("Save") { dialog, _ ->
                // Save mood logic
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}
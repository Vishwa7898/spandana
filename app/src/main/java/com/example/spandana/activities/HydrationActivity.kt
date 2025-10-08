package com.example.spandana.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spandana.R
import com.example.spandana.databinding.ActivityHydrationBinding
import com.example.spandana.models.HydrationEntry
import com.example.spandana.models.HydrationStats
import com.example.spandana.utils.HydrationManager
import com.example.spandana.services.HydrationReminderService
import java.util.*

class HydrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHydrationBinding
    private lateinit var hydrationManager: HydrationManager
    private lateinit var entriesAdapter: HydrationEntriesAdapter
    private var currentStats: HydrationStats? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHydrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hydrationManager = HydrationManager.getInstance(this)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        loadTodayStats()
        updateReminderSettings()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        entriesAdapter = HydrationEntriesAdapter { entry ->
            showDeleteConfirmation(entry)
        }
        
        binding.recyclerViewEntries.apply {
            layoutManager = LinearLayoutManager(this@HydrationActivity)
            adapter = entriesAdapter
        }
    }

    private fun setupClickListeners() {
        // Quick add buttons
        binding.btnSmallGlass.setOnClickListener { addWater(150, "Small Glass") }
        binding.btnMediumGlass.setOnClickListener { addWater(200, "Medium Glass") }
        binding.btnLargeGlass.setOnClickListener { addWater(250, "Large Glass") }
        binding.btnBottle.setOnClickListener { addWater(500, "Water Bottle") }
        
        // Testing: Add a button to manually trigger reminder
        binding.btnCustomAmount.setOnLongClickListener {
            // Long press on Custom Amount button to test reminder
            testReminder()
            true
        }
        
        // Settings
        binding.btnGoalSettings.setOnClickListener { showGoalSettingsDialog() }
        
        // Reminder toggle
        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            val (_, interval) = hydrationManager.getReminderSettings()
            hydrationManager.updateReminderSettings(isChecked, interval)
            updateReminderSettings()
        }
    }

    private fun loadTodayStats() {
        currentStats = hydrationManager.getTodayStats()
        updateUI()
    }

    private fun updateUI() {
        currentStats?.let { stats ->
            // Update progress
            val percentage = stats.getProgressPercentage()
            binding.progressCircle.progress = percentage
            binding.tvProgressPercentage.text = "$percentage%"
            binding.tvProgressAmount.text = "${stats.getFormattedTotal()} / ${stats.getFormattedGoal()}"
            
            // Update goal text
            binding.tvGoalText.text = "Daily Goal: ${stats.getFormattedGoal()}"
            
            // Update motivational message
            binding.tvMotivationalMessage.text = when {
                stats.isGoalReached() -> "Great job! You've reached your goal! 🎉"
                percentage >= 75 -> "Almost there! Keep going! 💪"
                percentage >= 50 -> "You're doing great! Stay hydrated! 💧"
                percentage >= 25 -> "Good start! Keep drinking water! 🌊"
                else -> "Stay hydrated! 💧"
            }
            
            // Update entries
            if (stats.entries.isNotEmpty()) {
                entriesAdapter.updateEntries(stats.entries.reversed()) // Show latest first
                binding.recyclerViewEntries.visibility = View.VISIBLE
                binding.tvNoEntries.visibility = View.GONE
            } else {
                binding.recyclerViewEntries.visibility = View.GONE
                binding.tvNoEntries.visibility = View.VISIBLE
            }
        }
    }

    private fun addWater(amount: Int, containerType: String) {
        val entry = HydrationEntry(
            id = UUID.randomUUID().toString(),
            userId = getCurrentUserId(),
            amount = amount,
            containerType = containerType,
            timestamp = System.currentTimeMillis()
        )
        
        hydrationManager.saveHydrationEntry(entry)
        loadTodayStats()
        
        Toast.makeText(this, "Added ${entry.getFormattedAmount()} of water", Toast.LENGTH_SHORT).show()
    }

    private fun showCustomAmountDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom_amount, null)
        val editAmount = dialogView.findViewById<EditText>(R.id.editAmount)
        val editContainerType = dialogView.findViewById<EditText>(R.id.editContainerType)
        
        AlertDialog.Builder(this)
            .setTitle("Add Custom Amount")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val amountText = editAmount.text.toString()
                val containerType = editContainerType.text.toString().takeIf { it.isNotBlank() } ?: "Custom"
                
                try {
                    val amount = amountText.toInt()
                    if (amount > 0 && amount <= 2000) {
                        addWater(amount, containerType)
                    } else {
                        Toast.makeText(this, "Please enter a valid amount (1-2000ml)", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showGoalSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_goal_settings, null)
        val editGoal = dialogView.findViewById<EditText>(R.id.editGoal)
        val currentGoal = hydrationManager.getUserGoal()
        
        editGoal.setText((currentGoal.dailyGoal / 1000).toString()) // Show in liters
        
        AlertDialog.Builder(this)
            .setTitle("Set Daily Goal")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val goalText = editGoal.text.toString()
                try {
                    val goalInLiters = goalText.toFloat()
                    val goalInMl = (goalInLiters * 1000).toInt()
                    
                    if (goalInMl >= 500 && goalInMl <= 5000) {
                        val updatedGoal = currentGoal.copy(dailyGoal = goalInMl)
                        hydrationManager.saveUserGoal(updatedGoal)
                        loadTodayStats()
                        Toast.makeText(this, "Daily goal updated to ${updatedGoal.getFormattedGoal()}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Please enter a goal between 0.5L and 5L", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(entry: HydrationEntry) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this ${entry.getFormattedAmount()} entry?")
            .setPositiveButton("Delete") { _, _ ->
                hydrationManager.deleteHydrationEntry(entry.id)
                loadTodayStats()
                Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateReminderSettings() {
        val (enabled, interval) = hydrationManager.getReminderSettings()
        binding.switchReminders.isChecked = enabled
        binding.tvReminderInterval.text = "$interval minutes"
        
        // Start or stop reminder service based on settings
        val intent = Intent(this, HydrationReminderService::class.java)
        if (enabled) {
            startService(intent)
        } else {
            stopService(intent)
        }
    }

    private fun getCurrentUserId(): String {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getString("user_uid", "guest") ?: "guest"
    }

    private fun testReminder() {
        // Testing function to manually trigger reminder
        val notificationManager = com.example.spandana.utils.HydrationNotificationManager.getInstance(this)
        notificationManager.showHydrationReminder()
        Toast.makeText(this, "Test reminder sent!", Toast.LENGTH_SHORT).show()
    }

    // RecyclerView Adapter
    private inner class HydrationEntriesAdapter(
        private val onDeleteClick: (HydrationEntry) -> Unit
    ) : RecyclerView.Adapter<HydrationEntriesAdapter.EntryViewHolder>() {

        private var entries = listOf<HydrationEntry>()

        fun updateEntries(newEntries: List<HydrationEntry>) {
            entries = newEntries
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hydration_entry, parent, false)
            return EntryViewHolder(view)
        }

        override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
            holder.bind(entries[position])
        }

        override fun getItemCount(): Int = entries.size

        inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
            private val tvContainerType: TextView = itemView.findViewById(R.id.tvContainerType)
            private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
            private val btnDelete: View = itemView.findViewById(R.id.btnDelete)

            fun bind(entry: HydrationEntry) {
                tvAmount.text = entry.getFormattedAmount()
                tvContainerType.text = entry.containerType
                tvTime.text = entry.getFormattedTime()
                
                btnDelete.setOnClickListener {
                    onDeleteClick(entry)
                }
            }
        }
    }
}

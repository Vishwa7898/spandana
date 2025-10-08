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
import com.example.spandana.databinding.ActivityHabitsBinding
import com.example.spandana.models.Habit
import com.example.spandana.models.HabitEntry
import com.example.spandana.models.HabitProgress
import com.example.spandana.utils.HabitManager
import java.util.*

class HabitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHabitsBinding
    private lateinit var habitManager: HabitManager
    private lateinit var habitsAdapter: HabitsAdapter
    private var habits = listOf<Habit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        habitManager = HabitManager.getInstance(this)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        loadHabits()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            onCompleteClick = { habit -> completeHabit(habit) },
            onEditClick = { habit -> editHabit(habit) },
            onDeleteClick = { habit -> deleteHabit(habit) }
        )
        
        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(this@HabitsActivity)
            adapter = habitsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }

    private fun loadHabits() {
        habits = habitManager.getActiveHabits()
        updateUI()
    }

    private fun updateUI() {
        if (habits.isEmpty()) {
            binding.recyclerViewHabits.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.progressBarToday.progress = 0
            binding.tvProgressPercentage.text = "0%"
            binding.tvProgressText.text = "0 of 0 habits completed"
        } else {
            binding.recyclerViewHabits.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            
            // Update progress
            val todayProgress = habitManager.getTodayProgress()
            val completedHabits = todayProgress.count { it.isCompleted }
            val totalHabits = todayProgress.size
            val completionRate = if (totalHabits > 0) (completedHabits * 100) / totalHabits else 0
            
            binding.progressBarToday.progress = completionRate
            binding.tvProgressPercentage.text = "$completionRate%"
            binding.tvProgressText.text = "$completedHabits of $totalHabits habits completed"
            
            // Update adapter
            habitsAdapter.updateHabits(habits)
        }
    }

    private fun completeHabit(habit: Habit) {
        val today = habitManager.getTodayHabitEntries()
        val existingEntry = today.find { it.habitId == habit.id }
        
        if (existingEntry != null) {
            // Already completed today
            Toast.makeText(this, "${habit.name} already completed today!", Toast.LENGTH_SHORT).show()
            return
        }
        
        val entry = HabitEntry(
            id = UUID.randomUUID().toString(),
            habitId = habit.id,
            userId = getCurrentUserId(),
            completedValue = habit.targetValue,
            date = getTodayDateString(),
            timestamp = System.currentTimeMillis()
        )
        
        habitManager.saveHabitEntry(entry)
        loadHabits()
        
        Toast.makeText(this, "Great! ${habit.name} completed! 🎉", Toast.LENGTH_SHORT).show()
    }

    private fun editHabit(habit: Habit) {
        showEditHabitDialog(habit)
    }

    private fun deleteHabit(habit: Habit) {
        AlertDialog.Builder(this)
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                habitManager.deleteHabit(habit.id)
                loadHabits()
                Toast.makeText(this, "Habit deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null)
        val editName = dialogView.findViewById<EditText>(R.id.editHabitName)
        val editDescription = dialogView.findViewById<EditText>(R.id.editHabitDescription)
        val editTarget = dialogView.findViewById<EditText>(R.id.editTargetValue)
        val editUnit = dialogView.findViewById<EditText>(R.id.editUnit)
        
        AlertDialog.Builder(this)
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editName.text.toString().trim()
                val description = editDescription.text.toString().trim()
                val targetText = editTarget.text.toString().trim()
                val unit = editUnit.text.toString().trim()
                
                if (name.isNotEmpty() && targetText.isNotEmpty()) {
                    try {
                        val targetValue = targetText.toInt()
                        if (targetValue > 0) {
                            val habit = Habit(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                description = description,
                                targetValue = targetValue,
                                unit = unit.ifEmpty { "times" }
                            )
                            
                            habitManager.saveHabit(habit)
                            loadHabits()
                            Toast.makeText(this, "Habit added successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Target value must be greater than 0", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null)
        val editName = dialogView.findViewById<EditText>(R.id.editHabitName)
        val editDescription = dialogView.findViewById<EditText>(R.id.editHabitDescription)
        val editTarget = dialogView.findViewById<EditText>(R.id.editTargetValue)
        val editUnit = dialogView.findViewById<EditText>(R.id.editUnit)
        
        // Pre-fill with existing values
        editName.setText(habit.name)
        editDescription.setText(habit.description)
        editTarget.setText(habit.targetValue.toString())
        editUnit.setText(habit.unit)
        
        AlertDialog.Builder(this)
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = editName.text.toString().trim()
                val description = editDescription.text.toString().trim()
                val targetText = editTarget.text.toString().trim()
                val unit = editUnit.text.toString().trim()
                
                if (name.isNotEmpty() && targetText.isNotEmpty()) {
                    try {
                        val targetValue = targetText.toInt()
                        if (targetValue > 0) {
                            val updatedHabit = habit.copy(
                                name = name,
                                description = description,
                                targetValue = targetValue,
                                unit = unit.ifEmpty { "times" }
                            )
                            
                            habitManager.saveHabit(updatedHabit)
                            loadHabits()
                            Toast.makeText(this, "Habit updated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Target value must be greater than 0", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getCurrentUserId(): String {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getString("user_uid", "guest") ?: "guest"
    }

    private fun getTodayDateString(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // RecyclerView Adapter
    private inner class HabitsAdapter(
        private val onCompleteClick: (Habit) -> Unit,
        private val onEditClick: (Habit) -> Unit,
        private val onDeleteClick: (Habit) -> Unit
    ) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

        private var habitsList = listOf<Habit>()

        fun updateHabits(newHabits: List<Habit>) {
            habitsList = newHabits
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_habit, parent, false)
            return HabitViewHolder(view)
        }

        override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
            holder.bind(habitsList[position])
        }

        override fun getItemCount(): Int = habitsList.size

        inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvHabitIcon: TextView = itemView.findViewById(R.id.tvHabitIcon)
            private val tvHabitName: TextView = itemView.findViewById(R.id.tvHabitName)
            private val tvHabitDescription: TextView = itemView.findViewById(R.id.tvHabitDescription)
            private val progressBarHabit: android.widget.ProgressBar = itemView.findViewById(R.id.progressBarHabit)
            private val tvProgressText: TextView = itemView.findViewById(R.id.tvProgressText)
            private val btnComplete: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btnComplete)
            private val btnEdit: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btnEdit)
            private val btnDelete: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btnDelete)

            fun bind(habit: Habit) {
                tvHabitIcon.text = habit.icon
                tvHabitName.text = habit.name
                tvHabitDescription.text = habit.description
                
                // Get today's progress
                val today = getTodayDateString()
                val progress = habitManager.getHabitProgressForDate(habit.id, today)
                
                progressBarHabit.progress = progress.completionPercentage
                tvProgressText.text = progress.getProgressText()
                
                // Update complete button state
                if (progress.isCompleted) {
                    btnComplete.text = "✓"
                    btnComplete.isEnabled = false
                    btnComplete.backgroundTintList = getColorStateList(R.color.text_secondary)
                } else {
                    btnComplete.text = "✓"
                    btnComplete.isEnabled = true
                    btnComplete.backgroundTintList = getColorStateList(R.color.accent_green)
                }
                
                // Set click listeners
                btnComplete.setOnClickListener { onCompleteClick(habit) }
                btnEdit.setOnClickListener { onEditClick(habit) }
                btnDelete.setOnClickListener { onDeleteClick(habit) }
            }
        }
    }
}

package com.example.spandana.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spandana.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        setupUserProfile()
        setupClickListeners()
        setupNotificationSwitch()
    }

    private fun setupUserProfile() {
        val userName = sharedPreferences.getString("userName", "") ?: ""
        val userEmail = sharedPreferences.getString("userEmail", "") ?: ""

        binding.tvProfileName.text = userName
        binding.tvProfileEmail.text = userEmail
    }

    private fun setupClickListeners() {
        binding.cardEditProfile.setOnClickListener {
            // Launch edit profile dialog/activity
            showEditProfileDialog()
        }
    }

    private fun setupNotificationSwitch() {
        // Load saved notification preference
        val notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true)
        binding.switchNotifications.isChecked = notificationsEnabled

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notificationsEnabled", isChecked).apply()
            updateNotificationSettings(isChecked)
        }
    }

    private fun showEditProfileDialog() {
        EditProfileDialog().show(parentFragmentManager, "EditProfileDialog")
    }

    private fun updateNotificationSettings(enabled: Boolean) {
        val message = if (enabled) "Notifications enabled" else "Notifications disabled"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        // TODO: Implement actual notification settings update
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Edit Profile Dialog
class EditProfileDialog : DialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentName = sharedPreferences.getString("userName", "") ?: ""
        val currentEmail = sharedPreferences.getString("userEmail", "") ?: ""

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.etName)
        val emailInput = dialogView.findViewById<EditText>(R.id.etEmail)

        nameInput.setText(currentName)
        emailInput.setText(currentEmail)

        return AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString()
                val newEmail = emailInput.text.toString()

                sharedPreferences.edit()
                    .putString("userName", newName)
                    .putString("userEmail", newEmail)
                    .apply()

                // Update UI in parent fragment
                (parentFragment as? ProfileFragment)?.setupUserProfile()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}

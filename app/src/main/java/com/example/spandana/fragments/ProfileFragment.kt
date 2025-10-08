package com.example.spandana.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.example.spandana.R
import com.example.spandana.databinding.FragmentProfileBinding
import com.example.spandana.utils.ThemeManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeManager: ThemeManager

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

        themeManager = ThemeManager.getInstance(requireContext())
        setupProfile()
        setupClickListeners()
        updateStats()
    }

    private fun setupProfile() {
        // Load user data from SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "User")
        val userEmail = prefs.getString("user_email", "")
        val userType = prefs.getString("user_type", "guest")
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)

        // Update stats
        updateStats()
        binding.tvProfileName.text = userName ?: "User"
        binding.tvProfileEmail.text = userEmail ?: ""

        // Show/hide email based on user type
        if (userType == "guest" || userEmail.isNullOrEmpty()) {
            binding.tvProfileEmail.visibility = View.GONE
        } else {
            binding.tvProfileEmail.visibility = View.VISIBLE
        }
        
        // Set initial dark mode state
        binding.switchDarkMode.isChecked = themeManager.isDarkMode()
        updateThemeIcon()
    }

    private fun updateStats() {
        binding.statsWorkouts.text = "24" // Replace with actual stats
        binding.statsMinutes.text = "1,240"
        binding.statsStreak.text = "7"
    }

    private fun updateThemeIcon() {
        binding.themeIcon.setImageResource(
            if (themeManager.isDarkMode()) R.drawable.ic_moon else R.drawable.ic_sun
        )
    }

    private fun setupClickListeners() {
        // Dark mode switch
        binding.switchDarkMode.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked != themeManager.isDarkMode()) {
                themeManager.toggleTheme()
                updateThemeIcon()
            }
        }

        // Edit profile
        binding.cardEditProfile.setOnClickListener {
            // Navigate to edit profile
        }

        // Help & Support
        binding.cardHelp.setOnClickListener {
            // Navigate to help
        }

        // About
        binding.cardAbout.setOnClickListener {
            // Navigate to about
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.logout) { _, _ ->
                // Perform logout
                performLogout()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun performLogout() {
        // Clear user data and navigate to login
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        
        // Firebase logout
        com.example.spandana.utils.FirebaseAuthManager.getInstance(requireContext()).logout()
        
        // Navigate to AuthActivity
        val intent = android.content.Intent(requireContext(), com.example.spandana.activities.AuthActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
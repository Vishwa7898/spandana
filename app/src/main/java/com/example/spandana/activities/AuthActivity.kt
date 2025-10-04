package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spandana.MainActivity
import com.example.spandana.databinding.ActivityAuthBinding
import com.example.spandana.utils.FirebaseAuthManager
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var firebaseAuthManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        firebaseAuthManager = FirebaseAuthManager.getInstance(this)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Login Button Click - Navigate to login form
        binding.loginButton.setOnClickListener {
            navigateToLoginForm()
        }

        // Sign Up Button Click - Navigate to signup form
        binding.signUpButton.setOnClickListener {
            navigateToSignUpForm()
        }

        // Skip for now Text Click
        binding.skipText.setOnClickListener {
            navigateAsGuest()
        }
    }

    private fun navigateToLoginForm() {
        val intent = Intent(this, LoginFormActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSignUpForm() {
        val intent = Intent(this, SignupFormActivity::class.java)
        startActivity(intent)
    }

    private fun navigateAsGuest() {
        // Save user as guest
        prefs.edit().apply {
            putBoolean("is_logged_in", false)
            putString("user_type", "guest")
            putString("user_name", "Guest User")
            putString("user_email", "")
            apply()
        }
        
        Toast.makeText(this, "Continuing as guest", Toast.LENGTH_SHORT).show()
        navigateToMainApp()
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spandana.MainActivity
import com.example.spandana.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Login Button Click - LoginActivity එකට යාම
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Sign Up Button Click - SignUpActivity එකට යාම
        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Skip for now Text Click - MainApp එකට යාම
        binding.skipText.setOnClickListener {
            navigateToMainApp()
        }
    }

    private fun navigateToMainApp() {
        // Set guest mode in SharedPreferences
        val editor = sharedPref.edit()
        editor.putBoolean("is_guest_mode", true)
        editor.putBoolean("is_logged_in", false)
        editor.putString("user_email", "guest@spandana.com")
        editor.apply()

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("GUEST_MODE", true)
        startActivity(intent)
        finish()
    }
}
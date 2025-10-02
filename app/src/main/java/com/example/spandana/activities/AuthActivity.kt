package com.example.spandana.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spandana.MainActivity
import com.example.spandana.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Login Button Click
        binding.loginButton.setOnClickListener {
            navigateToMainApp()
        }

        // Sign Up Button Click
        binding.signUpButton.setOnClickListener {
            navigateToMainApp()
        }

        // Skip for now Text Click
        binding.skipText.setOnClickListener {
            navigateToMainApp()
        }
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
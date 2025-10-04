package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spandana.MainActivity
import com.example.spandana.databinding.ActivitySignupFormBinding
import com.example.spandana.utils.FirebaseAuthManager
import kotlinx.coroutines.launch

class SignupFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupFormBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var firebaseAuthManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        firebaseAuthManager = FirebaseAuthManager.getInstance(this)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Sign up button
        binding.btnSignUp.setOnClickListener {
            if (validateForm()) {
                signUpUser()
            }
        }

        // Login link
        binding.tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateForm(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            binding.etName.error = "Name is required"
            return false
        }

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            return false
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun signUpUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Show loading state
        binding.btnSignUp.isEnabled = false
        binding.btnSignUp.text = "Creating account..."

        lifecycleScope.launch {
            val result = firebaseAuthManager.signUpWithEmail(name, email, password)
            
            result.fold(
                onSuccess = { user ->
                    Toast.makeText(this@SignupFormActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    navigateToMainApp()
                },
                onFailure = { exception ->
                    Toast.makeText(this@SignupFormActivity, "Sign up failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    binding.btnSignUp.isEnabled = true
                    binding.btnSignUp.text = "Sign Up"
                }
            )
        }
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

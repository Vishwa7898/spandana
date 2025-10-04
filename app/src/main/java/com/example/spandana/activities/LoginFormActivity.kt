package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spandana.MainActivity
import com.example.spandana.databinding.ActivityLoginFormBinding
import com.example.spandana.utils.FirebaseAuthManager
import kotlinx.coroutines.launch

class LoginFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginFormBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var firebaseAuthManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginFormBinding.inflate(layoutInflater)
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

        // Login button
        binding.btnLogin.setOnClickListener {
            if (validateForm()) {
                loginUser()
            }
        }

        // Sign up link
        binding.tvSignUpLink.setOnClickListener {
            val intent = Intent(this, SignupFormActivity::class.java)
            startActivity(intent)
        }

        // Forgot password link
        binding.tvForgotPassword.setOnClickListener {
            // TODO: Implement forgot password
            Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateForm(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

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

        return true
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Show loading state
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Logging in..."

        lifecycleScope.launch {
            val result = firebaseAuthManager.loginWithEmail(email, password)
            
            result.fold(
                onSuccess = { user ->
                    Toast.makeText(this@LoginFormActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMainApp()
                },
                onFailure = { exception ->
                    Toast.makeText(this@LoginFormActivity, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
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

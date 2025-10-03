package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.spandana.MainActivity
import com.example.spandana.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings
import android.util.Log
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        
        // Test Firebase connection
        Log.d("SignUpActivity", "Firebase Auth initialized: ${auth.app.name}")
        Log.d("SignUpActivity", "Current user: ${auth.currentUser?.email ?: "No user"}")
        Log.d("SignUpActivity", "Firebase project ID: ${auth.app.options.projectId}")
        Log.d("SignUpActivity", "Firebase API key: ${auth.app.options.apiKey?.take(10)}...")

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Sign Up Button Click
        binding.signUpButton.setOnClickListener {
            performSignUp()
        }

        // Back Button Click
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Login Text Click
        binding.loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performSignUp() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Email validation check
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Password strength check
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Create account with email and password
        createAccountWithEmailPassword(email, name, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun createAccountWithEmailPassword(email: String, name: String, password: String) {
        Log.d("SignUpActivity", "Creating account with email/password for: $email")
        
        // Show loading state
        binding.signUpButton.isEnabled = false
        binding.signUpButton.text = "Creating Account..."

        // Create account with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Restore button state
                binding.signUpButton.isEnabled = true
                binding.signUpButton.text = "SIGN UP"
                
                if (task.isSuccessful) {
                    Log.d("SignUpActivity", "Account created successfully for: $email")
                    
                    // Update user profile with name
                    val user = auth.currentUser
                    if (user != null && name.isNotEmpty()) {
                        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            Log.d("SignUpActivity", "Profile updated successfully")
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                            // Save login state and clear guest mode
                            saveLoginState(email)

                            // Navigate to login page
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("EMAIL", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("SignUpActivity", "Failed to update profile", profileTask.exception)
                            Toast.makeText(this, "Account created but profile update failed", Toast.LENGTH_SHORT).show()

                            // Save login state and clear guest mode
                            saveLoginState(email)

                            // Still navigate to login
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("EMAIL", email)
                            startActivity(intent)
                            finish()
                        }
                            }
                    } else {
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                        // Save login state and clear guest mode
                        saveLoginState(email)
                        
                        // Navigate to login page
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val exception = task.exception
                    Log.e("SignUpActivity", "Failed to create account", exception)
                    Log.e("SignUpActivity", "Exception message: ${exception?.message}")
                    
                    val errorMessage = when {
                        exception?.message?.contains("INVALID_EMAIL") == true -> "Invalid email address"
                        exception?.message?.contains("EMAIL_ALREADY_IN_USE") == true -> "This email is already registered. Please sign in or use a different email."
                        exception?.message?.contains("WEAK_PASSWORD") == true -> "Password is too weak. Use at least 6 characters"
                        exception?.message?.contains("TOO_MANY_ATTEMPTS") == true -> "Too many attempts. Please try again later"
                        exception?.message?.contains("NETWORK_ERROR") == true -> "Network error. Please check your connection"
                        exception is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "This email is already registered. Please sign in or use a different email."
                        else -> "Failed to create account. Error: ${exception?.message ?: "Unknown error"}"
                    }
                    
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                // Restore button state on failure
                binding.signUpButton.isEnabled = true
                binding.signUpButton.text = "SIGN UP"
                
                Log.e("SignUpActivity", "Firebase request failed", exception)
                
                val errorMessage = when {
                    exception is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "This email is already registered. Please sign in or use a different email."
                    exception?.message?.contains("EMAIL_ALREADY_IN_USE") == true -> "This email is already registered. Please sign in or use a different email."
                    else -> "Request failed. Please try again."
                }
                
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                             networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            
            Log.d("SignUpActivity", "Network available: $hasInternet")
            hasInternet
        } catch (e: Exception) {
            Log.e("SignUpActivity", "Error checking network", e)
            // If we can't check network, assume it's available and let Firebase handle the error
            true
        }
    }

    private fun saveLoginState(email: String) {
        val editor = sharedPref.edit()
        editor.putBoolean("is_logged_in", true)
        editor.putString("user_email", email)
        // Clear guest mode when user signs up
        editor.putBoolean("is_guest_mode", false)
        editor.apply()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spandana.MainActivity
import com.example.spandana.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings
import android.util.Log
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        setupClickListeners()
        
        // Pre-fill email if coming from signup
        val emailFromSignup = intent.getStringExtra("EMAIL")
        if (!emailFromSignup.isNullOrEmpty()) {
            binding.emailEditText.setText(emailFromSignup)
        }
    }

    private fun setupClickListeners() {
        // Login Button Click - Email/Password login
        binding.loginButton.setOnClickListener {
            performLoginWithEmailPassword()
        }

        // Back Button Click
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Sign Up Text Click
        binding.signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Forgot Password Click
        binding.forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLoginWithEmailPassword() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Email validation check
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Password length check
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Perform login with email and password
        loginWithEmailPassword(email, password)
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        Log.d("LoginActivity", "Logging in with email/password for: $email")
        
        // Show loading state
        binding.loginButton.isEnabled = false
        binding.loginButton.text = "Logging in..."

        // Sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Restore button state
                binding.loginButton.isEnabled = true
                binding.loginButton.text = "LOGIN"
                
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Login successful for: $email")
                    
                    // Save login state
                    saveLoginState(email)
                    
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to main app
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val exception = task.exception
                    Log.e("LoginActivity", "Login failed", exception)
                    Log.e("LoginActivity", "Exception message: ${exception?.message}")
                    
                    val errorMessage = when {
                        exception?.message?.contains("INVALID_EMAIL") == true -> "Invalid email address"
                        exception?.message?.contains("USER_DISABLED") == true -> "This account has been disabled"
                        exception?.message?.contains("USER_NOT_FOUND") == true -> "No account found with this email"
                        exception?.message?.contains("WRONG_PASSWORD") == true -> "Incorrect password"
                        exception?.message?.contains("TOO_MANY_ATTEMPTS") == true -> "Too many attempts. Please try again later"
                        exception?.message?.contains("NETWORK_ERROR") == true -> "Network error. Please check your connection"
                        exception?.message?.contains("INVALID_CREDENTIAL") == true -> "Invalid email or password"
                        else -> "Login failed. Error: ${exception?.message ?: "Unknown error"}"
                    }
                    
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                // Restore button state on failure
                binding.loginButton.isEnabled = true
                binding.loginButton.text = "LOGIN"
                
                Log.e("LoginActivity", "Firebase request failed", exception)
                
                val errorMessage = when {
                    exception?.message?.contains("INVALID_CREDENTIAL") == true -> "Invalid email or password"
                    exception?.message?.contains("USER_NOT_FOUND") == true -> "No account found with this email"
                    else -> "Login failed. Please try again."
                }
                
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun saveEmailForVerification(email: String) {
        with(sharedPref.edit()) {
            putString("email_for_verification", email)
            apply()
        }
    }

    private fun checkEmailLink(intent: Intent?) {
        val emailLink = intent?.data?.toString()

        if (emailLink != null) {
            handleEmailLink(emailLink)
        }
    }

    private fun handleEmailLink(emailLink: String) {
        // Verify this is a sign-in link
        if (auth.isSignInWithEmailLink(emailLink)) {
            val email = sharedPref.getString("email_for_verification", "")

            if (!email.isNullOrEmpty()) {
                // Sign in with email link
                auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login successful
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                            // Clear saved email
                            with(sharedPref.edit()) {
                                remove("email_for_verification")
                                apply()
                            }

                            // Go to main app
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please request a new login link", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun saveLoginState(email: String) {
        val editor = sharedPref.edit()
        editor.putBoolean("is_logged_in", true)
        editor.putString("user_email", email)
        editor.apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
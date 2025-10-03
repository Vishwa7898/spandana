package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spandana.databinding.ActivityOtpverificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserProfileChangeRequest
import android.util.Log
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpverificationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private var userEmail: String = ""
    private var isSignupMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        // Email එක ලබාගන්න
        userEmail = intent.getStringExtra("EMAIL") ?: ""
        isSignupMode = intent.getBooleanExtra("SIGNUP_MODE", false)

        setupClickListeners()
        setupUI()
        checkEmailLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkEmailLink(intent)
    }

    private fun setupUI() {
        if (isSignupMode) {
            binding.emailText.text = "Verification link sent to: $userEmail"
        } else {
            binding.emailText.text = "Login link sent to: $userEmail"
        }
    }

    private fun setupClickListeners() {
        // Open Email App Button
        binding.verifyButton.setOnClickListener {
            openEmailApp()
        }

        // Resend OTP Text
        binding.resendOtpText.setOnClickListener {
            resendOTP()
        }

        // Back Button
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun openEmailApp() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resendOTP() {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://localhost")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.spandana", true, "1")
            .build()

        auth.sendSignInLinkToEmail(userEmail, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val message = if (isSignupMode) "Verification link resent to your email" else "Login link resent to your email"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    val message = if (isSignupMode) "Failed to resend verification link" else "Failed to resend login link"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkEmailLink(intent: Intent?) {
        val link = intent?.data?.toString()
        if (link != null) {
            auth.signInWithEmailLink(userEmail, link)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (isSignupMode) {
                            handleSignupVerification()
                        } else {
                            handleLoginVerification()
                        }
                    } else {
                        Toast.makeText(this, "Invalid or expired link", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun handleSignupVerification() {
        // Get saved signup details
        val signupPref = getSharedPreferences("signup_prefs", MODE_PRIVATE)
        val name = signupPref.getString("signup_name", "") ?: ""
        val email = signupPref.getString("signup_email", "") ?: ""
        val password = signupPref.getString("signup_password", "") ?: ""

        // Update user profile with name
        val user = auth.currentUser
        if (user != null && name.isNotEmpty()) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Clear signup preferences
                        signupPref.edit().clear().apply()
                        
                        // Save login state
                        saveLoginState(email)
                        
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        
                        // Navigate to login page
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun handleLoginVerification() {
        // Save login state
        saveLoginState(userEmail)
        
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        
        // Navigate to main app
        val intent = Intent(this, com.example.spandana.MainActivity::class.java)
        startActivity(intent)
        finish()
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
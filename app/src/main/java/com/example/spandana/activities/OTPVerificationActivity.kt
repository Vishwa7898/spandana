package com.example.spandana.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spandana.databinding.ActivityOtpverificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpverificationBinding
    private lateinit var auth: FirebaseAuth
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Email එක ලබාගන්න
        userEmail = intent.getStringExtra("EMAIL") ?: ""

        setupClickListeners()
        setupUI()
    }

    private fun setupUI() {
        binding.emailText.text = "Login link sent to: $userEmail"
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
            .setUrl("https://spandana.page.link/otp")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.spandana", true, "1")
            .build()

        auth.sendSignInLinkToEmail(userEmail, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login link resent to your email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to resend login link", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
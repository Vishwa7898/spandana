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
        checkEmailLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkEmailLink(intent)
    }

    private fun setupClickListeners() {
        // Login Button Click - OTP එක්ක login කිරීම
        binding.loginButton.setOnClickListener {
            performLoginWithOTP()
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
            val email = binding.emailEditText.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
            } else {
                sendOTP(email)
            }
        }
    }

    private fun performLoginWithOTP() {
        val email = binding.emailEditText.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        // Email validation check
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // OTP send කිරීම
        sendOTP(email)
    }

    private fun sendOTP(email: String) {
        // Firebase Email Link settings
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://spandana.page.link/otp")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.example.spandana",
                true, // Install app if not available
                "1"   // Minimum version
            )
            .build()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login link sent to your email", Toast.LENGTH_SHORT).show()

                    // Email එක save කිරීම (verification සඳහා)
                    saveEmailForVerification(email)

                    // OTP verification screen එකට යාම
                    val intent = Intent(this, OTPVerificationActivity::class.java)
                    intent.putExtra("EMAIL", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to send login link: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
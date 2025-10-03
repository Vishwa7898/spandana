package com.example.spandana.services

import android.content.Context
import kotlin.random.Random

class OTPService(private val context: Context) {

    // OTP generate කිරීම (EmailJS නැතිව)
    fun generateOTP(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    // Simple OTP service (EmailJS නැතිව)
    fun sendOTPEmail(userEmail: String, otp: String): Boolean {
        // මෙය තාවකාලිකව false return කරන්න
        // පසුව Firebase එකට replace කරන්න
        return false
    }
}
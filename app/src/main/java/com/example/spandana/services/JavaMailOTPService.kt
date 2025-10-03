package com.example.spandana.services

import android.content.Context
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random

class JavaMailOTPService(private val context: Context) {

    // Gmail settings (Free)
    private val host = "smtp.gmail.com"
    private val port = "587"
    private val username = "your.email@gmail.com" // ඔබගේ Gmail
    private val password = "your_app_password" // Gmail App Password

    fun generateOTP(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    fun sendOTPEmail(userEmail: String, otp: String) {
        Thread {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", host)
                    put("mail.smtp.port", port)
                }

                val session = Session.getInstance(props,
                    object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(username, password)
                        }
                    })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(username))
                    setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(userEmail)
                    )
                    subject = "Spandana Health Tracker - OTP Verification"
                    setText("""
                        Your OTP Code is: $otp
                        
                        This OTP will expire in 10 minutes.
                        
                        Thank you for using Spandana Health Tracker!
                    """.trimIndent())
                }

                Transport.send(message)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
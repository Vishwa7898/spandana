package com.example.spandana.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import com.example.spandana.utils.HydrationManager
import com.example.spandana.utils.HydrationNotificationManager

class HydrationReminderService : Service() {

    private lateinit var hydrationManager: HydrationManager
    private lateinit var notificationManager: HydrationNotificationManager
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    companion object {
        private const val CHECK_INTERVAL = 10 * 1000L // Check every 10 seconds for testing
    }

    override fun onCreate() {
        super.onCreate()
        hydrationManager = HydrationManager.getInstance(this)
        notificationManager = HydrationNotificationManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            startReminderCheck()
        }
        return START_STICKY // Restart if killed
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startReminderCheck() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isRunning) {
                    checkAndShowReminder()
                    handler.postDelayed(this, CHECK_INTERVAL)
                }
            }
        }, CHECK_INTERVAL)
    }

    private fun checkAndShowReminder() {
        val (enabled, _) = hydrationManager.getReminderSettings()
        
        if (enabled && hydrationManager.shouldShowReminder()) {
            // Check if user is actively using the app (optional)
            // For now, we'll show reminders regardless
            notificationManager.showHydrationReminder()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}

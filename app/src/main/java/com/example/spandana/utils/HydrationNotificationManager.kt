package com.example.spandana.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.spandana.R
import com.example.spandana.activities.HydrationActivity

class HydrationNotificationManager private constructor(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "hydration_reminders"
    private val notificationId = 1001
    
    companion object {
        @Volatile
        private var INSTANCE: HydrationNotificationManager? = null
        
        fun getInstance(context: Context): HydrationNotificationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HydrationNotificationManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to drink water and stay hydrated"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showHydrationReminder() {
        val hydrationManager = HydrationManager.getInstance(context)
        val stats = hydrationManager.getTodayStats()
        
        val motivationalMessages = listOf(
            "Time to hydrate! 💧",
            "Your body needs water! 🌊",
            "Stay refreshed! 💦",
            "Don't forget to drink water! 🥤",
            "Hydration time! 💧",
            "Keep your body happy! 😊"
        )
        
        val message = if (stats.totalIntake == 0) {
            "Start your hydration journey! 💧"
        } else {
            val progress = stats.getProgressPercentage()
            when {
                progress >= 75 -> "Almost at your goal! Keep going! 💪"
                progress >= 50 -> "You're doing great! Stay hydrated! 💧"
                progress >= 25 -> "Good progress! Keep drinking water! 🌊"
                else -> motivationalMessages.random()
            }
        }
        
        val intent = Intent(context, HydrationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Hydration Reminder")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(
                R.drawable.ic_water,
                "Log Water",
                pendingIntent
            )
            .build()
        
        notificationManager.notify(notificationId, notification)
        
        // Update last reminder time
        hydrationManager.updateLastReminderTime()
    }
    
    fun cancelReminders() {
        notificationManager.cancel(notificationId)
    }
    
    fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            channel?.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            true
        }
    }
}

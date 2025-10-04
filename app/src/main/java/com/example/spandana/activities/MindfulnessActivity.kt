package com.example.spandana.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spandana.R
import com.example.spandana.databinding.ActivityMindfulnessBinding

class MindfulnessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMindfulnessBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMindfulnessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back Button
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Meditation Button
        binding.meditationButton.setOnClickListener {
            // TODO: Navigate to meditation session
            android.widget.Toast.makeText(this, "Meditation feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }

        // Breathing Exercise Button
        binding.breathingButton.setOnClickListener {
            // TODO: Navigate to breathing exercise
            android.widget.Toast.makeText(this, "Breathing exercise feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }

        // Gratitude Journal Button
        binding.gratitudeButton.setOnClickListener {
            // TODO: Navigate to gratitude journal
            android.widget.Toast.makeText(this, "Gratitude journal feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }

        // Mindful Walking Button
        binding.walkingButton.setOnClickListener {
            // TODO: Navigate to mindful walking
            Toast.makeText(this, "Mindful walking feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Music Controls
        binding.playMusicButton.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        binding.stopMusicButton.setOnClickListener {
            stopMusic()
        }
    }

    private fun playMusic() {
        try {
            if (mediaPlayer == null) {
                // For demo purposes, we'll create a simple MediaPlayer
                // In a real app, you would use actual relaxation music files
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource("https://www.soundjay.com/misc/sounds/bell-ringing-05.wav")
                mediaPlayer?.prepare()
            }
            
            mediaPlayer?.start()
            isPlaying = true
            binding.playMusicButton.text = "Pause"
            binding.musicStatusText.text = "Playing relaxation music..."
            Toast.makeText(this, "Relaxation music started", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error playing music: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.playMusicButton.text = "Play"
        binding.musicStatusText.text = "Music paused"
        Toast.makeText(this, "Music paused", Toast.LENGTH_SHORT).show()
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        binding.playMusicButton.text = "Play"
        binding.musicStatusText.text = "Music stopped"
        Toast.makeText(this, "Music stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

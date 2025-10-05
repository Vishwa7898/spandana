package com.example.spandana.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.spandana.R
import com.google.android.material.button.MaterialButton

class MeditationActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btn5Min: MaterialButton
    private lateinit var btn10Min: MaterialButton
    private lateinit var btn15Min: MaterialButton
    private lateinit var btnPause: MaterialButton
    private lateinit var btnStop: MaterialButton
    private lateinit var btnMusic: MaterialButton

    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = false
    private var timeLeftInMillis = 0L
    private var totalTimeInMillis = 0L
    private var soundHandler: android.os.Handler? = null
    private var toneGenerator: android.media.ToneGenerator? = null
    private var isTimerRunning = false
    private var currentMusicType = 0 // 0: Custom Music 1, 1: Custom Music 2, 2: Custom Music 3, 3: Custom Music 4, 4: Silence

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        initViews()
        setupClickListeners()
        setupInitialState()
    }

    private fun initViews() {
        tvTimer = findViewById(R.id.tvTimer)
        tvStatus = findViewById(R.id.tvStatus)
        btn5Min = findViewById(R.id.btn5Min)
        btn10Min = findViewById(R.id.btn10Min)
        btn15Min = findViewById(R.id.btn15Min)
        btnPause = findViewById(R.id.btnPause)
        btnStop = findViewById(R.id.btnStop)
        btnMusic = findViewById(R.id.btnMusic)
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Duration buttons
        btn5Min.setOnClickListener {
            setDuration(5 * 60 * 1000L) // 5 minutes
        }

        btn10Min.setOnClickListener {
            setDuration(10 * 60 * 1000L) // 10 minutes
        }

        btn15Min.setOnClickListener {
            setDuration(15 * 60 * 1000L) // 15 minutes
        }

        // Control buttons
        btnPause.setOnClickListener {
            if (isTimerRunning) {
                if (isPaused) {
                    resumeMeditation()
                } else {
                    pauseMeditation()
                }
            } else {
                startMeditation()
            }
        }

        btnStop.setOnClickListener {
            stopMeditation()
        }
        
        // Music selection button
        btnMusic.setOnClickListener {
            showMusicSelectionDialog()
        }
    }

    private fun setupInitialState() {
        tvTimer.text = "00:00"
        tvStatus.text = "Select duration to begin"
        btnPause.isEnabled = false
        btnStop.isEnabled = false
        btnPause.text = "Start"

        // Enable duration buttons initially
        btn5Min.isEnabled = true
        btn10Min.isEnabled = true
        btn15Min.isEnabled = true

        isTimerRunning = false
        isPaused = false
    }

    private fun setDuration(duration: Long) {
        totalTimeInMillis = duration
        timeLeftInMillis = duration
        updateTimerDisplay()

        btnPause.isEnabled = true
        btnStop.isEnabled = true
        btnPause.text = "Start"

        // Disable duration buttons when duration is selected
        btn5Min.isEnabled = false
        btn10Min.isEnabled = false
        btn15Min.isEnabled = false

        isTimerRunning = false
        isPaused = false
    }

    private fun startMeditation() {
        tvStatus.text = "Meditation in progress..."
        btnPause.text = "Pause"
        isTimerRunning = true
        isPaused = false

        // Disable duration buttons
        btn5Min.isEnabled = false
        btn10Min.isEnabled = false
        btn15Min.isEnabled = false

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                completeMeditation()
            }
        }.start()

        // Start custom meditation music
        playCustomMusic(currentMusicType)
    }

    private fun pauseMeditation() {
        countDownTimer?.cancel()
        tvStatus.text = "Meditation paused"
        btnPause.text = "Resume"
        isPaused = true
        isTimerRunning = false

        // Pause meditation sound
        pauseMeditationSound()
    }

    private fun resumeMeditation() {
        startMeditation()
    }

    private fun stopMeditation() {
        countDownTimer?.cancel()
        stopMeditationSound()

        // Reset to initial state
        setupInitialState()
        timeLeftInMillis = 0L
        totalTimeInMillis = 0L
        isPaused = false
        isTimerRunning = false

        // Re-enable duration buttons
        btn5Min.isEnabled = true
        btn10Min.isEnabled = true
        btn15Min.isEnabled = true
    }

    private fun completeMeditation() {
        tvStatus.text = "Meditation completed! 🎉"
        btnPause.isEnabled = false
        btnStop.isEnabled = false
        isTimerRunning = false

        stopMeditationSound()

        // Show completion message
        android.widget.Toast.makeText(this, "Great job! You completed your meditation session.", android.widget.Toast.LENGTH_LONG).show()

        // Re-enable duration buttons after completion
        btn5Min.isEnabled = true
        btn10Min.isEnabled = true
        btn15Min.isEnabled = true
    }

    private fun updateTimerDisplay() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun startMeditationSound() {
        try {
            // Create a gentle meditation tone
            toneGenerator = android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 30)
            soundHandler = android.os.Handler(android.os.Looper.getMainLooper())
            
            // Start the meditation sound loop
            playMeditationTone()
            
        } catch (e: Exception) {
            // If sound doesn't work, continue without sound
            e.printStackTrace()
        }
    }
    
    private fun playMeditationTone() {
        // All sounds are now handled by MediaPlayer in playCustomMusic()
        // This function is kept for compatibility but doesn't do anything
        // Custom music plays continuously via MediaPlayer
    }

    private fun pauseMeditationSound() {
        // Stop the sound loop
        soundHandler?.removeCallbacksAndMessages(null)
        
        // Pause custom music if playing
        mediaPlayer?.pause()
    }

    private fun stopMeditationSound() {
        // Stop the sound loop
        soundHandler?.removeCallbacksAndMessages(null)
        
        // Stop custom music
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        
        // Release tone generator
        toneGenerator?.release()
        toneGenerator = null
        soundHandler = null
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        stopMeditationSound()
    }

    override fun onPause() {
        super.onPause()
        if (isTimerRunning && !isPaused) {
            pauseMeditation()
        }
    }
    
    private fun showMusicSelectionDialog() {
        val musicOptions = arrayOf("🔔 Meditation 1", "🌊 Meditation 2", "🐦 Meditation 3", "🎵 Custom Sound", "🔇 Silent")
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Choose Meditation Sound")
            .setItems(musicOptions) { _, which ->
                currentMusicType = which
                updateMusicButton()
                playCustomMusic(which)
                android.widget.Toast.makeText(this, "Music changed to: ${musicOptions[which]}", android.widget.Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    private fun updateMusicButton() {
        val musicTexts = arrayOf("🔔", "🌊", "🐦", "🎵", "🔇")
        btnMusic.text = musicTexts[currentMusicType]
    }
    
    
    private fun showAddMusicInstructions() {
        val instructions = """
            To add custom meditation music:
            
            1. Download MP3 files from:
               - Freesound.org (free)
               - Pixabay Music (free)
               - YouTube Audio Library (free)
            
            2. Place files in: app/src/main/res/raw/
               - meditation_1.mp3 (Bell meditation)
               - meditation_2.mp3 (Ocean waves)
               - meditation_3.mp3 (Nature sounds)
               - custom_sound.mp3 (Ambient music)
            
            3. Supported formats: MP3, WAV, OGG
            4. File size: Under 5MB recommended
            5. Length: 3-10 minutes for loops
            
            Current files in raw folder:
            ✓ meditation_1.mp3
            ✓ meditation_2.mp3
            ✓ meditation_3.mp3
            ✓ custom_sound.mp3
        """.trimIndent()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("How to Add Custom Music")
            .setMessage(instructions)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun playCustomMusic(musicIndex: Int) {
        try {
            // Stop any existing sound
            stopMeditationSound()
            
            if (musicIndex == 4) { // Silent
                android.widget.Toast.makeText(this, "Silent meditation mode", android.widget.Toast.LENGTH_SHORT).show()
                return
            }
            
            // Create MediaPlayer for custom music
            val musicResources = arrayOf(
                R.raw.meditation_1,       // Bell meditation sound
                R.raw.meditation_2,       // Ocean waves sound
                R.raw.meditation_3,       // Nature sounds
                R.raw.custom_sound        // Ambient music
            )
            
            if (musicIndex < musicResources.size) {
                mediaPlayer = MediaPlayer.create(this, musicResources[musicIndex])
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
                
                android.widget.Toast.makeText(this, "Custom music started!", android.widget.Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, "Music file not found. Please add music files to res/raw/ folder.", android.widget.Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
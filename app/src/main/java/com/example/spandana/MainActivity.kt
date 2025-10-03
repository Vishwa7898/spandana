package com.example.spandana

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spandana.activities.AuthActivity
import com.example.spandana.activities.ProfileActivity
import com.example.spandana.databinding.ActivityMainBinding
import com.example.spandana.fragments.CategoriesFragment
import com.example.spandana.fragments.HomeFragment
import com.example.spandana.fragments.ProfileFragment
import com.example.spandana.fragments.TrendsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private var isGuestMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        
        // Check if in guest mode
        isGuestMode = intent.getBooleanExtra("GUEST_MODE", false) || 
                     sharedPref.getBoolean("is_guest_mode", false)

        // පළමු fragment එක load කිරීම
        replaceFragment(HomeFragment())

        // Bottom navigation setup - නව method එක use කිරීම
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_today -> replaceFragment(HomeFragment())
                R.id.nav_categories -> replaceFragment(CategoriesFragment())
                R.id.nav_trends -> replaceFragment(TrendsFragment())
                R.id.nav_profile -> {
                    if (isGuestMode) {
                        // Guest mode - show login prompt
                        showGuestLoginPrompt()
                    } else {
                        // Navigate to ProfileActivity instead of fragment
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showGuestLoginPrompt() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Login Required")
            .setMessage("You're currently using the app as a guest. To access your profile and save your data, please login or create an account.")
            .setPositiveButton("Login") { _, _ ->
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Continue as Guest") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
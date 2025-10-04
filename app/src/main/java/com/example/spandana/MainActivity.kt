package com.example.spandana

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spandana.activities.AuthActivity
import com.example.spandana.databinding.ActivityMainBinding
import com.example.spandana.fragments.CategoriesFragment
import com.example.spandana.fragments.HomeFragment
import com.example.spandana.fragments.ProfileFragment
import com.example.spandana.fragments.TrendsFragment
import com.example.spandana.utils.ThemeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var themeManager: ThemeManager
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize theme before super.onCreate()
        themeManager = ThemeManager.getInstance(this)
        themeManager.init()
        
        super.onCreate(savedInstanceState)
        
        // Check authentication status
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val userType = prefs.getString("user_type", "")
        
        // If not logged in and not a guest user, redirect to AuthActivity
        if (!isLoggedIn && userType != "guest") {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load first fragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_today -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_categories -> {
                    replaceFragment(CategoriesFragment())
                    true
                }
                R.id.nav_trends -> {
                    replaceFragment(TrendsFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
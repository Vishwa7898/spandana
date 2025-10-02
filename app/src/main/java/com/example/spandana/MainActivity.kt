package com.example.spandana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spandana.databinding.ActivityMainBinding
import com.example.spandana.fragments.CategoriesFragment
import com.example.spandana.fragments.HomeFragment
import com.example.spandana.fragments.ProfileFragment
import com.example.spandana.fragments.TrendsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // පළමු fragment එක load කිරීම
        replaceFragment(HomeFragment())

        // Bottom navigation setup - නව method එක use කිරීම
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_today -> replaceFragment(HomeFragment())
                R.id.nav_categories -> replaceFragment(CategoriesFragment())
                R.id.nav_trends -> replaceFragment(TrendsFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
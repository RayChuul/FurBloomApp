package com.example.furbloomappmsd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FIXED: The ID 'custom_toolbar' belongs to the MaterialToolbar *inside* the included layout.
        // The original findViewById call was correct, and this ensures it finds the toolbar.
        // This was the root cause of the crash on launch.
        val toolbar: MaterialToolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set the initial fragment and title
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment()).commit()
            supportActionBar?.title = "Home"
        }

        bottomNav.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            var title: String = "Home"

            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                    title = "Home"
                }
                R.id.nav_calendar -> {
                    selectedFragment = CalendarFragment()
                    title = "Calendar"
                }
                R.id.nav_settings -> {
                    selectedFragment = SettingsFragment()
                    title = "Settings"
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment).commit()
                supportActionBar?.title = title
            }

            true
        }
    }
}

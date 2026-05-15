package com.example.rakta_seva

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rakta_seva.fragments.AlertsFragment
import com.example.rakta_seva.fragments.DonorsFragment
import com.example.rakta_seva.fragments.ProfileFragment
import com.example.rakta_seva.fragments.RequestFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // Initial fragment
        loadFragment(AlertsFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_alerts -> {
                    loadFragment(AlertsFragment())
                    true
                }
                R.id.navigation_request -> {
                    loadFragment(RequestFragment())
                    true
                }
                R.id.navigation_donors -> {
                    loadFragment(DonorsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
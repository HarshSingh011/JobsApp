package com.example.main_project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)

        // Apply edge-to-edge rendering and handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up BottomNavigationView and NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupCustomNavigation(bottomNavigationView, navController)
    }

    private fun setupCustomNavigation(bottomNavigationView: BottomNavigationView, navController: NavController) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.jobs -> {
                    navController.navigate(R.id.searchJob)
                    true
                }
                R.id.community -> {
                    // Example: Handle community navigation or do nothing
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.candidateProfile)
                    true
                }
                R.id.home -> {
                    navController.navigate(R.id.notification)
                    true
                }
                else -> false
            }
        }
    }
}

package com.example.main_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupCustomNavigation(bottomNavigationView, navController)

        println("MainActivity4 is being loaded successfully")

    }

    private fun setupCustomNavigation(bottomNavigationView: BottomNavigationView, navController: NavController) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.jobs -> {
                    navController.navigate(R.id.searchJob)
                    true
                }
                R.id.community -> {
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

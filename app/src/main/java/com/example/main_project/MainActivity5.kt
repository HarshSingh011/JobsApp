package com.example.main_project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupCustomNavigation(bottomNavigationView, navController)
    }

    private fun setupCustomNavigation(bottomNavigationView: BottomNavigationView,navController: NavController) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.jobs -> {
                    navController.navigate(R.id.postAJob)
                    true
                }
                R.id.community -> {
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.recruiterDetails)
                    true
                }
                R.id.home -> {
                    navController.navigate(R.id.jobPosted)
                    true
                }
                else -> false
            }
        }
    }
}

package com.example.main_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity4 : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navigationView = findViewById<NavigationView>(R.id.navigation_drawer)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(navigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        setupCustomNavigation(bottomNavigationView, navigationView)
    }

    private fun setupCustomNavigation(bottomNavigationView: BottomNavigationView, navigationView: NavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.searchJob)
                    selectDrawerItem(R.id.home)
                    true
                }
                R.id.jobs -> {
                    navController.navigate(R.id.seeAllJobs)
                    selectDrawerItem(R.id.jobs)
                    true
                }
                R.id.applications -> {
                    navController.navigate(R.id.applications)
                    selectDrawerItem(R.id.applications)
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.candidateProfile)
                    selectDrawerItem(R.id.profile)
                    true
                }
                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.searchJob)
                    selectBottomItem(R.id.home)
                }
                R.id.profile -> {
                    navController.navigate(R.id.candidateProfile)
                    selectBottomItem(R.id.profile)
                }
                R.id.jobs -> {
                    navController.navigate(R.id.seeAllJobs)
                    selectBottomItem(R.id.jobs)
                }
                R.id.applications -> {
                    navController.navigate(R.id.applications)
                    selectBottomItem(R.id.applications)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun selectDrawerItem(itemId: Int) {
        findViewById<NavigationView>(R.id.navigation_drawer).setCheckedItem(itemId)
    }

    private fun selectBottomItem(itemId: Int) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = itemId
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}

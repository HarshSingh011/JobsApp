package com.example.main_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity5 : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navigationView = findViewById<NavigationView>(R.id.navigation_drawer)

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
                    navController.navigate(R.id.jobPosted)
                    selectDrawerItem(R.id.home)
                    true
                }
                R.id.jobs -> {
                    navController.navigate(R.id.buttonPostAJob)
                    selectDrawerItem(R.id.jobs)
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.recruiterDetails)
                    selectDrawerItem(R.id.profile)
                    true
                }
                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.jobPosted)
                    selectBottomItem(R.id.home)
                }
                R.id.jobs -> {
                    navController.navigate(R.id.buttonPostAJob)
                    selectBottomItem(R.id.jobs)
                }
                R.id.profile -> {
                    navController.navigate(R.id.recruiterDetails)
                    selectBottomItem(R.id.profile)
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

package com.example.main_project.SettingProfile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.main_project.R
import com.example.main_project.TabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2

class MainActivity2 : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize ViewPager2 and TabLayout
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        // Set up the adapter for ViewPager2
        val tabAdapter = TabAdapter(this)
        viewPager.adapter = tabAdapter

        // Attach TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Set tab titles
            tab.text = when (position) {
                0 -> "Your Profile"
                1 -> "Experience"
                2 -> "Certificates"
                else -> "Tab $position"
            }
        }.attach()
    }
}

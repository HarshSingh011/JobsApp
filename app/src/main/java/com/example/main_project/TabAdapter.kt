package com.example.main_project

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.main_project.SettingProfile.Fragments.YourProfile
import com.example.main_project.SettingProfile.Fragments.Certificates
import com.example.main_project.SettingProfile.Fragments.ExprienceRecord

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val tabCount = 3

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> YourProfile()
            1 -> ExprienceRecord()
            2 -> Certificates()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}

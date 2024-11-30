package com.example.main_project.SettingProfile.ViewModels

import androidx.lifecycle.ViewModel
import com.example.main_project.SettingProfile.DataClasses.Education
import com.example.main_project.SettingProfile.DataClasses.Experience

class CandidateViewModel : ViewModel(){
    var role : String = ""
    var name_intitute : String = ""
    var degree : String = ""
    var year_of_completion : String = ""
    var domain : String = ""
    var exp : String = ""
    var company_name  : String = ""
    var position  : String = ""
    var year_of_Work : String = ""
    var educationList = mutableListOf<Education>()
    var experienceList = mutableListOf<Experience>()
    var isApiSuccess = false
    var DOB : String = ""
}

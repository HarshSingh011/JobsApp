package com.example.main_project.Recruiter.DataClasses

data class JobRequest(
    val title: String,
    val description: String,
    val location: String,
    val experience: Int,
    val minSalary: Int,
    val maxSalary: Int,
    val employmentType: String,
    val requiredSkills: List<String>
)


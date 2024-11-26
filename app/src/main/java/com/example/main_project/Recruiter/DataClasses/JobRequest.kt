package com.example.main_project.Recruiter.DataClasses

data class JobRequest(
    val title: String,
    val description: String,
    val location: String,
    val experience: Int,
    val jobType: String,
    val jobMode: String,
    val minSalary: Int,
    val maxSalary: Int,
    val requiredSkills: List<String>
)


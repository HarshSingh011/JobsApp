package com.example.main_project.SeeJobs.DataClasses

data class Job(
    val title: String,
    val description: String,
    val location: String,
    val minSalary: Int,
    val maxSalary: Int,
    val requiredSkills: List<String>,
    val experience: Int
)

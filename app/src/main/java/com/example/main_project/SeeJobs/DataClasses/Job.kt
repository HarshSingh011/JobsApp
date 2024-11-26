package com.example.main_project.SeeJobs.DataClasses

data class Job(
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val minSalary: Int,
    val maxSalary: Int,
    val mode: String?,
    val jobType: String?,
    val requiredSkills: List<String>,
    val experience: Int
)

data class JobApplyResponse(
    val message: String
)

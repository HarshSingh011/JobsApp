package com.example.main_project.SeeJobs.DataClasses

data class SearchJobData(
    val title: String,
    val company: String,
    val type: String,
    val location: String,
    val experience: String,
    val salary: String
)


data class JobApplication(
    val id: Long,
    val applicant: Applicant,
    val job: Job,
    val appliedAt: String?,
    val status: String
)

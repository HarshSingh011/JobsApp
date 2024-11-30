package com.example.main_project.Recruiter.DataClasses

import com.example.main_project.SeeJobs.DataClasses.Job


data class ApplicantsResponse(
    val applicants: List<ApplicantApplication>
)
data class ApplicantContent(
    val id: Long,
    val applicant: Applicant
)

data class UpdateStatusResponse(
    val message: String
)

data class UpdateStatusBody(
    val status: String
)

data class ApplicantApplication(
    val id: Long,
    val applicant: Applicant,
    val job: Job,
    val appliedAt: String,
    val status: String
)

data class Applicant(
    val id: Long,
    val skills: List<String>,
    val resumeKey: String?,
    val portfolioKey: String?,
    val profileImageKey: String?,
    val dob: String?
)


data class ProfilePictureResponse(
    val message: String
)

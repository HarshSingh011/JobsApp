package com.example.main_project.candidate.DataClasses

import com.example.main_project.SettingProfile.DataClasses.Educations
import com.example.main_project.SettingProfile.DataClasses.Experience

data class CandidateDataGet(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val education: List<Educations>,
    val experience: List<Experience>,
    val skill: List<String>,
    val certificate: List<Certificate>,
    val resumeKey: String?,
    val profileImageKey: String?,
    val dob: String?
)
data class Certificate(
    val id: String?,
    val certificateName: String?,
    val issuedBy: String?,
    val fileKey: String?
)


data class UpdateCandidateRequest(
    val education: List<UpdateEducation>,
    val experiences: List<UpdateExperience>,
    val skill: List<String>
)

data class UpdateEducation(
    val institution: String,
    val degree: String,
    val yearOfCompletion: Int
)

data class UpdateExperience(
    val companyName: String,
    val yearsWorked: Int,
    val position: String
)

data class NotificationModel(
    val id: Long,
    val title: String,
    val message: String
)


data class ResumeResponseBody(
    val message: String
)
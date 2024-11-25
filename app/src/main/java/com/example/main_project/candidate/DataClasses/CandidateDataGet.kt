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
    val certificateId: String?,
    val certificateName: String?,
    val issuedBy: String?,
    val fileKey: String?
)
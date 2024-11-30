package com.example.main_project.SettingProfile.DataClasses

data class CandidateData(
    var education: MutableList<Education> = mutableListOf(),
    var experience: MutableList<Experience> = mutableListOf(),
    var skill: List<String>,
    var DOB: String
)

data class ProfilePictureUploadResponse(
    val message: String
)
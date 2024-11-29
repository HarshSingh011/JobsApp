package com.example.main_project.SettingProfile.DataClasses

data class CandidateData(
    var educations: MutableList<Educations> = mutableListOf(),
    var experiences: MutableList<Experience> = mutableListOf(),
    var skill: List<String>,
    var DOB: String
)

data class ProfilePictureUploadResponse(
    val message: String
)
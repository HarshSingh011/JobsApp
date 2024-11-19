package com.example.main_project

data class CandidateData(
    val education: List<Education>,
    val experiences: List<ExprienceRecord>,
    val skill: String
)

data class Education(
    val institution: String,
    val degree: String,
    val yearOfCompletion: Int
)

data class Experience(
    val companyName: String,
    val yearsWorked: Int,
    val position: String
)
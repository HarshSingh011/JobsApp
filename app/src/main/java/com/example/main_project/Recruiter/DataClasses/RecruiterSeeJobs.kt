package com.example.main_project.Recruiter.DataClasses

data class RecruiterSeeJobsResponse(
    val content: List<JobContent>
)

data class JobContent(
    val id: Long,
    val title: String?,
    val description: String?,
    val location: String?,
    val experience: Int,
    val jobType: String?,
    val mode: String?,
    val minSalary: Int?,
    val maxSalary: Int?,
    val requiredSkills: List<String>?,
    val postedBy: PostedBy
)

data class PostedBy(
    val id: Long,
    val user: User,
    val companyEmail: String,
    val companyName: String,
    val jobTitle: String,
    val companyWebsite: String,
    val companyLocation: String,
    val industry: String,
    val profileImage: String?
)

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String?,
    val email: String,
    val mobile: String?,
    val status: String?,
    val membership: Boolean,
    val enabled: Boolean,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val authorities: List<Authority>
)

data class Authority(
    val authority: String
)


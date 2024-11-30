package com.example.main_project.Auth.DataClasses

data class LoginResponse(
    val token: String?,
    val message: String?,
    val user: User?
)

data class User(
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val mobile: String?,
    val status: String?,
    val membership: Boolean,
    val role: String, // Ensure role is included here
    val enabled: Boolean
)


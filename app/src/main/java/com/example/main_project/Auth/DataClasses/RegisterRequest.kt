package com.example.main_project.Auth.DataClasses

data class RegisterRequestEmail(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)


data class RegisterRequestPhone(
    val firstName: String,
    val lastName: String,
    val mobile: String,
    val password: String
)



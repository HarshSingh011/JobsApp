package com.example.main_project.Auth.DataClasses

data class NewPasswordFormedRequest(
    val contact: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)

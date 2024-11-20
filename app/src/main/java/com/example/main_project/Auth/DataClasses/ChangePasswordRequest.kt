package com.example.main_project.Auth.DataClasses

data class ChangePasswordRequest(
    val contact: String = "",
    val otp: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)

package com.example.main_project

data class NewPasswordFormedRequest(
    val contact: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)

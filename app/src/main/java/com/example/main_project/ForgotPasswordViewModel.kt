package com.example.main_project

import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel : ViewModel() {
    var contact: String = ""
    var newPassword: String = ""
    var confirmPassword: String = ""
}
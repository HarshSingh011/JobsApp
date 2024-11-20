package com.example.main_project.Auth.ViewModels

import com.example.main_project.Auth.DataClasses.ForgotPasswordRequest
import com.example.main_project.Auth.DataClasses.ForgotPasswordResponse
import androidx.lifecycle.ViewModel
import com.example.main_project.Auth.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordViewModel : ViewModel() {
    var contact: String = ""
    var newPassword: String = ""
    var confirmPassword: String = ""

    fun sendForgotPasswordRequest(
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val request = ForgotPasswordRequest(contact)

        RetrofitClient.instance.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful) {
                    onSuccess?.invoke()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    onError?.invoke(errorMessage)
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                val errorMessage = "Request failed: ${t.message}"
                onError?.invoke(errorMessage)
            }
        })
    }

}
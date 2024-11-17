package com.example.main_project

import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    var firstName: String = ""
    var lastName: String = ""
    var email: String = ""
    var password: String = ""

    fun sendDataToApi(onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        val request = RegisterRequestEmail(firstName, lastName, email, password)

        RetrofitClient.instance.registerEmail(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess?.invoke()
                } else {
                    val errorMessage = "Failed to resend OTP"
                    onError?.invoke(errorMessage)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val errorMessage = "Request failed: ${t.message}"
                onError?.invoke(errorMessage)
            }
        })
    }
}

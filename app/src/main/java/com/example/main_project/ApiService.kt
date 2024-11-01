package com.example.main_project

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/auth/authenticate")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/v1/auth/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

}

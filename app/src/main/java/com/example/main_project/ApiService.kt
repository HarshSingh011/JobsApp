package com.example.main_project

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @POST("authenticate")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @POST("register")
    fun registerEmail(@Body request: RegisterRequestEmail): Call<RegisterResponse>

    @POST("register")
    fun registerPhone(@Body request: RegisterRequestPhone): Call<RegisterResponse>

    @POST("validate")
    fun validateOtp(@Body request: OtpRequest): Call<OtpResponse>

    @POST("verify-otp-forgotpassword")
    fun forgotPasswordOTP(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

    @PUT("change-password")
    fun changePassword(@Body request: NewPasswordFormedRequest): Call<ChangePasswordResponse>
}

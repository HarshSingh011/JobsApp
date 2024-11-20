package com.example.main_project.Auth

import com.example.main_project.Auth.DataClasses.ChangePasswordRequest
import com.example.main_project.Auth.DataClasses.ChangePasswordResponse
import com.example.main_project.Auth.DataClasses.ForgotPasswordRequest
import com.example.main_project.Auth.DataClasses.ForgotPasswordResponse
import com.example.main_project.Auth.DataClasses.LoginRequest
import com.example.main_project.Auth.DataClasses.LoginResponse
import com.example.main_project.Auth.DataClasses.NewPasswordFormedRequest
import com.example.main_project.Auth.DataClasses.OtpRequest
import com.example.main_project.Auth.DataClasses.OtpResponse
import com.example.main_project.Auth.DataClasses.RegisterRequestEmail
import com.example.main_project.Auth.DataClasses.RegisterRequestPhone
import com.example.main_project.Auth.DataClasses.RegisterResponse
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

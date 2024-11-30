package com.example.main_project.SettingProfile

import android.content.Context
import android.util.Log
import com.example.main_project.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthTokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = runBlocking {
            DataStoreManager(context).getToken().first()
        }
        Log.d("AuthTokenInterceptor", "Token: ${token?.take(10)}...")

        val originalRequest = chain.request()
        val newRequest: Request = originalRequest.newBuilder().apply {
            token?.let {
                header("Authorization", "Bearer $it")
                Log.d("AuthTokenInterceptor", "Added Authorization header")
            } ?: Log.e("AuthTokenInterceptor", "No token available")
        }.build()

        Log.d("AuthTokenInterceptor", "Request URL: ${newRequest.url}")
        Log.d("AuthTokenInterceptor", "Request Method: ${newRequest.method}")
        Log.d("AuthTokenInterceptor", "Request Headers: ${newRequest.headers}")

        val response = chain.proceed(newRequest)
        Log.d("AuthTokenInterceptor", "Response code: ${response.code}")
        return response
    }
}


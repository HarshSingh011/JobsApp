package com.example.main_project.SettingProfile

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CandidateProfileRetrofitClient {

    private const val BASE_URL = "https://naitikjain.me/api/"

    fun instance(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthTokenInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

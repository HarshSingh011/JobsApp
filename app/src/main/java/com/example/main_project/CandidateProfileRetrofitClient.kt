package com.example.main_project

import android.content.Context
import com.example.main_project.SettingProfile.AuthTokenInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CandidateProfileRetrofitClient {

    private const val BASE_URL = "https://naitikjain.me/api/"
    private var retrofit: Retrofit? = null

    fun instance(context: Context): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthTokenInterceptor(context))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}


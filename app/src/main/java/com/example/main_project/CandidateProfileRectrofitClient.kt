package com.example.main_project

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CandidateProfileRectrofitClient {
    private const val BASE_URL = "https://naitikjain.me/api/"

    val instance: CandidateInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CandidateInterface::class.java)
    }
}

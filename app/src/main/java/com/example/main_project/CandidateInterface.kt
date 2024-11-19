package com.example.main_project

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CandidateInterface {

    @Headers("Content-Type: application/json")
    @POST("candidates/create")
    fun createCandidate(
        @Body candidateData: CandidateData
    ): Call<CandidateDataResponse>
}

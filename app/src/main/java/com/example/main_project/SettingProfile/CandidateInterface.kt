package com.example.main_project.SettingProfile

import com.example.main_project.SettingProfile.DataClasses.CandidateData
import com.example.main_project.SettingProfile.DataClasses.CandidateDataResponse
import com.example.main_project.SettingProfile.DataClasses.CertificateUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CandidateInterface {

    @Headers("Content-Type: application/json")
    @POST("candidates/create")
    suspend fun createCandidate(
        @Body candidateData: CandidateData
    ): Response<CandidateDataResponse>

    @Multipart
    @POST("candidates/certificate")
    suspend fun uploadCertificate(
        @Part("certificateName") certificateName: RequestBody,
        @Part certificateData: MultipartBody.Part
    ): Response<CertificateUploadResponse>
}


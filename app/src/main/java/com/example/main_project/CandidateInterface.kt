package com.example.main_project

import com.example.main_project.Recruiter.DataClasses.RecruiterData
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateData
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateResponse
import com.example.main_project.SettingProfile.DataClasses.CandidateData
import com.example.main_project.candidate.DataClasses.CandidateDataResponse
import com.example.main_project.SettingProfile.DataClasses.CertificateUploadResponse
import com.example.main_project.SettingProfile.DataClasses.RequiterRequest
import com.example.main_project.SettingProfile.DataClasses.RequiterResponse
import com.example.main_project.candidate.DataClasses.CandidateDataGet
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @Multipart
    @POST("candidates/resume")
    suspend fun uploadResume(
        @Part resumeData: MultipartBody.Part
    ): Response<CertificateUploadResponse>

    @Headers("Content-Type: application/json")
    @POST("recruiter/create")
    suspend fun createRecruiter(
        @Body recruiterData: RequiterRequest
    ): Response<RequiterResponse>


    @GET("candidates/get-current")
    suspend fun getCurrentCandidate(): Response<CandidateDataGet>

    @GET("recruiter/current-recruiter")
    suspend fun getCurrentRecruiter(): Response<RecruiterData>

    @Headers("Content-Type: application/json")
    @PUT("recruiter/update")
    suspend fun updateRecruiter(
        @Body recruiterUpdateData: RecruiterUpdateData
    ): Response<RecruiterUpdateResponse>

}


package com.example.main_project

import com.example.main_project.Recruiter.DataClasses.JobRequest
import com.example.main_project.Recruiter.DataClasses.JobResponse
import com.example.main_project.Recruiter.DataClasses.RecruiterData
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateData
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateResponse
import com.example.main_project.SeeJobs.Adapter.JobShowResponse
import com.example.main_project.SeeJobs.DataClasses.JobApplyResponse
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
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap

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
    @POST("recruiter/create")
    suspend fun createRecruiter(
        @Body recruiterData: RequiterRequest
    ): Response<RequiterResponse>

    @GET("candidates/get-current")
    suspend fun getCurrentCandidate(): Response<CandidateDataGet>

    @GET("recruiter/current-recruiter")
    suspend fun getCurrentRecruiter(): Response<RecruiterData>
    @PUT("recruiter/update")
    suspend fun updateRecruiter(
        @Body recruiterUpdateData: RecruiterUpdateData
    ): Response<RecruiterUpdateResponse>
    @POST("jobs/post")
    suspend fun postJob(@Body jobRequest: JobRequest): Response<JobResponse>
    @GET("jobs/all-jobs")
    suspend fun getJobs(): Response<JobShowResponse>
    @PATCH("candidates/update")
    suspend fun updateCandidate(@Body data: Map<String, Any>): Response<CandidateDataResponse>
    @POST("jobs/apply/applications/{id}")
    suspend fun applyForJob(@Path("id") id: String): Response<JobApplyResponse>

    @GET("jobs/filter")
    suspend fun getJobsWithFilters(@QueryMap filters: Map<String, String>): Response<JobShowResponse>
}



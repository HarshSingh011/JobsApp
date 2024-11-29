package com.example.main_project

import android.telecom.Call
import com.example.main_project.Recruiter.DataClasses.ApplicantApplication
import com.example.main_project.Recruiter.DataClasses.ApplicantsResponse
import com.example.main_project.Recruiter.DataClasses.JobContent
import com.example.main_project.Recruiter.DataClasses.JobRequest
import com.example.main_project.Recruiter.DataClasses.JobResponse
import com.example.main_project.Recruiter.DataClasses.RecruiterData
import com.example.main_project.Recruiter.DataClasses.RecruiterSeeJobsResponse
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateData
import com.example.main_project.Recruiter.DataClasses.RecruiterUpdateResponse
import com.example.main_project.Recruiter.DataClasses.UpdateStatusBody
import com.example.main_project.Recruiter.DataClasses.UpdateStatusResponse
import com.example.main_project.SeeJobs.DataClasses.Job
import com.example.main_project.SeeJobs.DataClasses.JobShowResponse
import com.example.main_project.SeeJobs.DataClasses.JobApplyResponse
import com.example.main_project.SettingProfile.DataClasses.CandidateData
import com.example.main_project.candidate.DataClasses.CandidateDataResponse
import com.example.main_project.SettingProfile.DataClasses.CertificateUploadResponse
import com.example.main_project.SettingProfile.DataClasses.ProfilePictureUploadResponse
import com.example.main_project.SettingProfile.DataClasses.RequiterRequest
import com.example.main_project.SettingProfile.DataClasses.RequiterResponse
import com.example.main_project.candidate.DataClasses.CandidateDataGet
import com.example.main_project.candidate.DataClasses.NotificationModel
import com.example.main_project.candidate.DataClasses.ResumeResponseBody
import com.example.main_project.candidate.DataClasses.UpdateCandidateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
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
    @PATCH("recruiter/update")
    suspend fun updateRecruiter(
        @Body recruiterUpdateData: RecruiterUpdateData
    ): Response<RecruiterUpdateResponse>
    @POST("jobs/post")
    suspend fun postJob(@Body jobRequest: JobRequest): Response<JobResponse>
    @GET("jobs/all-jobs")
    suspend fun getJobs(): Response<List<Job>>

    @PATCH("candidates/update")
    suspend fun updateCandidate(@Body data: UpdateCandidateRequest): Response<CandidateDataResponse>
    @POST("jobs/apply/applications/{id}")
    suspend fun applyForJob(@Path("id") id: String): Response<JobApplyResponse>

    @GET("jobs/filter")
    suspend fun getJobsWithFilters(@QueryMap filters: Map<String, String>): Response<List<Job>>

    @DELETE("candidates/delete-resume")
    suspend fun deleteResume(): Response<ResumeResponseBody>

    @DELETE("candidates/certificate/{id}")
    suspend fun deleteCertificate(@Path("id") certificateId: String): Response<Unit>

    @GET("jobs/recruiter")
    suspend fun getrecruiterpostedJobs(): Response<List<JobContent>>

    @GET("jobs/applications/{jobId}")
    suspend fun getJobApplications(@Path("jobId") jobId: Long): Response<List<ApplicantApplication>>

    @POST("jobs/application/update-status/{id}")
    suspend fun updateApplicationStatus(
        @Path("id") applicantId: Long,
        @Body status: UpdateStatusBody
    ): Response<UpdateStatusResponse>

    @Multipart
    @POST("candidates/Profile-picture")
    suspend fun uploadProfilePicture(
        @Part image: MultipartBody.Part
    ): Response<ProfilePictureUploadResponse>

    @GET("get-all")
    suspend fun getNotifications(): Response<List<NotificationModel>>

}



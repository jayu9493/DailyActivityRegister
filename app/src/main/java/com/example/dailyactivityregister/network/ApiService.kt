package com.example.dailyactivityregister.network

import com.example.dailyactivityregister.Project
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Multipart
    @POST("api/android/projects/upload")
    suspend fun uploadProjectFile(@Part file: MultipartBody.Part): Project

    @GET("api/android/projects")
    suspend fun getProjects(): List<Project>

    @POST("api/android/projects/create")
    suspend fun createProject(@Body projectData: ProjectCreateRequest): Project

    @PUT("api/android/projects/{projectName}")
    suspend fun updateProject(@Path("projectName") projectName: String, @Body project: Project): Project

    @DELETE("api/android/projects/{projectName}")
    suspend fun deleteProject(@Path("projectName") projectName: String, @Query("password") password: String): Map<String, String>
}

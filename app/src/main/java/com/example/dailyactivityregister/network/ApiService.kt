package com.example.dailyactivityregister.network

import com.example.dailyactivityregister.Project
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("projects/upload")
    suspend fun uploadProjectFile(@Part file: MultipartBody.Part): Project

    @GET("projects")
    suspend fun getProjects(): List<Project>
}

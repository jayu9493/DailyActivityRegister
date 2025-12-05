package com.example.dailyactivityregister.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // **LOCAL BACKEND** (backup - for development)
    private const val LOCAL_BASE_URL = "http://10.16.233.245:8000/"
    
    // **CLOUD BACKEND** (Render - PRODUCTION) ☁️
    private const val CLOUD_BASE_URL = "https://dailyactivityregister.onrender.com/"
    
    // **CURRENT MODE**: Using CLOUD! 🚀
    // To switch back to local, change to LOCAL_BASE_URL
    private const val BASE_URL = CLOUD_BASE_URL

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    
    // Helper to check if using cloud
    val isUsingCloud: Boolean get() = BASE_URL == CLOUD_BASE_URL
}
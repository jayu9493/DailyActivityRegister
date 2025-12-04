package com.example.dailyactivityregister.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // **LOCAL BACKEND** (includes Excel parsing)
    private const val LOCAL_BASE_URL = "http://10.16.233.245:8000/"
    
    // **SUPABASE CLOUD** (data only, no parsing yet)
    // TODO: Migrate Excel parsing to cloud function
    private const val SUPABASE_BASE_URL = "https://viwrtolkwuqhjqqfcwah.supabase.co/rest/v1/"
    
    // **CURRENT MODE**: Using local for now (includes all features)
    // Switch to Supabase after migrating Excel parsing logic
    private const val BASE_URL = LOCAL_BASE_URL

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    
    // Helper to check if using cloud
    val isUsingCloud: Boolean get() = BASE_URL == SUPABASE_BASE_URL
}
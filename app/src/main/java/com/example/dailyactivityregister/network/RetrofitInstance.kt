package com.example.dailyactivityregister.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // **IMPORTANT**: Replace this with the IP address of your computer from the 'ipconfig' command
    private const val BASE_URL = "http://10.223.19.245:8000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
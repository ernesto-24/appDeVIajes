package com.example.appdeviajes.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create()

    fun <T> createService(baseUrl: String, serviceClass: Class<T>): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(serviceClass)

    inline fun <reified T> createService(baseUrl: String): T =
        createService(baseUrl, T::class.java)
}

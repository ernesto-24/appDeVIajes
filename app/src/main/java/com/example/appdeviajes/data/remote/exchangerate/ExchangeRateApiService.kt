package com.example.appdeviajes.data.remote.exchangerate

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {

    @GET("latest/{baseCode}")
    suspend fun getTasas(
        @Path("baseCode") baseCode: String
    ): ExchangeRateDto

    companion object {
        const val BASE_URL = "https://open.er-api.com/v6/"
    }
}

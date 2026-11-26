package com.example.appdeviajes.data.remote.ipgeolocation

import retrofit2.http.GET
import retrofit2.http.Query

interface IpGeolocationApiService {

    @GET("./")
    suspend fun getCurrentLocation(
        @Query("key") apiKey: String? = null
    ): IpGeolocationDto

    @GET("./")
    suspend fun getLocationByIp(
        @Query("ip") ip: String,
        @Query("key") apiKey: String? = null
    ): IpGeolocationDto

    companion object {
        const val BASE_URL = "https://api.ip2location.io/"
    }
}

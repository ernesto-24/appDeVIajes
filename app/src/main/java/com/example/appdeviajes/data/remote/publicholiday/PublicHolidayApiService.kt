package com.example.appdeviajes.data.remote.publicholiday

import retrofit2.http.GET
import retrofit2.http.Path

interface PublicHolidayApiService {

    @GET("PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<PublicHolidayDto>

    @GET("LongWeekend/{year}/{countryCode}")
    suspend fun getLongWeekends(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<LongWeekendDto>

    companion object {
        const val BASE_URL = "https://date.nager.at/api/v3/"
    }
}

package com.example.appdeviajes.data.remote.restcountries

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RestCountriesApiService {

    @GET("codes.alpha_2/{code}")
    suspend fun getCountryByCode(
        @Path("code") code: String,
        @Header("Authorization") authorization: String,
        @Query("response_fields") responseFields: String = RESPONSE_FIELDS
    ): RestCountriesResponseDto

    companion object {
        const val BASE_URL = "https://api.restcountries.com/countries/v5/"
        const val RESPONSE_FIELDS = "names.common,codes.alpha_2,codes.alpha_3,capitals,languages,currencies"
    }
}

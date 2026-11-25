package com.example.appdeviajes.data.remote.restcountries

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// REST Countries API v5. Exige API key como header "Authorization: Bearer TU_KEY"
// (registro: https://restcountries.com/sign-up). La key se guarda en local.properties
// y se expone vía BuildConfig.RESTCOUNTRIES_API_KEY; nunca se sube al repositorio.
interface RestCountriesApiService {

    @GET("codes.alpha_2/{codigo}")
    suspend fun getPaisPorCodigo(
        @Path("codigo") codigo: String,
        @Header("Authorization") autorizacion: String,
        @Query("response_fields") campos: String = CAMPOS
    ): RestCountriesResponseDto

    companion object {
        const val BASE_URL = "https://api.restcountries.com/countries/v5/"
        const val CAMPOS = "names.common,codes.alpha_2,codes.alpha_3,capitals,languages,currencies"
    }
}

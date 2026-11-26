package com.example.appdeviajes.data.remote.rickandmorty

import retrofit2.http.GET

interface RickAndMortyApiService {

    @GET("character")
    suspend fun getCharacters(): RickAndMortyCharactersResponseDto

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}

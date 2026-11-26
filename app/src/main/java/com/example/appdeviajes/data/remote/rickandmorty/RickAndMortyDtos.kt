package com.example.appdeviajes.data.remote.rickandmorty

import com.google.gson.annotations.SerializedName

data class RickAndMortyCharactersResponseDto(
    @SerializedName("info") val info: RickAndMortyInfoDto?,
    @SerializedName("results") val results: List<RickAndMortyCharacterDto>?
)

data class RickAndMortyInfoDto(
    @SerializedName("count") val count: Int?,
    @SerializedName("pages") val pages: Int?,
    @SerializedName("next") val next: String?,
    @SerializedName("prev") val prev: String?
)

data class RickAndMortyCharacterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("species") val species: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("image") val image: String?
)

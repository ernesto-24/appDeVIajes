package com.example.appdeviajes.data.model

import com.example.appdeviajes.data.remote.rickandmorty.RickAndMortyCharacterDto

fun RickAndMortyCharacterDto.toDomain(): Personaje = Personaje(
    id = id,
    nombre = name,
    estado = status,
    especie = species,
    genero = gender,
    urlImagen = image
)

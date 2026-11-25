package com.example.appdeviajes.data.model

import com.example.appdeviajes.data.remote.ipgeolocation.IpGeolocationDto

fun IpGeolocationDto.toDomain(): UbicacionUsuario {
    return UbicacionUsuario(
        codigoPais = countryCode,
        nombrePais = countryName,
        ciudad = cityName,
        zonaHoraria = timeZone
    )
}
package com.example.appdeviajes.data.model

import com.example.appdeviajes.data.remote.ipgeolocation.IpGeolocationDto
import java.time.ZoneOffset

fun IpGeolocationDto.toDomain(): UbicacionUsuario = UbicacionUsuario(
    codigoPais = requireNotNull(countryCode) { "No se pudo determinar el país de la IP $ip" },
    nombrePais = countryName,
    ciudad = cityName,
    desfaseHorario = timeZone?.let(ZoneOffset::of)
)

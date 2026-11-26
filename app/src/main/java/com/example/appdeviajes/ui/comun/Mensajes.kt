package com.example.appdeviajes.ui.comun

import retrofit2.HttpException
import java.io.IOException

fun Throwable.mensajeParaUsuario(): String = when (this) {
    is IOException -> "No hay conexión a internet o el servidor no responde."
    is HttpException -> "El servidor respondió con un error (${code()}). Intenta de nuevo más tarde."
    else -> message?.takeIf { it.isNotBlank() } ?: "Ocurrió un error inesperado."
}

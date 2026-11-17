package com.mirenta.data

sealed class ErrorRepositorio(mensaje: String) : Exception(mensaje)

class SesionVencidaException :
    ErrorRepositorio("Tu sesión venció. Cierra sesión y vuelve a ingresar.")

class ConflictoVersionException :
    ErrorRepositorio("La declaración se actualizó desde otro dispositivo.")

class SinConexionException :
    ErrorRepositorio("No se pudo conectar con el servidor. Revisa tu conexión a internet e inténtalo de nuevo.")

class ErrorServidorException(mensaje: String) : ErrorRepositorio(mensaje)

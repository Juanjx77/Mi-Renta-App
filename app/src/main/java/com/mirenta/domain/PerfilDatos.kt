package com.mirenta.domain

data class PerfilDatos(
    val nombre: String = "",
    val tipoDocumento: String = "Cédula de ciudadanía",
    val numeroDocumento: String = "",
    val telefono: String = "",
    val fechaNacimiento: String = "",
    val tieneRut: String = "Sí",
    val responsableIva: String = "No"
)

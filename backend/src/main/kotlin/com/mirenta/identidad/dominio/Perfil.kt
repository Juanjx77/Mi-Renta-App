package com.mirenta.identidad.dominio

/** Datos personales del contribuyente (contexto Identidad y Perfil). */
data class Perfil(
    val usuarioId: String,
    val nombre: String,
    val tipoDocumento: String,
    val numeroDocumento: String,
    val telefono: String,
    val fechaNacimiento: String,
    val tieneRut: String,
    val responsableIva: String,
)

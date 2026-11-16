package com.mirenta.declaracion.aplicacion

import com.mirenta.declaracion.dominio.Declaracion

/** Puerto secundario: el núcleo define qué necesita; la infraestructura decide cómo (SQL, memoria...). */
interface DeclaracionRepository {
    fun buscar(usuarioId: String, anio: Int): Declaracion?

    /** Guarda y devuelve la declaración con su nueva versión. Lanza [ConflictoVersionException] si otro cliente guardó antes. */
    fun guardar(declaracion: Declaracion): Declaracion
}

class ConflictoVersionException(mensaje: String) : RuntimeException(mensaje)

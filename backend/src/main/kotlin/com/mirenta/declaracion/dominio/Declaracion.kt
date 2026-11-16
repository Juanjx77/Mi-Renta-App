package com.mirenta.declaracion.dominio

import java.util.UUID

enum class EstadoDeclaracion { BORRADOR, EN_REVISION, LISTA, EXPORTADA }

/** Agregado raíz del contexto Declaración. */
data class Declaracion(
    val id: UUID,
    val usuarioId: String,
    val anioGravable: Int,
    val estado: EstadoDeclaracion,
    /** Control de concurrencia optimista: la versión que el cliente leyó. Null si aún no existe. */
    val version: Long?,
    val entradas: Entradas,
) {
    fun calcular(): Resultado = CalculadoraRenta(ReglasFactory.para(anioGravable)).calcular(entradas)

    fun conEntradas(nuevas: Entradas): Declaracion = copy(entradas = nuevas)

    /** Prototype: copia para simular escenarios sin tocar la declaración real. */
    fun clonar(): Declaracion = copy(id = UUID.randomUUID(), version = null)

    companion object {
        fun nueva(usuarioId: String, anio: Int): Declaracion {
            ReglasFactory.para(anio) // valida que el año esté soportado
            return Declaracion(UUID.randomUUID(), usuarioId, anio, EstadoDeclaracion.BORRADOR, null, Entradas())
        }
    }
}

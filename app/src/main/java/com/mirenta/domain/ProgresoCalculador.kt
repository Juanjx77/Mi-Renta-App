package com.mirenta.domain

/** Función pura en Kotlin puro para calcular el número de secciones completadas (de 5). */
object ProgresoCalculador {
    fun calcular(seccionesGuardadas: Set<String>, datosPersonalesCompletos: Boolean): Int {
        var count = 0
        if (datosPersonalesCompletos) count++
        if (seccionesGuardadas.contains(SeccionesDeclaracion.Ingresos)) count++
        if (seccionesGuardadas.contains(SeccionesDeclaracion.Patrimonio)) count++
        if (seccionesGuardadas.contains(SeccionesDeclaracion.Deducciones)) count++
        if (seccionesGuardadas.contains(SeccionesDeclaracion.Retenciones)) count++
        return count
    }
}

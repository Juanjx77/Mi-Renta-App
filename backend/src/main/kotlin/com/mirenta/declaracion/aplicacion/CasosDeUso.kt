package com.mirenta.declaracion.aplicacion

import com.mirenta.declaracion.dominio.Declaracion
import com.mirenta.declaracion.dominio.Entradas
import com.mirenta.declaracion.dominio.ReglasFactory
import com.mirenta.declaracion.dominio.ReglasTributarias
import com.mirenta.declaracion.dominio.Resultado

data class DeclaracionCalculada(val declaracion: Declaracion, val resultado: Resultado)

class ConsultarDeclaracion(private val repositorio: DeclaracionRepository) {
    fun ejecutar(usuarioId: String, anio: Int): DeclaracionCalculada? {
        ReglasFactory.para(anio)
        val declaracion = repositorio.buscar(usuarioId, anio) ?: return null
        return DeclaracionCalculada(declaracion, declaracion.calcular())
    }
}

/** Guarda las entradas y recalcula con las reglas del año: el cálculo oficial vive en el servidor (ADR-010). */
class GuardarDeclaracion(private val repositorio: DeclaracionRepository) {
    fun ejecutar(usuarioId: String, anio: Int, versionLeida: Long?, entradas: Entradas): DeclaracionCalculada {
        val actual = repositorio.buscar(usuarioId, anio) ?: Declaracion.nueva(usuarioId, anio)
        if (actual.version != versionLeida) {
            throw ConflictoVersionException(
                "La declaración cambió desde que la leíste (versión actual ${actual.version}, enviada $versionLeida). Recárgala."
            )
        }
        val guardada = repositorio.guardar(actual.conEntradas(entradas))
        return DeclaracionCalculada(guardada, guardada.calcular())
    }
}

/** Calcula un escenario "¿qué pasa si...?" sin persistir nada. */
class SimularDeclaracion {
    fun ejecutar(usuarioId: String, anio: Int, entradas: Entradas): Resultado =
        Declaracion.nueva(usuarioId, anio).conEntradas(entradas).calcular()
}

class ConsultarReglas {
    fun ejecutar(anio: Int): ReglasTributarias = ReglasFactory.para(anio)
}

package com.mirenta.data.api

import com.google.gson.Gson
import com.mirenta.data.DeclaracionRepository
import com.mirenta.domain.Entradas
import com.mirenta.domain.Resultado

class ApiDeclaracionRepository(
    private val api: MiRentaApi,
    private val gson: Gson,
    private val anio: Int = 2025,
) : DeclaracionRepository {
    @Volatile
    private var version: Long? = null

    override suspend fun cargar(uid: String): Entradas? {
        if (uid.isEmpty()) throw IllegalStateException("Sin sesión activa")
        val declaracion = ejecutarLlamada(gson) { api.obtenerDeclaracion(anio) }
        version = declaracion?.version
        return declaracion?.let { it.entradas.aDominio() }
    }

    override suspend fun guardar(uid: String, entradas: Entradas) {
        if (uid.isEmpty()) throw IllegalStateException("Sin sesión activa")
        val cuerpo = GuardarDeclaracionRequest(version = version, entradas = EntradasDto.de(entradas))
        val guardada = ejecutarLlamada(gson) { api.guardarDeclaracion(anio, cuerpo) }
        if (guardada != null) version = guardada.version
    }

    suspend fun simular(entradas: Entradas): Resultado? =
        ejecutarLlamada(gson) { api.simular(anio, EntradasDto.de(entradas)) }?.aDominio()
}

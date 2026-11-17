package com.mirenta.data.api

import com.google.gson.Gson
import com.mirenta.data.PerfilRepository
import com.mirenta.domain.PerfilDatos

class ApiPerfilRepository(
    private val api: MiRentaApi,
    private val gson: Gson,
) : PerfilRepository {
    override suspend fun cargarPerfil(): PerfilDatos? =
        ejecutarLlamada(gson) { api.obtenerPerfil() }?.aDominio()

    override suspend fun guardarPerfil(perfil: PerfilDatos) {
        ejecutarLlamada(gson) { api.guardarPerfil(PerfilDto.de(perfil)) }
    }
}

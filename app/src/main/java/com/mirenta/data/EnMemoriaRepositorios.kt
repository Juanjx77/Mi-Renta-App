package com.mirenta.data

import com.mirenta.domain.Entradas
import com.mirenta.domain.PerfilDatos

class EnMemoriaPerfilRepository(private var perfil: PerfilDatos? = null) : PerfilRepository {
    override suspend fun cargarPerfil(): PerfilDatos? = perfil

    override suspend fun guardarPerfil(perfil: PerfilDatos) {
        this.perfil = perfil
    }
}

class EnMemoriaDeclaracionRepository : DeclaracionRepository {
    private val datos = mutableMapOf<String, Entradas>()

    override suspend fun cargar(uid: String): Entradas? = datos[uid]

    override suspend fun guardar(uid: String, entradas: Entradas) {
        datos[uid] = entradas
    }
}

package com.mirenta.data

import com.mirenta.domain.PerfilDatos

interface PerfilRepository {
    suspend fun cargarPerfil(): PerfilDatos?
    suspend fun guardarPerfil(perfil: PerfilDatos)
}

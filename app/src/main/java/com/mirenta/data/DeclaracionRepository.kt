package com.mirenta.data

import com.mirenta.domain.Entradas

interface DeclaracionRepository {
    suspend fun cargar(uid: String): Entradas?
    suspend fun guardar(uid: String, entradas: Entradas)
}

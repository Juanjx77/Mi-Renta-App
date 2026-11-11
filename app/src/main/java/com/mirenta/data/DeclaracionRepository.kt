package com.mirenta.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mirenta.domain.Entradas
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class DeclaracionRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun cargar(uid: String): Entradas? {
        if (uid.isEmpty()) throw IllegalStateException("Sin sesión activa")
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("declaraciones")
            .document("2025")
            .get()
            .await()
        val map = snapshot.data ?: return null
        val seccionesList = (map["seccionesGuardadas"] as? List<*>)?.mapNotNull { it as? String }?.toSet() ?: emptySet()
        return Entradas(
            salario = (map["salario"] as? Number)?.toLong() ?: 0L,
            honorarios = (map["honorarios"] as? Number)?.toLong() ?: 0L,
            arriendos = (map["arriendos"] as? Number)?.toLong() ?: 0L,
            intereses = (map["intereses"] as? Number)?.toLong() ?: 0L,
            aportes = (map["aportes"] as? Number)?.toLong() ?: 0L,
            vivienda = (map["vivienda"] as? Number)?.toLong() ?: 0L,
            dependientes = (map["dependientes"] as? Number)?.toLong() ?: 0L,
            prepagada = (map["prepagada"] as? Number)?.toLong() ?: 0L,
            afc = (map["afc"] as? Number)?.toLong() ?: 0L,
            retenciones = (map["retenciones"] as? Number)?.toLong() ?: 0L,
            patrimonio = (map["patrimonio"] as? Number)?.toLong() ?: 0L,
            deudas = (map["deudas"] as? Number)?.toLong() ?: 0L,
            consumos = (map["consumos"] as? Number)?.toLong() ?: 0L,
            seccionesGuardadas = seccionesList
        )
    }

    suspend fun guardar(uid: String, entradas: Entradas) {
        if (uid.isEmpty()) throw IllegalStateException("Sin sesión activa")
        val map = mapOf(
            "salario" to entradas.salario,
            "honorarios" to entradas.honorarios,
            "arriendos" to entradas.arriendos,
            "intereses" to entradas.intereses,
            "aportes" to entradas.aportes,
            "vivienda" to entradas.vivienda,
            "dependientes" to entradas.dependientes,
            "prepagada" to entradas.prepagada,
            "afc" to entradas.afc,
            "retenciones" to entradas.retenciones,
            "patrimonio" to entradas.patrimonio,
            "deudas" to entradas.deudas,
            "consumos" to entradas.consumos,
            "seccionesGuardadas" to entradas.seccionesGuardadas.toList()
        )
        withTimeout(10_000) {
            firestore.collection("users")
                .document(uid)
                .collection("declaraciones")
                .document("2025")
                .set(map)
                .await()
        }
    }
}

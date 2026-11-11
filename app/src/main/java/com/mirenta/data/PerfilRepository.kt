package com.mirenta.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mirenta.domain.PerfilDatos
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class PerfilRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val uid: String?
        get() = auth.currentUser?.uid

    suspend fun cargarPerfil(): PerfilDatos? {
        val currentUid = uid ?: throw IllegalStateException("Sin sesión activa")
        val snapshot = firestore.collection("users")
            .document(currentUid)
            .collection("perfil")
            .document("datos")
            .get()
            .await()
        return snapshot.toObject(PerfilDatos::class.java)
    }

    suspend fun guardarPerfil(perfil: PerfilDatos) {
        val currentUid = uid ?: throw IllegalStateException("Sin sesión activa")
        withTimeout(10_000) {
            firestore.collection("users")
                .document(currentUid)
                .collection("perfil")
                .document("datos")
                .set(perfil)
                .await()
        }
    }
}

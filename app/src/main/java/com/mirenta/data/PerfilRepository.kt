package com.mirenta.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mirenta.domain.PerfilDatos
import kotlinx.coroutines.tasks.await

class PerfilRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val uid: String?
        get() = auth.currentUser?.uid

    suspend fun cargarPerfil(): PerfilDatos? {
        val currentUid = uid ?: return null
        val snapshot = firestore.collection("users")
            .document(currentUid)
            .collection("perfil")
            .document("datos")
            .get()
            .await()
        return snapshot.toObject(PerfilDatos::class.java)
    }

    suspend fun guardarPerfil(perfil: PerfilDatos) {
        val currentUid = uid ?: return
        firestore.collection("users")
            .document(currentUid)
            .collection("perfil")
            .document("datos")
            .set(perfil)
            .await()
    }
}

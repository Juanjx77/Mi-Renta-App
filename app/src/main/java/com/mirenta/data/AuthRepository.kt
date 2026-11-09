package com.mirenta.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val usuarioActual: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun iniciarSesion(correo: String, clave: String) {
        firebaseAuth.signInWithEmailAndPassword(correo, clave).await()
    }

    suspend fun registrar(nombre: String, correo: String, clave: String) {
        val resultado = firebaseAuth.createUserWithEmailAndPassword(correo, clave).await()
        val user = resultado.user
        if (user != null) {
            val profile = UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build()
            user.updateProfile(profile).await()
        }
    }

    suspend fun recuperarClave(correo: String) {
        firebaseAuth.sendPasswordResetEmail(correo).await()
    }

    fun cerrarSesion() {
        firebaseAuth.signOut()
    }
}

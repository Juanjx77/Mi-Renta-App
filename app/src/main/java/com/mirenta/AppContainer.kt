package com.mirenta

import com.google.firebase.auth.FirebaseAuth
import com.mirenta.data.AuthRepository
import com.mirenta.data.DeclaracionRepository
import com.mirenta.data.FirestoreDeclaracionRepository
import com.mirenta.data.FirestorePerfilRepository
import com.mirenta.data.PerfilRepository
import com.mirenta.data.api.ApiCliente
import com.mirenta.data.api.ApiDeclaracionRepository
import com.mirenta.data.api.ApiPerfilRepository
import com.mirenta.ui.MiRentaViewModelFactory
import kotlinx.coroutines.tasks.await

class AppContainer(
    val usarApi: Boolean = BuildConfig.USAR_API,
    private val apiUrl: String = BuildConfig.API_URL,
) {
    val authRepository: AuthRepository = AuthRepository()

    private val apiCliente: ApiCliente by lazy {
        ApiCliente(apiUrl) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
        }
    }

    val perfilRepository: PerfilRepository =
        if (usarApi) ApiPerfilRepository(apiCliente.api, apiCliente.gson) else FirestorePerfilRepository()

    val declaracionRepository: DeclaracionRepository =
        if (usarApi) ApiDeclaracionRepository(apiCliente.api, apiCliente.gson) else FirestoreDeclaracionRepository()

    val viewModelFactory: MiRentaViewModelFactory = MiRentaViewModelFactory(this)

    fun uidActual(): String? = authRepository.usuarioActual?.uid
}

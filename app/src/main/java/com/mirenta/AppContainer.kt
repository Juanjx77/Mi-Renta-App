package com.mirenta

import com.mirenta.data.AuthRepository
import com.mirenta.data.DeclaracionRepository
import com.mirenta.data.FirestoreDeclaracionRepository
import com.mirenta.data.FirestorePerfilRepository
import com.mirenta.data.PerfilRepository
import com.mirenta.ui.MiRentaViewModelFactory

class AppContainer {
    val authRepository: AuthRepository = AuthRepository()

    val perfilRepository: PerfilRepository = FirestorePerfilRepository()

    val declaracionRepository: DeclaracionRepository = FirestoreDeclaracionRepository()

    val viewModelFactory: MiRentaViewModelFactory = MiRentaViewModelFactory(this)

    fun uidActual(): String? = authRepository.usuarioActual?.uid
}

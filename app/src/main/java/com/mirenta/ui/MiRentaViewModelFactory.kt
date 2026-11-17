package com.mirenta.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mirenta.AppContainer

class MiRentaViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(RentaViewModel::class.java) ->
            RentaViewModel(container.declaracionRepository) { container.uidActual() }
        modelClass.isAssignableFrom(PerfilViewModel::class.java) ->
            PerfilViewModel(container.perfilRepository)
        modelClass.isAssignableFrom(AuthViewModel::class.java) ->
            AuthViewModel(container.authRepository)
        else -> throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    } as T
}

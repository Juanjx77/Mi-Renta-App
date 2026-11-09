package com.mirenta.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirenta.data.PerfilRepository
import com.mirenta.domain.PerfilDatos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: PerfilRepository = PerfilRepository()
) : ViewModel() {

    var perfil by mutableStateOf(PerfilDatos())
        private set

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _errorRed = MutableStateFlow<String?>(null)
    val errorRed: StateFlow<String?> = _errorRed.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _cargando.value = true
            _errorRed.value = null
            try {
                val resultado = repository.cargarPerfil()
                if (resultado != null) {
                    perfil = resultado
                }
            } catch (e: Exception) {
                _errorRed.value = "Error al cargar los datos. Verifica tu conexión a internet."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun guardar(perfilAGuardar: PerfilDatos, onExito: () -> Unit) {
        viewModelScope.launch {
            _cargando.value = true
            _errorRed.value = null
            try {
                repository.guardarPerfil(perfilAGuardar)
                perfil = perfilAGuardar
                onExito()
            } catch (e: Exception) {
                _errorRed.value = "Error al guardar los datos. Verifica tu conexión a internet."
            } finally {
                _cargando.value = false
            }
        }
    }
}

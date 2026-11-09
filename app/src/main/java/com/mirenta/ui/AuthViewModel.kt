package com.mirenta.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.mirenta.data.AuthRepository
import com.mirenta.domain.Validadores
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _errorGeneral = MutableStateFlow<String?>(null)
    val errorGeneral: StateFlow<String?> = _errorGeneral.asStateFlow()

    private val _mensajeInfo = MutableStateFlow<String?>(null)
    val mensajeInfo: StateFlow<String?> = _mensajeInfo.asStateFlow()

    private val _sesionIniciada = MutableStateFlow(authRepository.usuarioActual != null)
    val sesionIniciada: StateFlow<Boolean> = _sesionIniciada.asStateFlow()

    fun limpiarMensajes() {
        _errorGeneral.value = null
        _mensajeInfo.value = null
    }

    fun iniciarSesion(correo: String, clave: String, onExito: () -> Unit) {
        val errCorreo = Validadores.correo(correo)
        val errClave = Validadores.claveLogin(clave)
        if (errCorreo != null || errClave != null) {
            _errorGeneral.value = errCorreo ?: errClave
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _errorGeneral.value = null
            _mensajeInfo.value = null
            try {
                authRepository.iniciarSesion(correo.trim(), clave)
                _sesionIniciada.value = true
                onExito()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorGeneral.value = traducirError(e)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun registrar(
        nombre: String,
        correo: String,
        clave: String,
        confirmar: String,
        terminosAceptados: Boolean,
        onExito: () -> Unit
    ) {
        val errNombre = Validadores.nombre(nombre)
        val errCorreo = Validadores.correo(correo)
        val errClave = Validadores.claveNueva(clave)
        val errConf = Validadores.confirmacion(clave, confirmar)
        val errTerm = Validadores.terminos(terminosAceptados)

        if (errNombre != null || errCorreo != null || errClave != null || errConf != null || errTerm != null) {
            _errorGeneral.value = errNombre ?: errCorreo ?: errClave ?: errConf ?: errTerm
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _errorGeneral.value = null
            _mensajeInfo.value = null
            try {
                authRepository.registrar(nombre.trim(), correo.trim(), clave)
                _sesionIniciada.value = true
                onExito()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorGeneral.value = traducirError(e)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun recuperarClave(correo: String) {
        val errCorreo = Validadores.correo(correo)
        if (errCorreo != null) {
            _errorGeneral.value = errCorreo
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _errorGeneral.value = null
            _mensajeInfo.value = null
            try {
                authRepository.recuperarClave(correo.trim())
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // No se revela si el correo existe o no por seguridad
            } finally {
                _cargando.value = false
                _mensajeInfo.value = "Si el correo está registrado, recibirás instrucciones para recuperar tu contraseña."
            }
        }
    }

    fun cerrarSesion(onCerrar: () -> Unit) {
        authRepository.cerrarSesion()
        _sesionIniciada.value = false
        onCerrar()
    }

    private fun traducirError(e: Exception): String {
        val errorCode = (e as? FirebaseAuthException)?.errorCode ?: e.message ?: ""
        return when {
            errorCode.contains("email-already-in-use", true) || errorCode.contains("ERROR_EMAIL_ALREADY_IN_USE", true) ->
                "El correo electrónico ya está registrado."
            errorCode.contains("weak-password", true) || errorCode.contains("ERROR_WEAK_PASSWORD", true) ->
                "La contraseña es demasiado débil (mínimo 6 caracteres)."
            errorCode.contains("invalid-credential", true) || errorCode.contains("wrong-password", true) || errorCode.contains("user-not-found", true) || errorCode.contains("ERROR_WRONG_PASSWORD", true) || errorCode.contains("ERROR_USER_NOT_FOUND", true) || errorCode.contains("ERROR_INVALID_CREDENTIAL", true) ->
                "Correo o contraseña incorrectos."
            errorCode.contains("network-request-failed", true) || errorCode.contains("ERROR_NETWORK_REQUEST_FAILED", true) ->
                "Sin conexión a internet. Verifica tu red."
            errorCode.contains("too-many-requests", true) || errorCode.contains("ERROR_TOO_MANY_REQUESTS", true) ->
                "Demasiados intentos fallidos. Inténtalo más tarde."
            errorCode.contains("invalid-email", true) || errorCode.contains("ERROR_INVALID_EMAIL", true) ->
                "El formato del correo electrónico no es válido."
            else -> e.localizedMessage ?: "Ocurrió un error inesperado. Inténtalo de nuevo."
        }
    }
}

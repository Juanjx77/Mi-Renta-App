package com.mirenta.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mirenta.data.DeclaracionRepository
import com.mirenta.domain.Campo
import com.mirenta.domain.Entradas
import com.mirenta.domain.Resultado
import com.mirenta.domain.ResultadoImportacion
import com.mirenta.domain.TaxCalculator
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado compartido entre pantallas (exógena + simulador). */
class RentaViewModel(
    private val declaracionRepository: DeclaracionRepository = DeclaracionRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    var exogena by mutableStateOf<String?>(null)
        private set
    var declaracion by mutableStateOf(Entradas())
        private set
    var simulacion by mutableStateOf(Entradas())
        private set
    var calculado by mutableStateOf(false)
        private set

    private val _errorRed = MutableStateFlow<String?>(null)
    val errorRed: StateFlow<String?> = _errorRed.asStateFlow()

    val resultadoDeclaracion: Resultado get() = TaxCalculator.calcular(declaracion)
    val resultadoSimulacion: Resultado get() = TaxCalculator.calcular(simulacion)

    init {
        cargar()
    }

    fun cargar() {
        val currentUid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _errorRed.value = null
            try {
                val guardadas = declaracionRepository.cargar(currentUid)
                if (guardadas != null) {
                    declaracion = guardadas
                }
            } catch (e: Exception) {
                _errorRed.value = "Error al cargar los datos. Verifica tu conexión a internet."
            }
        }
    }

    fun guardar(onExito: () -> Unit = {}) {
        val currentUid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _errorRed.value = null
            try {
                if (auth.currentUser?.uid != currentUid) return@launch
                declaracionRepository.guardar(currentUid, declaracion)
                onExito()
            } catch (e: TimeoutCancellationException) {
                _errorRed.value = "Sin conexión estable: tus cambios se enviarán cuando vuelva internet."
            } catch (e: Exception) {
                _errorRed.value = "Error al guardar los datos. Verifica tu conexión a internet."
            }
        }
    }

    fun guardarSeccion(seccion: String, onExito: () -> Unit) {
        declaracion = declaracion.copy(seccionesGuardadas = declaracion.seccionesGuardadas + seccion)
        guardar(onExito)
    }

    /** Lectura simulada: precarga las cifras de ejemplo, como en el prototipo. */
    fun cargarExogena(nombre: String) {
        exogena = nombre
        declaracion = Entradas.Ejemplo
    }

    fun aplicarExogena(nombreArchivo: String, r: ResultadoImportacion) {
        if (r.errores.isNotEmpty()) return
        exogena = nombreArchivo
        declaracion = declaracion.copy(
            salario = r.salario,
            honorarios = r.honorarios,
            arriendos = r.arriendos,
            intereses = r.intereses,
            aportes = r.aportes,
            retenciones = r.retenciones
        )
        guardar()
    }

    fun quitarExogena() {
        exogena = null
    }

    fun cambiar(campo: Campo, valor: Long) {
        simulacion = simulacion.con(campo, valor)
        calculado = false
    }

    fun cambiarDeclaracion(campo: Campo, valor: Long) {
        declaracion = declaracion.con(campo, valor)
    }

    fun calcular() {
        calculado = true
    }

    fun cargarEjemplo() {
        simulacion = Entradas.Ejemplo
        calculado = false
    }

    fun limpiar() {
        simulacion = Entradas()
        calculado = false
    }

    fun partirDeDeclaracion() {
        simulacion = declaracion
        calculado = false
    }

    fun reiniciar() {
        exogena = null
        declaracion = Entradas()
        simulacion = Entradas()
        calculado = false
        _errorRed.value = null
    }
}

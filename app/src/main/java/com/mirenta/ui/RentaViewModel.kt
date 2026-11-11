package com.mirenta.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mirenta.domain.Campo
import com.mirenta.domain.Entradas
import com.mirenta.domain.Resultado
import com.mirenta.domain.ResultadoImportacion
import com.mirenta.domain.TaxCalculator

/** Estado compartido entre pantallas (exógena + simulador). */
class RentaViewModel : ViewModel() {
    var exogena by mutableStateOf<String?>(null)
        private set
    var declaracion by mutableStateOf(Entradas())
        private set
    var simulacion by mutableStateOf(Entradas())
        private set
    var calculado by mutableStateOf(false)
        private set

    val resultadoDeclaracion: Resultado get() = TaxCalculator.calcular(declaracion)
    val resultadoSimulacion: Resultado get() = TaxCalculator.calcular(simulacion)

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
    }
}

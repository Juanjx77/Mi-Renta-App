package com.mirenta.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mirenta.domain.Campo
import com.mirenta.domain.Entradas
import com.mirenta.domain.Resultado
import com.mirenta.domain.TaxCalculator

/** Estado compartido entre pantallas (exógena + simulador). */
class RentaViewModel : ViewModel() {
    var exogena by mutableStateOf<String?>(null)
        private set
    var entradas by mutableStateOf(Entradas())
        private set
    var calculado by mutableStateOf(false)
        private set

    val resultado: Resultado get() = TaxCalculator.calcular(entradas)

    /** Lectura simulada: precarga las cifras de ejemplo, como en el prototipo. */
    fun cargarExogena(nombre: String) {
        exogena = nombre
        entradas = Entradas.Ejemplo
    }

    fun quitarExogena() {
        exogena = null
    }

    fun cambiar(campo: Campo, valor: Long) {
        entradas = entradas.con(campo, valor)
        calculado = false
    }

    fun calcular() {
        calculado = true
    }

    fun cargarEjemplo() {
        entradas = Entradas.Ejemplo
        calculado = false
    }

    fun limpiar() {
        entradas = Entradas()
        calculado = false
    }
}

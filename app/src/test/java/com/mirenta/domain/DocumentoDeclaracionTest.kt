package com.mirenta.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentoDeclaracionTest {

    @Test
    fun testEnmascararDocumento() {
        assertEquals("******1234", DocumentoDeclaracion.enmascararDocumento("1234561234"))
        assertEquals("1234", DocumentoDeclaracion.enmascararDocumento("1234"))
        assertEquals("******1234", DocumentoDeclaracion.enmascararDocumento("  1234561234  "))
    }

    @Test
    fun testDeclaracionVacia() {
        val entradas = Entradas()
        val resultado = TaxCalculator.calcular(entradas)
        val perfil = PerfilDatos(nombre = "Juan Pérez", numeroDocumento = "987654321")
        val hallazgos = emptyList<Hallazgo>()

        val secciones = DocumentoDeclaracion.generar(entradas, resultado, hallazgos, perfil, "01/01/2025")

        // Verificar que secciones no guardadas muestran "Sin datos"
        val ingresosSeccion = secciones.find { it.titulo.contains("2. Ingresos") }
        assertTrue(ingresosSeccion?.lineas?.all { it.valor == "Sin datos" } == true)
    }

    @Test
    fun testDeclaracionConDatos() {
        val entradas = Entradas(
            salario = 96_000_000L,
            seccionesGuardadas = setOf(SeccionesDeclaracion.Ingresos)
        )
        val resultado = TaxCalculator.calcular(entradas)
        val perfil = PerfilDatos(nombre = "María Gómez", numeroDocumento = "1234567890")
        val hallazgos = emptyList<Hallazgo>()

        val secciones = DocumentoDeclaracion.generar(entradas, resultado, hallazgos, perfil, "01/01/2025")

        val ingresosSeccion = secciones.find { it.titulo.contains("2. Ingresos") }
        val salarioLinea = ingresosSeccion?.lineas?.find { it.etiqueta.contains("Salario") }
        assertEquals("$ 96.000.000", salarioLinea?.valor)
    }
}

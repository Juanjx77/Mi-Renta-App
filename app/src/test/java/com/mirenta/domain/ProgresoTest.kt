package com.mirenta.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgresoTest {

    @Test
    fun testProgresoCeroDeCinco() {
        val count = ProgresoCalculador.calcular(emptySet(), false)
        assertEquals(0, count)
    }

    @Test
    fun testProgresoTresDeCinco() {
        val secciones = setOf(SeccionesDeclaracion.Ingresos, SeccionesDeclaracion.Patrimonio, SeccionesDeclaracion.Deducciones)
        val count = ProgresoCalculador.calcular(secciones, false)
        assertEquals(3, count)
    }

    @Test
    fun testProgresoCincoDeCinco() {
        val secciones = setOf(
            SeccionesDeclaracion.Ingresos,
            SeccionesDeclaracion.Patrimonio,
            SeccionesDeclaracion.Deducciones,
            SeccionesDeclaracion.Retenciones
        )
        val count = ProgresoCalculador.calcular(secciones, true)
        assertEquals(5, count)
    }
}

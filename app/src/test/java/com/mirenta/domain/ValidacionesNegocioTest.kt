package com.mirenta.domain

import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesNegocioTest {

    @Test
    fun testEntradasValidas() {
        val entradas = Entradas.Ejemplo
        val hallazgos = ValidacionesNegocio.validar(entradas)
        val errores = hallazgos.filter { it.severidad == Severidad.ERROR }
        assertTrue(errores.isEmpty())
    }

    @Test
    fun testMontosNegativos() {
        val entradas = Entradas(salario = -1000000)
        val hallazgos = ValidacionesNegocio.validar(entradas)
        assertTrue(hallazgos.any { it.severidad == Severidad.ERROR && it.codigo == "MONTO_NEGATIVO" })
    }

    @Test
    fun testRetencionesMayoresAIngresos() {
        val entradas = Entradas(salario = 10_000_000, retenciones = 15_000_000)
        val hallazgos = ValidacionesNegocio.validar(entradas)
        assertTrue(hallazgos.any { it.severidad == Severidad.ERROR && it.codigo == "RETENCIONES_MAYORES_INGRESOS" })
    }

    @Test
    fun testExcesoDependientes() {
        val entradas = Entradas(dependientes = 5)
        val hallazgos = ValidacionesNegocio.validar(entradas)
        assertTrue(hallazgos.any { it.severidad == Severidad.ADVERTENCIA && it.codigo == "EXCESO_DEPENDIENTES" })
    }

    @Test
    fun testPatrimonioLiquidoNegativo() {
        val entradas = Entradas(patrimonio = 50_000_000, deudas = 80_000_000)
        val hallazgos = ValidacionesNegocio.validar(entradas)
        assertTrue(hallazgos.any { it.severidad == Severidad.ADVERTENCIA && it.codigo == "PATRIMONIO_LIQUIDO_NEGATIVO" })
    }
}

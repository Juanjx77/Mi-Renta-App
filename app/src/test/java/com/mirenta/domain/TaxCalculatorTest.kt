package com.mirenta.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaxCalculatorTest {

    @Test
    fun testCaso1() {
        val entradas = Entradas(salario = 30_000_000L)
        val r = TaxCalculator.calcular(entradas)

        assertEquals(30_000_000.0, r.laborales, 1.0)
        assertEquals(0.0, r.noConstitutivos, 1.0)
        assertEquals(0.0, r.deducciones, 1.0)
        assertEquals(7_500_000.0, r.exenta, 1.0)
        assertEquals(12_000_000.0, r.limite, 1.0)
        assertEquals(7_500_000.0, r.beneficios, 1.0)
        assertEquals(22_500_000.0, r.rentaLiquidaTrabajo, 1.0)
        assertEquals(22_500_000.0, r.rentaGravable, 1.0)
        assertEquals(451.82, r.uvt, 0.01)
        assertEquals(0.0, r.impuesto, 1.0)
        assertEquals(0.0, r.saldo, 1.0)
        assertTrue(r.motivos.isEmpty())
    }

    @Test
    fun testCaso2() {
        val entradas = Entradas(
            salario = 120_000_000L,
            aportes = 10_000_000L,
            retenciones = 5_000_000L
        )
        val r = TaxCalculator.calcular(entradas)

        assertEquals(10_000_000.0, r.noConstitutivos, 1.0)
        assertEquals(0.0, r.deducciones, 1.0)
        assertEquals(27_500_000.0, r.exenta, 1.0)
        assertEquals(44_000_000.0, r.limite, 1.0)
        assertEquals(27_500_000.0, r.beneficios, 1.0)
        assertEquals(82_500_000.0, r.rentaLiquidaTrabajo, 1.0)
        assertEquals(82_500_000.0, r.rentaGravable, 1.0)
        assertEquals(1656.66, r.uvt, 0.01)
        assertEquals(5_362_000.0, r.impuesto, 1.0)
        assertEquals(362_000.0, r.saldo, 1.0)
    }

    @Test
    fun testCaso3() {
        val entradas = Entradas(
            salario = 120_000_000L,
            aportes = 10_000_000L,
            retenciones = 5_000_000L,
            vivienda = 4_000_000L,
            dependientes = 7L,
            prepagada = 120_000L,
            afc = 0L,
            patrimonio = 223_000_000L
        )
        val r = TaxCalculator.calcular(entradas)
        assertEquals(68_653_416.0, r.rentaGravable, 1.0)
        assertEquals(2_731_000.0, r.impuesto, 1.0)
        assertEquals(-2_269_000.0, r.saldo, 1.0)
        assertEquals(18_462_112.0, r.deducciones, 1.0)
        assertEquals(22_884_472.0, r.exenta, 1.0)
        assertEquals(44_000_000.0, r.limite, 1.0)
        assertEquals(41_346_584.0, r.beneficios, 1.0)
        assertEquals(68_653_416.0, r.rentaLiquidaTrabajo, 1.0)
        assertEquals(1378.61, r.uvt, 0.01)
    }

    @Test
    fun testCaso4() {
        val entradas = Entradas(
            salario = 500_000_000L,
            aportes = 10_000_000L,
            retenciones = 5_000_000L,
            vivienda = 4_000_000L,
            dependientes = 7L,
            prepagada = 120_000L,
            afc = 0L,
            patrimonio = 223_000_000L
        )
        val r = TaxCalculator.calcular(entradas)
        assertEquals(432_196_678.0, r.rentaGravable, 1.0)
        assertEquals(114_492_000.0, r.impuesto, 1.0)
        assertEquals(109_492_000.0, r.saldo, 1.0)
        assertEquals(18_462_112.0, r.deducciones, 1.0)
        assertEquals(39_341_210.0, r.exenta, 1.0)
        assertEquals(66_730_660.0, r.limite, 1.0)
        assertEquals(57_803_322.0, r.beneficios, 1.0)
        assertEquals(432_196_678.0, r.rentaLiquidaTrabajo, 1.0)
        assertEquals(8678.82, r.uvt, 0.01)
        assertEquals(1, r.motivos.size)
    }

    @Test
    fun testCaso5() {
        // cálculo manual, pendiente de contrastar con simulador DIAN
        val entradas = Entradas(
            salario = 120_000_000L,
            aportes = 10_000_000L,
            vivienda = 35_000_000L,
            dependientes = 4L
        )
        val r = TaxCalculator.calcular(entradas)

        assertEquals(49_342_112.0, r.deducciones, 1.0)
        assertEquals(15_164_472.0, r.exenta, 1.0)
        assertEquals(44_000_000.0, r.limite, 1.0)
        assertEquals(58_342_112.0, r.beneficios, 1.0)
        assertEquals(51_657_888.0, r.rentaLiquidaTrabajo, 1.0)
        assertEquals(1037.32, r.uvt, 0.01)
        assertEquals(0.0, r.impuesto, 1.0)
        assertEquals(0.0, r.saldo, 1.0)
        assertEquals(true, r.topeAlcanzado)
    }

    @Test
    fun testCasoDeduccionesTope() {
        val entradas = Entradas(
            salario = 120_000_000L,
            aportes = 10_000_000L,
            vivienda = 10_000_000L,
            dependientes = 4L
        )
        val r = TaxCalculator.calcular(entradas)

        assertEquals(24_342_112.0, r.deducciones, 1.0)
        assertEquals(21_414_472.0, r.exenta, 1.0)
        assertEquals(44_000_000.0, r.limite, 1.0)
        assertEquals(45_756_584.0, r.beneficios, 1.0)
        assertEquals(64_243_416.0, r.rentaLiquidaTrabajo, 1.0)
        assertEquals(false, r.topeAlcanzado)
    }
}

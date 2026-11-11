package com.mirenta.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExogenaImportadorTest {

    @Test
    fun testCaso1ArchivoValidoComas() {
        val csv = """
            pagador,tipo_renta,valor_bruto,aporte_salud,aporte_pension,retencion
            Empresa Ejemplo S.A.S.,salario,96000000,3840000,3840000,4200000
            Cliente Ficticio Ltda.,honorarios,24000000,0,0,2400000
            Arrendatario Ficticio,arriendos,12000000,,,
            Banco Ficticio,intereses,600000,,,
        """.trimIndent()

        val r = ExogenaImportador.leer(csv)
        assertEquals(4, r.filas)
        assertEquals(96_000_000L, r.salario)
        assertEquals(24_000_000L, r.honorarios)
        assertEquals(12_000_000L, r.arriendos)
        assertEquals(600_000L, r.intereses)
        assertEquals(7_680_000L, r.aportes)
        assertEquals(6_600_000L, r.retenciones)
        assertTrue(r.errores.isEmpty())
    }

    @Test
    fun testCaso2ArchivoValidoPuntoYComaYMilesConPunto() {
        val csv = """
            pagador;tipo_renta;valor_bruto;aporte_salud;aporte_pension;retencion
            Empresa Ejemplo S.A.S.;salario;96.000.000;3.840.000;3.840.000;4.200.000
            Cliente Ficticio Ltda.;honorarios;24.000.000;0;0;2.400.000
            Arrendatario Ficticio;arriendos;12.000.000;;;
            Banco Ficticio;intereses;600.000;;;
        """.trimIndent()

        val r = ExogenaImportador.leer(csv)
        assertEquals(4, r.filas)
        assertEquals(96_000_000L, r.salario)
        assertEquals(24_000_000L, r.honorarios)
        assertEquals(12_000_000L, r.arriendos)
        assertEquals(600_000L, r.intereses)
        assertEquals(7_680_000L, r.aportes)
        assertEquals(6_600_000L, r.retenciones)
        assertTrue(r.errores.isEmpty())
    }

    @Test
    fun testCaso3TipoRentaInvalido() {
        val csv = """
            pagador,tipo_renta,valor_bruto,aporte_salud,aporte_pension,retencion
            Empresa,dividendos,1000000,,,
        """.trimIndent()

        val r = ExogenaImportador.leer(csv)
        assertEquals(0, r.filas)
        assertEquals(1, r.errores.size)
        assertEquals(2, r.errores[0].linea)
    }

    @Test
    fun testCaso4ValorNegativoYDecimal() {
        val csv1 = """
            pagador,tipo_renta,valor_bruto,aporte_salud,aporte_pension,retencion
            Empresa,salario,-1000000,,,
        """.trimIndent()
        val r1 = ExogenaImportador.leer(csv1)
        assertEquals(1, r1.errores.size)

        val csv2 = """
            pagador;tipo_renta;valor_bruto;aporte_salud;aporte_pension;retencion
            Empresa;salario;1500000,50;;;
        """.trimIndent()
        val r2 = ExogenaImportador.leer(csv2)
        assertEquals(1, r2.errores.size)
    }

    @Test
    fun testCaso5ColumnaMenos() {
        val csv = """
            pagador,tipo_renta,valor_bruto,aporte_salud,aporte_pension,retencion
            Empresa,salario,1000000
        """.trimIndent()

        val r = ExogenaImportador.leer(csv)
        assertEquals(0, r.filas)
        assertEquals(1, r.errores.size)
        assertEquals(2, r.errores[0].linea)
    }

    @Test
    fun testCaso6EncabezadoIncorrectoYVacio() {
        val csvMal = """
            mal_encabezado,tipo_renta,valor_bruto,aporte_salud,aporte_pension,retencion
            Empresa,salario,1000000,,,
        """.trimIndent()
        val r1 = ExogenaImportador.leer(csvMal)
        assertEquals(1, r1.errores.size)
        assertEquals(1, r1.errores[0].linea)

        val r2 = ExogenaImportador.leer("   \n  ")
        assertEquals(1, r2.errores.size)
        assertEquals(1, r2.errores[0].linea)
    }

    @Test
    fun testCaso7MasDe500Filas() {
        val sb = StringBuilder("pagador,tipo_renta,valor_bruto,aporte_salud,aporte_pension,retencion\n")
        for (i in 1..501) {
            sb.append("Empresa $i,salario,1000000,,,\n")
        }
        val r = ExogenaImportador.leer(sb.toString())
        assertEquals(1, r.errores.size)
        assertEquals(1, r.errores[0].linea)
    }
}

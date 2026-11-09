package com.mirenta.domain

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    @Test
    fun testNombre() {
        // Caso válido: nombre y apellido
        assertNull(Validadores.nombre("Juan Pérez"))
        // Caso inválido: una sola palabra
        assertNotNull(Validadores.nombre("Juan"))
        // Caso inválido: vacío
        assertNotNull(Validadores.nombre(""))
    }

    @Test
    fun testTextoNoVacio() {
        assertNull(Validadores.textoNoVacio("Texto válido"))
        assertNotNull(Validadores.textoNoVacio(""))
        assertNotNull(Validadores.textoNoVacio("   "))
    }

    @Test
    fun testDocumento() {
        assertNull(Validadores.documento("12345678"))
        assertNotNull(Validadores.documento("12345")) // corto (< 6)
        assertNotNull(Validadores.documento("1234567890123")) // largo (> 12)
        assertNotNull(Validadores.documento("12345abc")) // con letras
    }

    @Test
    fun testTelefono() {
        assertNull(Validadores.telefono("3001234567"))
        assertNull(Validadores.telefono("")) // opcional / vacío
        assertNotNull(Validadores.telefono("2001234567")) // no empieza por 3
        assertNotNull(Validadores.telefono("300123456")) // < 10 dígitos
        assertNotNull(Validadores.telefono("30012345678")) // > 10 dígitos
    }

    @Test
    fun testFechaNacimiento() {
        val cal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.YEAR, -25) }
        val anio = cal.get(java.util.Calendar.YEAR)
        val mes = String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1)
        val dia = String.format("%02d", cal.get(java.util.Calendar.DAY_OF_MONTH))
        assertNull(Validadores.fechaNacimiento("$anio-$mes-$dia"))

        // Futura
        val calFutura = java.util.Calendar.getInstance().apply { add(java.util.Calendar.YEAR, 1) }
        val anioF = calFutura.get(java.util.Calendar.YEAR)
        assertNotNull(Validadores.fechaNacimiento("$anioF-01-01"))

        // Menor de 18
        val calJoven = java.util.Calendar.getInstance().apply { add(java.util.Calendar.YEAR, -10) }
        val anioJ = calJoven.get(java.util.Calendar.YEAR)
        val mesJ = String.format("%02d", calJoven.get(java.util.Calendar.MONTH) + 1)
        val diaJ = String.format("%02d", calJoven.get(java.util.Calendar.DAY_OF_MONTH))
        assertNotNull(Validadores.fechaNacimiento("$anioJ-$mesJ-$diaJ"))

        // Formato inválido
        assertNotNull(Validadores.fechaNacimiento("15-05-1990"))
        assertNotNull(Validadores.fechaNacimiento(""))
    }

    @Test
    fun testCorreo() {
        // Caso válido
        assertNull(Validadores.correo("usuario@correo.com"))
        // Caso inválido: correo sin arroba
        assertNotNull(Validadores.correo("usuariocorreo.com"))
        // Caso inválido: correo sin dominio
        assertNotNull(Validadores.correo("usuario@"))
        // Caso inválido: vacío
        assertNotNull(Validadores.correo(""))
    }

    @Test
    fun testClaveNueva() {
        // Caso válido: mínimo 8 caracteres, al menos una letra y un número
        assertNull(Validadores.claveNueva("Clave1234"))
        // Caso inválido: clave corta (< 8)
        assertNotNull(Validadores.claveNueva("C123"))
        // Caso inválido: clave sin número
        assertNotNull(Validadores.claveNueva("ClaveSinNumero"))
        // Caso inválido: clave sin letra
        assertNotNull(Validadores.claveNueva("12345678"))
    }

    @Test
    fun testClaveLogin() {
        // Caso válido: no vacía
        assertNull(Validadores.claveLogin("cualquierClave"))
        // Caso inválido: vacía
        assertNotNull(Validadores.claveLogin(""))
    }

    @Test
    fun testConfirmacion() {
        // Caso válido: igual a la clave
        assertNull(Validadores.confirmacion("Clave1234", "Clave1234"))
        // Caso inválido: confirmación distinta
        assertNotNull(Validadores.confirmacion("Clave1234", "OtraClave"))
    }

    @Test
    fun testTerminos() {
        // Caso válido: aceptado
        assertNull(Validadores.terminos(true))
        // Caso inválido: sin aceptar
        assertNotNull(Validadores.terminos(false))
    }
}

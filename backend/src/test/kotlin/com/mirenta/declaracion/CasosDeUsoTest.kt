package com.mirenta.declaracion

import com.mirenta.declaracion.aplicacion.ConflictoVersionException
import com.mirenta.declaracion.aplicacion.ConsultarDeclaracion
import com.mirenta.declaracion.aplicacion.DeclaracionRepository
import com.mirenta.declaracion.aplicacion.GuardarDeclaracion
import com.mirenta.declaracion.aplicacion.SimularDeclaracion
import com.mirenta.declaracion.dominio.AnioNoSoportadoException
import com.mirenta.declaracion.dominio.Declaracion
import com.mirenta.declaracion.dominio.Entradas
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Adaptador en memoria: prueba el núcleo sin base de datos (la ventaja de tener puertos). */
class DeclaracionRepositoryEnMemoria : DeclaracionRepository {
    private val datos = mutableMapOf<Pair<String, Int>, Declaracion>()

    override fun buscar(usuarioId: String, anio: Int) = datos[usuarioId to anio]

    override fun guardar(declaracion: Declaracion): Declaracion {
        val actual = datos[declaracion.usuarioId to declaracion.anioGravable]
        if (actual?.version != declaracion.version) throw ConflictoVersionException("conflicto")
        return declaracion.copy(version = (declaracion.version ?: -1) + 1).also { datos[it.usuarioId to it.anioGravable] = it }
    }
}

class CasosDeUsoTest {
    private val repo = DeclaracionRepositoryEnMemoria()
    private val guardar = GuardarDeclaracion(repo)
    private val consultar = ConsultarDeclaracion(repo)

    @Test
    fun `guardar por primera vez crea la declaracion y la calcula`() {
        val r = guardar.ejecutar("u1", 2025, null, Entradas(salario = 120_000_000, aportes = 10_000_000, retenciones = 5_000_000))
        assertEquals(0L, r.declaracion.version)
        assertEquals(r.declaracion.calcular(), r.resultado)
        assertEquals(r.resultado, consultar.ejecutar("u1", 2025)!!.resultado)
    }

    @Test
    fun `guardar con una version vieja es un conflicto`() {
        guardar.ejecutar("u1", 2025, null, Entradas(salario = 1))
        guardar.ejecutar("u1", 2025, 0, Entradas(salario = 2))
        assertThrows<ConflictoVersionException> { guardar.ejecutar("u1", 2025, 0, Entradas(salario = 3)) }
    }

    @Test
    fun `consultar sin datos devuelve null y un anio sin reglas falla`() {
        assertNull(consultar.ejecutar("nadie", 2025))
        assertThrows<AnioNoSoportadoException> { consultar.ejecutar("u1", 1999) }
    }

    @Test
    fun `simular no guarda nada`() {
        SimularDeclaracion().ejecutar("u1", 2025, Entradas(salario = 200_000_000))
        assertNull(repo.buscar("u1", 2025))
    }
}

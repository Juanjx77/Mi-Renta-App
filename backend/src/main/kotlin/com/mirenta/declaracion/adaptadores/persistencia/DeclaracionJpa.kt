package com.mirenta.declaracion.adaptadores.persistencia

import com.mirenta.declaracion.aplicacion.ConflictoVersionException
import com.mirenta.declaracion.aplicacion.DeclaracionRepository
import com.mirenta.declaracion.dominio.Declaracion
import com.mirenta.declaracion.dominio.Entradas
import com.mirenta.declaracion.dominio.EstadoDeclaracion
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

/** Modelo de persistencia: las anotaciones JPA viven aquí, nunca en el dominio. */
@Entity
@Table(name = "declaracion")
class DeclaracionEntity(
    @Id var id: UUID = UUID.randomUUID(),
    var usuarioId: String = "",
    var anioGravable: Short = 0,
    @Enumerated(EnumType.STRING) var estado: EstadoDeclaracion = EstadoDeclaracion.BORRADOR,
    @Version var version: Long? = null,
    var salario: Long = 0,
    var honorarios: Long = 0,
    var arriendos: Long = 0,
    var intereses: Long = 0,
    var aportes: Long = 0,
    var vivienda: Long = 0,
    var dependientes: Long = 0,
    var prepagada: Long = 0,
    var afc: Long = 0,
    var retenciones: Long = 0,
    var patrimonio: Long = 0,
    var deudas: Long = 0,
    var consumos: Long = 0,
    @Column(name = "secciones_guardadas") var seccionesGuardadas: String = "",
    var impuestoCalculado: Long = 0,
    var actualizadoEn: OffsetDateTime = OffsetDateTime.now(),
)

interface DeclaracionSpringData : JpaRepository<DeclaracionEntity, UUID> {
    fun findByUsuarioIdAndAnioGravable(usuarioId: String, anioGravable: Short): DeclaracionEntity?
}

/** Adaptador secundario: implementa el puerto del núcleo sobre PostgreSQL. */
@Component
class PostgresDeclaracionRepository(private val jpa: DeclaracionSpringData) : DeclaracionRepository {

    @Transactional(readOnly = true)
    override fun buscar(usuarioId: String, anio: Int): Declaracion? =
        jpa.findByUsuarioIdAndAnioGravable(usuarioId, anio.toShort())?.aDominio()

    @Transactional
    override fun guardar(declaracion: Declaracion): Declaracion {
        val entidad = jpa.findByUsuarioIdAndAnioGravable(declaracion.usuarioId, declaracion.anioGravable.toShort())
            ?: DeclaracionEntity(id = declaracion.id, usuarioId = declaracion.usuarioId, anioGravable = declaracion.anioGravable.toShort())
        if (entidad.version != declaracion.version) throw ConflictoVersionException("Otro dispositivo guardó la declaración primero. Recárgala.")
        entidad.copiarDe(declaracion)
        return try {
            jpa.saveAndFlush(entidad).aDominio()
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw ConflictoVersionException("Otro dispositivo guardó la declaración primero. Recárgala.")
        } catch (e: DataIntegrityViolationException) {
            throw ConflictoVersionException("La declaración se creó desde otro dispositivo. Recárgala.")
        }
    }

    private fun DeclaracionEntity.copiarDe(d: Declaracion) {
        val e = d.entradas
        estado = d.estado
        salario = e.salario; honorarios = e.honorarios; arriendos = e.arriendos; intereses = e.intereses
        aportes = e.aportes; vivienda = e.vivienda; dependientes = e.dependientes; prepagada = e.prepagada
        afc = e.afc; retenciones = e.retenciones; patrimonio = e.patrimonio; deudas = e.deudas; consumos = e.consumos
        seccionesGuardadas = e.seccionesGuardadas.sorted().joinToString(",")
        impuestoCalculado = d.calcular().impuesto.toLong()
        actualizadoEn = OffsetDateTime.now()
    }

    private fun DeclaracionEntity.aDominio() = Declaracion(
        id = id,
        usuarioId = usuarioId,
        anioGravable = anioGravable.toInt(),
        estado = estado,
        version = version,
        entradas = Entradas(
            salario, honorarios, arriendos, intereses, aportes, vivienda, dependientes,
            prepagada, afc, retenciones, patrimonio, deudas, consumos,
            seccionesGuardadas.split(",").filter { it.isNotBlank() }.toSet(),
        ),
    )
}

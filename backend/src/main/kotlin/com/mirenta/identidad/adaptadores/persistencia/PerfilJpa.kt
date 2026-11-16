package com.mirenta.identidad.adaptadores.persistencia

import com.mirenta.identidad.aplicacion.PerfilRepository
import com.mirenta.identidad.dominio.Perfil
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Entity
@Table(name = "perfil")
class PerfilEntity(
    @Id var usuarioId: String = "",
    var nombre: String = "",
    var tipoDocumento: String = "",
    var numeroDocumento: String = "",
    var telefono: String = "",
    var fechaNacimiento: String = "",
    var tieneRut: String = "",
    var responsableIva: String = "",
    var actualizadoEn: OffsetDateTime = OffsetDateTime.now(),
)

interface PerfilSpringData : JpaRepository<PerfilEntity, String>

@Component
class PostgresPerfilRepository(private val jpa: PerfilSpringData) : PerfilRepository {

    @Transactional(readOnly = true)
    override fun buscar(usuarioId: String): Perfil? = jpa.findById(usuarioId).orElse(null)?.aDominio()

    @Transactional
    override fun guardar(perfil: Perfil): Perfil = jpa.save(
        PerfilEntity(
            perfil.usuarioId, perfil.nombre, perfil.tipoDocumento, perfil.numeroDocumento,
            perfil.telefono, perfil.fechaNacimiento, perfil.tieneRut, perfil.responsableIva, OffsetDateTime.now(),
        )
    ).aDominio()

    private fun PerfilEntity.aDominio() =
        Perfil(usuarioId, nombre, tipoDocumento, numeroDocumento, telefono, fechaNacimiento, tieneRut, responsableIva)
}

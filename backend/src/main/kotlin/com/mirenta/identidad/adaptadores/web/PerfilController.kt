package com.mirenta.identidad.adaptadores.web

import com.mirenta.compartido.seguridad.UsuarioActual
import com.mirenta.identidad.aplicacion.ConsultarPerfil
import com.mirenta.identidad.aplicacion.GuardarPerfil
import com.mirenta.identidad.dominio.Perfil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class PerfilDto(
    @field:NotBlank @field:Size(max = 200) val nombre: String,
    @field:NotBlank @field:Size(max = 60) val tipoDocumento: String = "Cédula de ciudadanía",
    @field:NotBlank @field:Size(max = 30) val numeroDocumento: String,
    @field:Size(max = 30) val telefono: String = "",
    @field:Size(max = 20) val fechaNacimiento: String = "",
    @field:Size(max = 5) val tieneRut: String = "Sí",
    @field:Size(max = 5) val responsableIva: String = "No",
) {
    fun aDominio(usuarioId: String) =
        Perfil(usuarioId, nombre, tipoDocumento, numeroDocumento, telefono, fechaNacimiento, tieneRut, responsableIva)

    companion object {
        fun de(p: Perfil) =
            PerfilDto(p.nombre, p.tipoDocumento, p.numeroDocumento, p.telefono, p.fechaNacimiento, p.tieneRut, p.responsableIva)
    }
}

@RestController
@RequestMapping("/api/v1/perfil")
@Tag(name = "Perfil")
@SecurityRequirement(name = "firebase")
class PerfilController(
    private val consultar: ConsultarPerfil,
    private val guardar: GuardarPerfil,
    private val usuario: UsuarioActual,
) {
    @GetMapping
    @Operation(summary = "Perfil del usuario autenticado. 404 si aún no lo ha creado.")
    fun obtener(): ResponseEntity<PerfilDto> =
        consultar.ejecutar(usuario.id())?.let { ResponseEntity.ok(PerfilDto.de(it)) } ?: ResponseEntity.notFound().build()

    @PutMapping
    @Operation(summary = "Crea o actualiza el perfil del usuario autenticado.")
    fun guardar(@Valid @RequestBody body: PerfilDto): PerfilDto = PerfilDto.de(guardar.ejecutar(body.aDominio(usuario.id())))
}

package com.mirenta.declaracion.adaptadores.web

import com.mirenta.compartido.seguridad.UsuarioActual
import com.mirenta.declaracion.aplicacion.ConsultarDeclaracion
import com.mirenta.declaracion.aplicacion.ConsultarReglas
import com.mirenta.declaracion.aplicacion.DeclaracionCalculada
import com.mirenta.declaracion.aplicacion.GuardarDeclaracion
import com.mirenta.declaracion.aplicacion.SimularDeclaracion
import com.mirenta.declaracion.dominio.Entradas
import com.mirenta.declaracion.dominio.EstadoDeclaracion
import com.mirenta.declaracion.dominio.ReglasTributarias
import com.mirenta.declaracion.dominio.Resultado
import com.mirenta.declaracion.dominio.TramoTarifa
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EntradasDto(
    @field:PositiveOrZero val salario: Long = 0,
    @field:PositiveOrZero val honorarios: Long = 0,
    @field:PositiveOrZero val arriendos: Long = 0,
    @field:PositiveOrZero val intereses: Long = 0,
    @field:PositiveOrZero val aportes: Long = 0,
    @field:PositiveOrZero val vivienda: Long = 0,
    @field:Min(0) @field:Max(20) val dependientes: Long = 0,
    @field:PositiveOrZero val prepagada: Long = 0,
    @field:PositiveOrZero val afc: Long = 0,
    @field:PositiveOrZero val retenciones: Long = 0,
    @field:PositiveOrZero val patrimonio: Long = 0,
    @field:PositiveOrZero val deudas: Long = 0,
    @field:PositiveOrZero val consumos: Long = 0,
    val seccionesGuardadas: List<String> = emptyList(),
) {
    fun aDominio() = Entradas(
        salario, honorarios, arriendos, intereses, aportes, vivienda, dependientes,
        prepagada, afc, retenciones, patrimonio, deudas, consumos, seccionesGuardadas.toSet(),
    )

    companion object {
        fun de(e: Entradas) = EntradasDto(
            e.salario, e.honorarios, e.arriendos, e.intereses, e.aportes, e.vivienda, e.dependientes,
            e.prepagada, e.afc, e.retenciones, e.patrimonio, e.deudas, e.consumos, e.seccionesGuardadas.sorted(),
        )
    }
}

data class GuardarDeclaracionRequest(
    /** Versión que el cliente leyó; null si la declaración aún no existe. */
    val version: Long? = null,
    @field:Valid val entradas: EntradasDto,
)

data class DeclaracionDto(
    val anioGravable: Int,
    val version: Long?,
    val estado: EstadoDeclaracion,
    val entradas: EntradasDto,
    val resultado: Resultado,
) {
    companion object {
        fun de(c: DeclaracionCalculada) = DeclaracionDto(
            c.declaracion.anioGravable, c.declaracion.version, c.declaracion.estado,
            EntradasDto.de(c.declaracion.entradas), c.resultado,
        )
    }
}

data class ReglasDto(
    val anioGravable: Int,
    val uvt: Double,
    val parametrosUvt: Map<String, Double>,
    val porcentajes: Map<String, Double>,
    val tramosTarifa: List<TramoTarifa>,
) {
    companion object {
        fun de(r: ReglasTributarias) = ReglasDto(
            anioGravable = r.anioGravable,
            uvt = r.uvt.valor,
            parametrosUvt = mapOf(
                "topeInteresesVivienda" to r.topeInteresesViviendaUvt.valor,
                "topeSaludPrepagada" to r.topeSaludPrepagadaUvt.valor,
                "deduccionPorDependiente" to r.deduccionDependienteUvt.valor,
                "maxDependientes" to r.maxDependientes.valor,
                "topeAfc" to r.topeAfcUvt.valor,
                "topeRentaExentaLaboral" to r.topeRentaExentaLaboralUvt.valor,
                "topeLimiteConjunto" to r.topeLimiteConjuntoUvt.valor,
                "topeIngresosDeclarar" to r.topeIngresosDeclararUvt.valor,
                "topePatrimonioDeclarar" to r.topePatrimonioDeclararUvt.valor,
                "topeConsumosDeclarar" to r.topeConsumosDeclararUvt.valor,
            ),
            porcentajes = mapOf(
                "maxAfc" to r.porcentajeMaxAfc.valor,
                "rentaExentaLaboral" to r.porcentajeRentaExentaLaboral.valor,
                "limiteConjunto" to r.porcentajeLimiteConjunto.valor,
            ),
            tramosTarifa = r.tramosTarifa,
        )
    }
}

/** Adaptador primario: traduce HTTP/JSON a llamadas a los casos de uso. */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Declaración")
@SecurityRequirement(name = "firebase")
class DeclaracionController(
    private val consultar: ConsultarDeclaracion,
    private val guardar: GuardarDeclaracion,
    private val simular: SimularDeclaracion,
    private val consultarReglas: ConsultarReglas,
    private val usuario: UsuarioActual,
) {
    @GetMapping("/declaraciones/{anio}")
    @Operation(summary = "Declaración guardada del año, con su cálculo. 404 si aún no existe.")
    fun obtener(@PathVariable anio: Int): ResponseEntity<DeclaracionDto> =
        consultar.ejecutar(usuario.id(), anio)?.let { ResponseEntity.ok(DeclaracionDto.de(it)) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/declaraciones/{anio}")
    @Operation(summary = "Guarda las entradas y recalcula. Enviar la versión leída; 409 si otro cliente guardó antes.")
    fun guardar(@PathVariable anio: Int, @Valid @RequestBody body: GuardarDeclaracionRequest): DeclaracionDto =
        DeclaracionDto.de(guardar.ejecutar(usuario.id(), anio, body.version, body.entradas.aDominio()))

    @PostMapping("/declaraciones/{anio}/simulacion")
    @Operation(summary = "Calcula un escenario sin guardar nada.")
    fun simular(@PathVariable anio: Int, @Valid @RequestBody body: EntradasDto): Resultado =
        simular.ejecutar(usuario.id(), anio, body.aDominio())

    @GetMapping("/reglas/{anio}")
    @Operation(summary = "Reglas tributarias del año (UVT, topes, tarifa): configuración guiada por servidor.")
    fun reglas(@PathVariable anio: Int): ReglasDto = ReglasDto.de(consultarReglas.ejecutar(anio))
}

package com.mirenta.data.api

import com.mirenta.domain.Entradas
import com.mirenta.domain.PerfilDatos
import com.mirenta.domain.Resultado

data class PerfilDto(
    val nombre: String = "",
    val tipoDocumento: String = "Cédula de ciudadanía",
    val numeroDocumento: String = "",
    val telefono: String = "",
    val fechaNacimiento: String = "",
    val tieneRut: String = "Sí",
    val responsableIva: String = "No",
) {
    fun aDominio() = PerfilDatos(
        nombre = nombre,
        tipoDocumento = tipoDocumento,
        numeroDocumento = numeroDocumento,
        telefono = telefono,
        fechaNacimiento = fechaNacimiento,
        tieneRut = tieneRut,
        responsableIva = responsableIva,
    )

    companion object {
        fun de(p: PerfilDatos) = PerfilDto(
            nombre = p.nombre,
            tipoDocumento = p.tipoDocumento,
            numeroDocumento = p.numeroDocumento,
            telefono = p.telefono,
            fechaNacimiento = p.fechaNacimiento,
            tieneRut = p.tieneRut,
            responsableIva = p.responsableIva,
        )
    }
}

data class EntradasDto(
    val salario: Long = 0,
    val honorarios: Long = 0,
    val arriendos: Long = 0,
    val intereses: Long = 0,
    val aportes: Long = 0,
    val vivienda: Long = 0,
    val dependientes: Long = 0,
    val prepagada: Long = 0,
    val afc: Long = 0,
    val retenciones: Long = 0,
    val patrimonio: Long = 0,
    val deudas: Long = 0,
    val consumos: Long = 0,
    val seccionesGuardadas: List<String> = emptyList(),
) {
    fun aDominio() = Entradas(
        salario = salario,
        honorarios = honorarios,
        arriendos = arriendos,
        intereses = intereses,
        aportes = aportes,
        vivienda = vivienda,
        dependientes = dependientes,
        prepagada = prepagada,
        afc = afc,
        retenciones = retenciones,
        patrimonio = patrimonio,
        deudas = deudas,
        consumos = consumos,
        seccionesGuardadas = seccionesGuardadas.toSet(),
    )

    companion object {
        fun de(e: Entradas) = EntradasDto(
            salario = e.salario,
            honorarios = e.honorarios,
            arriendos = e.arriendos,
            intereses = e.intereses,
            aportes = e.aportes,
            vivienda = e.vivienda,
            dependientes = e.dependientes,
            prepagada = e.prepagada,
            afc = e.afc,
            retenciones = e.retenciones,
            patrimonio = e.patrimonio,
            deudas = e.deudas,
            consumos = e.consumos,
            seccionesGuardadas = e.seccionesGuardadas.sorted(),
        )
    }
}

data class ResultadoDto(
    val laborales: Double = 0.0,
    val capital: Double = 0.0,
    val noConstitutivos: Double = 0.0,
    val baseLaboral: Double = 0.0,
    val deducciones: Double = 0.0,
    val exenta: Double = 0.0,
    val limite: Double = 0.0,
    val beneficios: Double = 0.0,
    val rentaLiquidaTrabajo: Double = 0.0,
    val rentaGravable: Double = 0.0,
    val uvt: Double = 0.0,
    val impuesto: Double = 0.0,
    val saldo: Double = 0.0,
    val tarifaMarginal: Double = 0.0,
    val tarifaEfectiva: Double = 0.0,
    val patrimonioLiquido: Double = 0.0,
    val dependientesValor: Double = 0.0,
    val topeAlcanzado: Boolean = false,
    val motivos: List<String> = emptyList(),
) {
    fun aDominio() = Resultado(
        laborales = laborales,
        capital = capital,
        noConstitutivos = noConstitutivos,
        baseLaboral = baseLaboral,
        deducciones = deducciones,
        exenta = exenta,
        limite = limite,
        beneficios = beneficios,
        rentaLiquidaTrabajo = rentaLiquidaTrabajo,
        rentaGravable = rentaGravable,
        uvt = uvt,
        impuesto = impuesto,
        saldo = saldo,
        tarifaMarginal = tarifaMarginal,
        tarifaEfectiva = tarifaEfectiva,
        patrimonioLiquido = patrimonioLiquido,
        topeAlcanzado = topeAlcanzado,
        dependientesValor = dependientesValor,
        motivos = motivos,
    )
}

data class DeclaracionDto(
    val anioGravable: Int = 2025,
    val version: Long? = null,
    val estado: String = "BORRADOR",
    val entradas: EntradasDto = EntradasDto(),
    val resultado: ResultadoDto? = null,
)

data class GuardarDeclaracionRequest(
    val version: Long? = null,
    val entradas: EntradasDto = EntradasDto(),
)

data class ReglasDto(
    val anioGravable: Int = 2025,
    val uvt: Double = 0.0,
    val parametrosUvt: Map<String, Double> = emptyMap(),
    val porcentajes: Map<String, Double> = emptyMap(),
    val tramosTarifa: List<Map<String, Any?>> = emptyList(),
)

data class ErrorDto(
    val codigo: String = "",
    val mensaje: String = "",
)

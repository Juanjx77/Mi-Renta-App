package com.mirenta.declaracion.dominio

/** Strategy: parámetros y tarifa de un año gravable. Un año nuevo es una implementación nueva (OCP). */
interface ReglasTributarias {
    val anioGravable: Int
    val uvt: ParametroTributario<Double>
    val topeInteresesViviendaUvt: ParametroTributario<Double>
    val topeSaludPrepagadaUvt: ParametroTributario<Double>
    val maxDependientes: ParametroTributario<Double>
    val deduccionDependienteUvt: ParametroTributario<Double>
    val porcentajeMaxAfc: ParametroTributario<Double>
    val topeAfcUvt: ParametroTributario<Double>
    val porcentajeRentaExentaLaboral: ParametroTributario<Double>
    val topeRentaExentaLaboralUvt: ParametroTributario<Double>
    val porcentajeLimiteConjunto: ParametroTributario<Double>
    val topeLimiteConjuntoUvt: ParametroTributario<Double>
    val topeIngresosDeclararUvt: ParametroTributario<Double>
    val topeConsignacionesDeclararUvt: ParametroTributario<Double>
    val topePatrimonioDeclararUvt: ParametroTributario<Double>
    val topeConsumosDeclararUvt: ParametroTributario<Double>
    val tramosTarifa: List<TramoTarifa>

    fun impuestoEnUvt(u: Double): Double
    fun marginal(u: Double): Double
}

class AnioNoSoportadoException(anio: Int) : RuntimeException("No hay reglas tributarias para el año gravable $anio")

/** Factory: elige la estrategia de reglas según el año gravable. */
object ReglasFactory {
    private val porAnio: Map<Int, ReglasTributarias> = listOf(Reglas2025).associateBy { it.anioGravable }

    fun para(anio: Int): ReglasTributarias = porAnio[anio] ?: throw AnioNoSoportadoException(anio)

    fun aniosSoportados(): Set<Int> = porAnio.keys
}

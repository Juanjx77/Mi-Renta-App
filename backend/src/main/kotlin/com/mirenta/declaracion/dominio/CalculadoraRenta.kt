package com.mirenta.declaracion.dominio

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

data class Resultado(
    val laborales: Double,
    val capital: Double,
    val noConstitutivos: Double,
    val baseLaboral: Double,
    val deducciones: Double,
    val exenta: Double,
    val limite: Double,
    val beneficios: Double,
    val rentaLiquidaTrabajo: Double,
    val rentaGravable: Double,
    val uvt: Double,
    val impuesto: Double,
    val saldo: Double,
    val tarifaMarginal: Double,
    val tarifaEfectiva: Double,
    val patrimonioLiquido: Double,
    val topeAlcanzado: Boolean,
    val dependientesValor: Double,
    val motivos: List<String>,
)

/** Depuración simplificada de la renta de personas naturales; las cifras vienen de la estrategia de reglas del año. */
class CalculadoraRenta(private val reglas: ReglasTributarias) {
    val UVT = reglas.uvt.valor

    private fun impuestoEnUvt(u: Double): Double = reglas.impuestoEnUvt(u)

    private fun marginal(u: Double): Double = reglas.marginal(u)

    fun calcular(v: Entradas): Resultado {
        val lab = (v.salario + v.honorarios).toDouble()
        val cap = (v.arriendos + v.intereses).toDouble()
        val inc = min(v.aportes.toDouble(), lab)
        val base = lab - inc

        val viv = min(v.vivienda.toDouble(), reglas.topeInteresesViviendaUvt.valor * UVT)
        val pre = min(v.prepagada.toDouble(), reglas.topeSaludPrepagadaUvt.valor * UVT)
        val dep = min(v.dependientes.toDouble(), reglas.maxDependientes.valor) * reglas.deduccionDependienteUvt.valor * UVT
        val afc = minOf(v.afc.toDouble(), reglas.porcentajeMaxAfc.valor * lab, reglas.topeAfcUvt.valor * UVT)
        val ded = viv + pre + dep + afc
        val dedSinDep = viv + pre + afc

        val exe = min(reglas.porcentajeRentaExentaLaboral.valor * max(base - ded, 0.0), reglas.topeRentaExentaLaboralUvt.valor * UVT)
        val lim = min(reglas.porcentajeLimiteConjunto.valor * base, reglas.topeLimiteConjuntoUvt.valor * UVT)
        val dedComparacion = dedSinDep + exe
        val topeAlcanzado = dedComparacion >= lim && base > 0
        val ben = min(dedSinDep + exe, lim) + dep
        val rl = max(base - ben, 0.0)
        val rg = rl + cap
        val u = rg / UVT
        val imp = ((impuestoEnUvt(u) * UVT / 1000).roundToLong() * 1000).toDouble()

        val motivos = buildList {
            if (lab + cap >= reglas.topeIngresosDeclararUvt.valor * UVT) add("ingresos brutos ≥ 1.400 UVT (${fm(reglas.topeIngresosDeclararUvt.valor * UVT)})")
            if (v.patrimonio >= reglas.topePatrimonioDeclararUvt.valor * UVT) add("patrimonio bruto ≥ 4.500 UVT (${fm(reglas.topePatrimonioDeclararUvt.valor * UVT)})")
            if (v.consumos >= reglas.topeConsumosDeclararUvt.valor * UVT) add("consumos/compras ≥ 1.400 UVT")
        }

        return Resultado(
            laborales = lab,
            capital = cap,
            noConstitutivos = inc,
            baseLaboral = base,
            deducciones = ded,
            exenta = exe,
            limite = lim,
            beneficios = ben,
            rentaLiquidaTrabajo = rl,
            rentaGravable = rg,
            uvt = u,
            impuesto = imp,
            saldo = imp - v.retenciones,
            tarifaMarginal = marginal(u),
            tarifaEfectiva = if (lab + cap > 0) imp / (lab + cap) else 0.0,
            patrimonioLiquido = (v.patrimonio - v.deudas).toDouble(),
            topeAlcanzado = topeAlcanzado,
            dependientesValor = dep,
            motivos = motivos,
        )
    }
}

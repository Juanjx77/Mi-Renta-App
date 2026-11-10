package com.mirenta.domain

import com.mirenta.util.fm
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
    val motivos: List<String>,
)

/** Port directo del cálculo simplificado del prototipo HTML (año gravable 2025). */
object TaxCalculator {
    val UVT = Reglas2025.uvt.valor

    private fun impuestoEnUvt(u: Double): Double = Reglas2025.impuestoEnUvt(u)

    private fun marginal(u: Double): Double = Reglas2025.marginal(u)

    fun calcular(v: Entradas): Resultado {
        val lab = (v.salario + v.honorarios).toDouble()
        val cap = (v.arriendos + v.intereses).toDouble()
        val inc = min(v.aportes.toDouble(), lab)
        val base = lab - inc

        val viv = min(v.vivienda.toDouble(), Reglas2025.topeInteresesViviendaUvt.valor * UVT)
        val pre = min(v.prepagada.toDouble(), Reglas2025.topeSaludPrepagadaUvt.valor * UVT)
        val dep = min(v.dependientes.toDouble(), Reglas2025.maxDependientes.valor) * Reglas2025.deduccionDependienteUvt.valor * UVT
        val afc = minOf(v.afc.toDouble(), Reglas2025.porcentajeMaxAfc.valor * lab, Reglas2025.topeAfcUvt.valor * UVT)
        val ded = viv + pre + dep + afc
        val dedSinDep = viv + pre + afc

        val exe = min(Reglas2025.porcentajeRentaExentaLaboral.valor * max(base - ded, 0.0), Reglas2025.topeRentaExentaLaboralUvt.valor * UVT)
        val lim = min(Reglas2025.porcentajeLimiteConjunto.valor * base, Reglas2025.topeLimiteConjuntoUvt.valor * UVT)
        val ben = min(dedSinDep + exe, lim) + dep
        val rl = max(base - ben, 0.0)
        val rg = rl + cap
        val u = rg / UVT
        val imp = ((impuestoEnUvt(u) * UVT / 1000).roundToLong() * 1000).toDouble()

        val motivos = buildList {
            if (lab + cap >= Reglas2025.topeIngresosDeclararUvt.valor * UVT) add("ingresos brutos ≥ 1.400 UVT (${fm(Reglas2025.topeIngresosDeclararUvt.valor * UVT)})")
            if (v.patrimonio >= Reglas2025.topePatrimonioDeclararUvt.valor * UVT) add("patrimonio bruto ≥ 4.500 UVT (${fm(Reglas2025.topePatrimonioDeclararUvt.valor * UVT)})")
            if (v.consumos >= Reglas2025.topeConsumosDeclararUvt.valor * UVT) add("consumos/compras ≥ 1.400 UVT")
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
            motivos = motivos,
        )
    }
}

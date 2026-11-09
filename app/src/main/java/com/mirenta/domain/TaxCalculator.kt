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
    const val UVT = Reglas2025.UVT

    private fun impuestoEnUvt(u: Double): Double = Reglas2025.impuestoEnUvt(u)

    private fun marginal(u: Double): Double = Reglas2025.marginal(u)

    fun calcular(v: Entradas): Resultado {
        val lab = (v.salario + v.honorarios).toDouble()
        val cap = (v.arriendos + v.intereses).toDouble()
        val inc = min(v.aportes.toDouble(), lab)
        val base = lab - inc

        val viv = min(v.vivienda.toDouble(), Reglas2025.TOPE_INTERESES_VIVIENDA_UVT * UVT)
        val pre = min(v.prepagada.toDouble(), Reglas2025.TOPE_SALUD_PREPAGADA_UVT * UVT)
        val dep = min(v.dependientes.toDouble(), Reglas2025.MAX_DEPENDIENTES) * Reglas2025.DEDUCCION_DEPENDIENTE_UVT * UVT
        val afc = minOf(v.afc.toDouble(), Reglas2025.PORCENTAJE_MAX_AFC * lab, Reglas2025.TOPE_AFC_UVT * UVT)
        val ded = viv + pre + dep + afc

        val exe = min(Reglas2025.PORCENTAJE_RENTA_EXENTA_LABORAL * max(base - ded, 0.0), Reglas2025.TOPE_RENTA_EXENTA_LABORAL_UVT * UVT)
        val lim = min(Reglas2025.PORCENTAJE_LIMITE_CONJUNTO * base, Reglas2025.TOPE_LIMITE_CONJUNTO_UVT * UVT)
        val ben = min(ded + exe, lim)
        val rl = max(base - ben, 0.0)
        val rg = rl + cap
        val u = rg / UVT
        val imp = ((impuestoEnUvt(u) * UVT / 1000).roundToLong() * 1000).toDouble()

        val motivos = buildList {
            if (lab + cap >= Reglas2025.TOPE_INGRESOS_DECLARAR_UVT * UVT) add("ingresos brutos ≥ 1.400 UVT (${fm(Reglas2025.TOPE_INGRESOS_DECLARAR_UVT * UVT)})")
            if (v.patrimonio >= Reglas2025.TOPE_PATRIMONIO_DECLARAR_UVT * UVT) add("patrimonio bruto ≥ 4.500 UVT (${fm(Reglas2025.TOPE_PATRIMONIO_DECLARAR_UVT * UVT)})")
            if (v.consumos >= Reglas2025.TOPE_CONSUMOS_DECLARAR_UVT * UVT) add("consumos/compras ≥ 1.400 UVT")
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

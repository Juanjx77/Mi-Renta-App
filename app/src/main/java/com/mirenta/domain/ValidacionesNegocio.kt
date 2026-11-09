package com.mirenta.domain

enum class Severidad {
    ERROR,
    ADVERTENCIA,
    INFO
}

data class Hallazgo(
    val severidad: Severidad,
    val codigo: String,
    val mensaje: String
)

object ValidacionesNegocio {
    fun validar(v: Entradas): List<Hallazgo> {
        val hallazgos = mutableListOf<Hallazgo>()

        // 1. ERROR: montos negativos
        val camposNegativos = listOf(
            "Salario" to v.salario,
            "Honorarios" to v.honorarios,
            "Arriendos" to v.arriendos,
            "Intereses" to v.intereses,
            "Aportes" to v.aportes,
            "Intereses de vivienda" to v.vivienda,
            "Dependientes" to v.dependientes,
            "Salud prepagada" to v.prepagada,
            "AFC" to v.afc,
            "Retenciones" to v.retenciones,
            "Patrimonio" to v.patrimonio,
            "Deudas" to v.deudas,
            "Consumos" to v.consumos
        ).filter { it.second < 0 }

        if (camposNegativos.isNotEmpty()) {
            val nombres = camposNegativos.joinToString(", ") { it.first }
            hallazgos.add(
                Hallazgo(
                    Severidad.ERROR,
                    "MONTO_NEGATIVO",
                    "Se encontraron montos negativos en: $nombres. Los valores no pueden ser negativos."
                )
            )
        }

        // 2. ERROR: retenciones mayores que los ingresos totales
        val ingresosTotales = (v.salario + v.honorarios + v.arriendos + v.intereses).toDouble()
        if (v.retenciones.toDouble() > ingresosTotales && ingresosTotales >= 0) {
            hallazgos.add(
                Hallazgo(
                    Severidad.ERROR,
                    "RETENCIONES_MAYORES_INGRESOS",
                    "Las retenciones en la fuente no pueden ser mayores que los ingresos brutos totales."
                )
            )
        }

        // 3. ADVERTENCIA: más de 4 dependientes declarados
        if (v.dependientes > 4) {
            hallazgos.add(
                Hallazgo(
                    Severidad.ADVERTENCIA,
                    "EXCESO_DEPENDIENTES",
                    "Has declarado más de 4 dependientes. Ten en cuenta que la norma y TaxCalculator solo reconocen un máximo de 4 dependientes."
                )
            )
        }

        // 4. ADVERTENCIA: deducciones que superan el tope que TaxCalculator ya usa
        val lab = (v.salario + v.honorarios).toDouble()
        val inc = minOf(v.aportes.toDouble(), lab)
        val base = lab - inc
        val viv = minOf(v.vivienda.toDouble(), 1200 * TaxCalculator.UVT)
        val pre = minOf(v.prepagada.toDouble(), 192 * TaxCalculator.UVT)
        val dep = minOf(v.dependientes.toDouble(), 4.0) * 72 * TaxCalculator.UVT
        val afc = minOf(v.afc.toDouble(), 0.3 * lab, 3800 * TaxCalculator.UVT)
        val ded = viv + pre + dep + afc
        val lim = minOf(0.4 * base, 1340 * TaxCalculator.UVT)

        if (ded > lim && base > 0) {
            hallazgos.add(
                Hallazgo(
                    Severidad.ADVERTENCIA,
                    "DEDUCCIONES_SUPERAN_TOPE",
                    "El total de deducciones y rentas exentas supera el límite máximo permitido (40% de la renta líquida o 1.340 UVT). TaxCalculator aplicará el tope correspondiente."
                )
            )
        }

        // 5. ADVERTENCIA: patrimonio líquido negativo
        val patrimonioLiquido = v.patrimonio - v.deudas
        if (patrimonioLiquido < 0) {
            hallazgos.add(
                Hallazgo(
                    Severidad.ADVERTENCIA,
                    "PATRIMONIO_LIQUIDO_NEGATIVO",
                    "El patrimonio líquido es negativo (tus deudas superan a tu patrimonio bruto)."
                )
            )
        }

        // 6. INFO: ingresos sin soporte adjunto
        if (ingresosTotales > 0) {
            hallazgos.add(
                Hallazgo(
                    Severidad.INFO,
                    "INGRESOS_SIN_SOPORTE",
                    "Tienes ingresos registrados. Asegúrate de adjuntar los soportes correspondientes (certificados de ingresos, extractos, etc.)."
                )
            )
        }

        return hallazgos
    }
}

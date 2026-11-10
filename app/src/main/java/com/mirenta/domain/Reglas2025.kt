package com.mirenta.domain

data class ParametroTributario<T>(
    val valor: T,
    val descripcion: String,
    val fuente: String,
    val verificada: Boolean
)

data class TramoTarifa(
    val limiteUvt: Double,
    val tarifa: Double,
    val impuestoBaseUvt: Double,
    val descripcion: String,
    val fuente: String,
    val verificada: Boolean
)

object Reglas2025 {
    val uvt = ParametroTributario(
        valor = 49_799.0,
        descripcion = "Valor de la Unidad de Valor Tributario (UVT) para el año gravable 2025",
        fuente = "Estatuto Tributario / DIAN 2025",
        verificada = true
    )

    val topeInteresesViviendaUvt = ParametroTributario(
        valor = 1200.0,
        descripcion = "Tope máximo de intereses de vivienda deducibles en UVT",
        fuente = "Art. 387 ET (100 UVT mensuales)",
        verificada = true
    )

    val topeSaludPrepagadaUvt = ParametroTributario(
        valor = 192.0,
        descripcion = "Tope máximo de salud prepagada deducible en UVT (16 UVT mensuales)",
        fuente = "Art. 387 ET (16 UVT mensuales)",
        verificada = true
    )

    // Nota: según doctrina, esta deducción es adicional al límite del 40 % / 1.340 UVT; TaxCalculator ya la aplica fuera del límite (ver testCaso5)
    val maxDependientes = ParametroTributario(
        valor = 4.0,
        descripcion = "Número máximo de dependientes reconocidos tributariamente",
        fuente = "Art. 336 num. 3 inciso 2 ET, Ley 2277 de 2022 art. 7",
        verificada = true
    )

    // Nota: según doctrina, esta deducción es adicional al límite del 40 % / 1.340 UVT; TaxCalculator ya la aplica fuera del límite (ver testCaso5)
    val deduccionDependienteUvt = ParametroTributario(
        valor = 72.0,
        descripcion = "Deducción por cada dependiente en UVT",
        fuente = "Art. 336 num. 3 inciso 2 ET, Ley 2277 de 2022 art. 7",
        verificada = true
    )

    val porcentajeMaxAfc = ParametroTributario(
        valor = 0.3,
        descripcion = "Porcentaje máximo de ingresos para aporte a AFC o pensión voluntaria (30%)",
        fuente = "Arts. 126-1 y 126-4 ET",
        verificada = true
    )

    val topeAfcUvt = ParametroTributario(
        valor = 3800.0,
        descripcion = "Tope máximo de aportes AFC o pensión voluntaria en UVT",
        fuente = "Arts. 126-1 y 126-4 ET",
        verificada = true
    )

    val porcentajeRentaExentaLaboral = ParametroTributario(
        valor = 0.25,
        descripcion = "Porcentaje de renta exenta laboral (25%)",
        fuente = "Art. 206 num. 10 ET, Ley 2277 de 2022 art. 2",
        verificada = true
    )

    val topeRentaExentaLaboralUvt = ParametroTributario(
        valor = 790.0,
        descripcion = "Tope máximo de renta exenta laboral en UVT",
        fuente = "Art. 206 num. 10 ET, Ley 2277 de 2022 art. 2",
        verificada = true
    )

    val porcentajeLimiteConjunto = ParametroTributario(
        valor = 0.4,
        descripcion = "Porcentaje límite conjunto de deducciones y rentas exentas (40%)",
        fuente = "Art. 336 num. 3 ET, Ley 2277 de 2022 art. 7",
        verificada = true
    )

    val topeLimiteConjuntoUvt = ParametroTributario(
        valor = 1340.0,
        descripcion = "Tope máximo UVT del límite conjunto de deducciones y rentas exentas",
        fuente = "Art. 336 num. 3 ET, Ley 2277 de 2022 art. 7",
        verificada = true
    )

    val topeIngresosDeclararUvt = ParametroTributario(
        valor = 1400.0,
        descripcion = "Tope de ingresos brutos en UVT para obligación de declarar",
        fuente = "Estatuto Tributario",
        verificada = true
    )

    val topeConsignacionesDeclararUvt = ParametroTributario(
        valor = 1400.0,
        descripcion = "Tope de consignaciones bancarias en UVT para obligación de declarar",
        fuente = "Estatuto Tributario",
        verificada = true
    )

    val topePatrimonioDeclararUvt = ParametroTributario(
        valor = 4500.0,
        descripcion = "Tope de patrimonio bruto en UVT para obligación de declarar",
        fuente = "Estatuto Tributario",
        verificada = true
    )

    val topeConsumosDeclararUvt = ParametroTributario(
        valor = 1400.0,
        descripcion = "Tope de consumos y compras en UVT para obligación de declarar",
        fuente = "Estatuto Tributario",
        verificada = true
    )

    val tramosTarifa = listOf(
        TramoTarifa(1090.0, 0.0, 0.0, "Tramo 1 (hasta 1.090 UVT)", "Art. 241 ET", true),
        TramoTarifa(1700.0, 0.19, 0.0, "Tramo 2 (1.090 a 1.700 UVT)", "Art. 241 ET", true),
        TramoTarifa(4100.0, 0.28, 116.0, "Tramo 3 (1.700 a 4.100 UVT)", "Art. 241 ET", true),
        TramoTarifa(8670.0, 0.33, 788.0, "Tramo 4 (4.100 a 8.670 UVT)", "Art. 241 ET", true),
        TramoTarifa(18970.0, 0.35, 2296.0, "Tramo 5 (8.670 a 18.970 UVT)", "Art. 241 ET", true),
        TramoTarifa(31000.0, 0.37, 5901.0, "Tramo 6 (18.970 a 31.000 UVT)", "Art. 241 ET", true),
        TramoTarifa(Double.MAX_VALUE, 0.39, 10352.0, "Tramo 7 (más de 31.000 UVT)", "Art. 241 ET", true)
    )

    fun impuestoEnUvt(u: Double): Double = when {
        u <= tramosTarifa[0].limiteUvt -> 0.0
        u <= tramosTarifa[1].limiteUvt -> (u - tramosTarifa[0].limiteUvt) * tramosTarifa[1].tarifa
        u <= tramosTarifa[2].limiteUvt -> (u - tramosTarifa[1].limiteUvt) * tramosTarifa[2].tarifa + tramosTarifa[2].impuestoBaseUvt
        u <= tramosTarifa[3].limiteUvt -> (u - tramosTarifa[2].limiteUvt) * tramosTarifa[3].tarifa + tramosTarifa[3].impuestoBaseUvt
        u <= tramosTarifa[4].limiteUvt -> (u - tramosTarifa[3].limiteUvt) * tramosTarifa[4].tarifa + tramosTarifa[4].impuestoBaseUvt
        u <= tramosTarifa[5].limiteUvt -> (u - tramosTarifa[4].limiteUvt) * tramosTarifa[5].tarifa + tramosTarifa[5].impuestoBaseUvt
        else -> (u - tramosTarifa[5].limiteUvt) * tramosTarifa[6].tarifa + tramosTarifa[6].impuestoBaseUvt
    }

    fun marginal(u: Double): Double = when {
        u <= tramosTarifa[0].limiteUvt -> tramosTarifa[0].tarifa
        u <= tramosTarifa[1].limiteUvt -> tramosTarifa[1].tarifa
        u <= tramosTarifa[2].limiteUvt -> tramosTarifa[2].tarifa
        u <= tramosTarifa[3].limiteUvt -> tramosTarifa[3].tarifa
        u <= tramosTarifa[4].limiteUvt -> tramosTarifa[4].tarifa
        u <= tramosTarifa[5].limiteUvt -> tramosTarifa[5].tarifa
        else -> tramosTarifa[6].tarifa
    }
}

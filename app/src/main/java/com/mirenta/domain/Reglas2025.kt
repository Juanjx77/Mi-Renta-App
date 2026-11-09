package com.mirenta.domain

/**
 * Reglas y parámetros tributarios para el año gravable 2025.
 * Cada constante incluye su concepto y verificado = false.
 */
object Reglas2025 {
    // Valor de la Unidad de Valor Tributario (UVT) para 2025
    const val UVT = 49_799.0 // verificada = false

    // Tope máximo de intereses de vivienda deducibles en UVT (1.200 UVT)
    const val TOPE_INTERESES_VIVIENDA_UVT = 1200.0 // verificada = false

    // Tope máximo de salud prepagada deducible en UVT (192 UVT = 16 UVT/mes)
    const val TOPE_SALUD_PREPAGADA_UVT = 192.0 // verificada = false

    // Número máximo de dependientes reconocidos tributariamente (4)
    const val MAX_DEPENDIENTES = 4.0 // verificada = false

    // Deducción por cada dependiente en UVT (72 UVT)
    const val DEDUCCION_DEPENDIENTE_UVT = 72.0 // verificada = false

    // Porcentaje máximo de ingresos para aporte a AFC o pensión voluntaria (30%)
    const val PORCENTAJE_MAX_AFC = 0.3 // verificada = false

    // Tope máximo de aportes AFC o pensión voluntaria en UVT (3.800 UVT)
    const val TOPE_AFC_UVT = 3800.0 // verificada = false

    // Porcentaje de renta exenta laboral (25%)
    const val PORCENTAJE_RENTA_EXENTA_LABORAL = 0.25 // verificada = false

    // Tope máximo de renta exenta laboral en UVT (790 UVT)
    const val TOPE_RENTA_EXENTA_LABORAL_UVT = 790.0 // verificada = false

    // Porcentaje límite conjunto de deducciones y rentas exentas (40%)
    const val PORCENTAJE_LIMITE_CONJUNTO = 0.4 // verificada = false

    // Tope máximo UVT del límite conjunto de deducciones y rentas exentas (1.340 UVT)
    const val TOPE_LIMITE_CONJUNTO_UVT = 1340.0 // verificada = false

    // Tope de ingresos brutos en UVT para obligación de declarar (1.400 UVT)
    const val TOPE_INGRESOS_DECLARAR_UVT = 1400.0 // verificada = false

    // Tope de patrimonio bruto en UVT para obligación de declarar (4.500 UVT)
    const val TOPE_PATRIMONIO_DECLARAR_UVT = 4500.0 // verificada = false

    // Tope de consumos/compras en UVT para obligación de declarar (1.400 UVT)
    const val TOPE_CONSUMOS_DECLARAR_UVT = 1400.0 // verificada = false

    /**
     * Calcula el impuesto en UVT según la tabla de tarifas del Art. 241 E.T.
     * Los umbrales, tarifas y gravámenes se mantienen exactamente igual.
     */
    fun impuestoEnUvt(u: Double): Double = when {
        u <= 1090 -> 0.0 // verificada = false
        u <= 1700 -> (u - 1090) * 0.19 // verificada = false (tarifa 19%)
        u <= 4100 -> (u - 1700) * 0.28 + 116.0 // verificada = false (tarifa 28%, base 116 UVT)
        u <= 8670 -> (u - 4100) * 0.33 + 788.0 // verificada = false (tarifa 33%, base 788 UVT)
        u <= 18970 -> (u - 8670) * 0.35 + 2296.0 // verificada = false (tarifa 35%, base 2296 UVT)
        u <= 31000 -> (u - 18970) * 0.37 + 5901.0 // verificada = false (tarifa 37%, base 5901 UVT)
        else -> (u - 31000) * 0.39 + 10352.0 // verificada = false (tarifa 39%, base 10352 UVT)
    }

    /**
     * Obtiene la tarifa marginal en función de los rangos en UVT.
     */
    fun marginal(u: Double): Double = when {
        u <= 1090 -> 0.0 // verificada = false
        u <= 1700 -> 0.19 // verificada = false
        u <= 4100 -> 0.28 // verificada = false
        u <= 8670 -> 0.33 // verificada = false
        u <= 18970 -> 0.35 // verificada = false
        u <= 31000 -> 0.37 // verificada = false
        else -> 0.39 // verificada = false
    }
}

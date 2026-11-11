package com.mirenta.domain

object SeccionesDeclaracion {
    const val Ingresos = "Ingresos"
    const val Patrimonio = "Patrimonio"
    const val Deducciones = "Deducciones"
    const val Retenciones = "Retenciones"
}

/** Campos que el usuario puede editar en el simulador. */
enum class Campo(
    val seccion: String,
    val etiqueta: String,
    val ayuda: String,
    val ejemplo: Long,
    val soloCantidad: Boolean = false,
) {
    Salario("INGRESOS", "Salario anual", "Lo que te pagó tu empleador en el año", 96_000_000),
    Honorarios("", "Honorarios", "Pagos por servicios, sin relación laboral", 24_000_000),
    Arriendos("", "Arriendos", "Ingreso de capital por alquilar un bien", 12_000_000),
    Intereses("", "Intereses financieros", "Rendimientos de cuentas o CDT", 600_000),
    Aportes("RESTAN DE TUS INGRESOS", "Aportes obligatorios a salud y pensión", "No constituyen renta: se restan completos", 7_680_000),
    Vivienda("", "Intereses de vivienda", "Tope: 1.200 UVT al año", 3_000_000),
    Dependientes("", "Número de dependientes", "72 UVT por cada uno, máximo 4 (simplificado)", 0, soloCantidad = true),
    Prepagada("", "Salud prepagada", "Tope: 16 UVT al mes", 0),
    Afc("", "AFC o pensión voluntaria", "Tope: 30% del ingreso y 3.800 UVT", 0),
    Retenciones("YA PAGADO Y PATRIMONIO", "Retenciones en la fuente", "Anticipo que ya te descontaron", 6_600_000),
    Patrimonio("", "Patrimonio bruto", "Todo lo que tienes al 31 de diciembre", 223_000_000),
    Deudas("", "Deudas", "Restan solo para el patrimonio líquido", 90_000_000),
    Consumos("", "Consumos con tarjeta y compras del año", "Si superan 1.400 UVT, debes declarar", 0),
}

data class Entradas(
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
    val seccionesGuardadas: Set<String> = emptySet(),
) {
    operator fun get(c: Campo): Long = when (c) {
        Campo.Salario -> salario
        Campo.Honorarios -> honorarios
        Campo.Arriendos -> arriendos
        Campo.Intereses -> intereses
        Campo.Aportes -> aportes
        Campo.Vivienda -> vivienda
        Campo.Dependientes -> dependientes
        Campo.Prepagada -> prepagada
        Campo.Afc -> afc
        Campo.Retenciones -> retenciones
        Campo.Patrimonio -> patrimonio
        Campo.Deudas -> deudas
        Campo.Consumos -> consumos
    }

    fun con(c: Campo, v: Long): Entradas = when (c) {
        Campo.Salario -> copy(salario = v)
        Campo.Honorarios -> copy(honorarios = v)
        Campo.Arriendos -> copy(arriendos = v)
        Campo.Intereses -> copy(intereses = v)
        Campo.Aportes -> copy(aportes = v)
        Campo.Vivienda -> copy(vivienda = v)
        Campo.Dependientes -> copy(dependientes = v)
        Campo.Prepagada -> copy(prepagada = v)
        Campo.Afc -> copy(afc = v)
        Campo.Retenciones -> copy(retenciones = v)
        Campo.Patrimonio -> copy(patrimonio = v)
        Campo.Deudas -> copy(deudas = v)
        Campo.Consumos -> copy(consumos = v)
    }

    companion object {
        /** Cifras de ejemplo (las mismas del prototipo HTML). */
        val Ejemplo: Entradas = Campo.entries.fold(Entradas()) { acc, c -> acc.con(c, c.ejemplo) }
    }
}

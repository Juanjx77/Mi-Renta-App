package com.mirenta.declaracion.dominio

/** Valores que el contribuyente registra en su declaración (pesos colombianos). */
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
}

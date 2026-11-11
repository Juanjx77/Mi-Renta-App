package com.mirenta.domain

import com.mirenta.util.dec
import com.mirenta.util.fm
import kotlin.math.abs

data class LineaDoc(val etiqueta: String, val valor: String)
data class SeccionDoc(val titulo: String, val lineas: List<LineaDoc>)

object DocumentoDeclaracion {
    fun enmascararDocumento(doc: String): String {
        val limpio = doc.trim()
        if (limpio.length <= 4) return limpio
        return "*".repeat(limpio.length - 4) + limpio.takeLast(4)
    }

    fun generar(
        entradas: Entradas,
        resultado: Resultado,
        hallazgos: List<Hallazgo>,
        perfil: PerfilDatos?,
        fecha: String
    ): List<SeccionDoc> {
        val s = entradas.seccionesGuardadas
        val tieneIngresos = s.contains(SeccionesDeclaracion.Ingresos)
        val tieneDeducciones = s.contains(SeccionesDeclaracion.Deducciones)
        val tieneRetenciones = s.contains(SeccionesDeclaracion.Retenciones)
        val tienePatrimonio = s.contains(SeccionesDeclaracion.Patrimonio)

        val nombreContribuyente = perfil?.nombre?.takeIf { it.isNotBlank() } ?: "[Nombre y apellidos]"
        val tipoDoc = perfil?.tipoDocumento ?: "Cédula de ciudadanía"
        val numDocRaw = perfil?.numeroDocumento?.takeIf { it.isNotBlank() } ?: "00000000"
        val documentoEnmascarado = enmascararDocumento(numDocRaw)

        val porcentajeLimite = (Reglas2025.porcentajeLimiteConjunto.valor * 100).toInt()

        return listOf(
            SeccionDoc(
                "Encabezado y Fecha",
                listOf(
                    LineaDoc("Documento", "Simulación de declaración de renta · Personas naturales · Formulario 210"),
                    LineaDoc("Año gravable", "2025"),
                    LineaDoc("Fecha de generación", fecha)
                )
            ),
            SeccionDoc(
                "Aviso Legal",
                listOf(
                    LineaDoc("Estado", "Borrador informativo. No reemplaza la asesoría de un contador ni está presentado ante la DIAN.")
                )
            ),
            SeccionDoc(
                "1. Contribuyente",
                listOf(
                    LineaDoc("Nombre", nombreContribuyente),
                    LineaDoc("Identificación", "$tipoDoc · $documentoEnmascarado")
                )
            ),
            SeccionDoc(
                "2. Ingresos",
                listOf(
                    LineaDoc("Salario anual", if (tieneIngresos) fm(entradas.salario.toDouble()) else "Sin datos"),
                    LineaDoc("Honorarios", if (tieneIngresos) fm(entradas.honorarios.toDouble()) else "Sin datos"),
                    LineaDoc("Arriendos", if (tieneIngresos) fm(entradas.arriendos.toDouble()) else "Sin datos"),
                    LineaDoc("Intereses financieros", if (tieneIngresos) fm(entradas.intereses.toDouble()) else "Sin datos"),
                    LineaDoc("Consumos y compras", if (tieneIngresos) fm(entradas.consumos.toDouble()) else "Sin datos")
                )
            ),
            SeccionDoc(
                "3. Deducciones",
                listOf(
                    LineaDoc("Aportes obligatorios a salud y pensión", if (tieneDeducciones) fm(entradas.aportes.toDouble()) else "Sin datos"),
                    LineaDoc("Intereses de vivienda", if (tieneDeducciones) fm(entradas.vivienda.toDouble()) else "Sin datos"),
                    LineaDoc("Dependientes", if (tieneDeducciones) entradas.dependientes.toString() else "Sin datos"),
                    LineaDoc("Salud prepagada", if (tieneDeducciones) fm(entradas.prepagada.toDouble()) else "Sin datos"),
                    LineaDoc("AFC / pensión voluntaria", if (tieneDeducciones) fm(entradas.afc.toDouble()) else "Sin datos")
                )
            ),
            SeccionDoc(
                "4. Retenciones",
                listOf(
                    LineaDoc("Retenciones en la fuente", if (tieneRetenciones) fm(entradas.retenciones.toDouble()) else "Sin datos")
                )
            ),
            SeccionDoc(
                "5. Patrimonio",
                listOf(
                    LineaDoc("Patrimonio bruto", if (tienePatrimonio) fm(entradas.patrimonio.toDouble()) else "Sin datos"),
                    LineaDoc("Deudas", if (tienePatrimonio) fm(entradas.deudas.toDouble()) else "Sin datos"),
                    LineaDoc("Patrimonio líquido", if (tienePatrimonio) fm(resultado.patrimonioLiquido) else "Sin datos")
                )
            ),
            SeccionDoc(
                "6. Depuración y Liquidación",
                listOf(
                    LineaDoc("Ingresos laborales", if (tieneIngresos) fm(resultado.laborales) else "Sin datos"),
                    LineaDoc("(−) No constitutivos de renta", if (tieneIngresos) fm(resultado.noConstitutivos) else "Sin datos"),
                    LineaDoc("(−) Deducciones", if (tieneDeducciones) fm(resultado.deducciones) else "Sin datos"),
                    LineaDoc("(−) Renta exenta 25%", if (tieneDeducciones || tieneIngresos) fm(resultado.exenta) else "Sin datos"),
                    LineaDoc("Renta líquida de trabajo", if (tieneIngresos || tieneDeducciones) fm(resultado.rentaLiquidaTrabajo) else "Sin datos"),
                    LineaDoc("(+) Rentas de capital", if (tieneIngresos) fm(resultado.capital) else "Sin datos"),
                    LineaDoc("Renta gravable", if (tieneIngresos) "${fm(resultado.rentaGravable)} · ${dec(resultado.uvt, 2)} UVT" else "Sin datos"),
                    LineaDoc("Impuesto a cargo", if (tieneIngresos) fm(resultado.impuesto) else "Sin datos"),
                    LineaDoc("(−) Retenciones", if (tieneRetenciones) fm(entradas.retenciones.toDouble()) else "Sin datos"),
                    LineaDoc(if (resultado.saldo < 0) "Saldo a favor" else "Saldo a pagar", if (tieneIngresos) fm(abs(resultado.saldo)) else "Sin datos"),
                    LineaDoc("Tarifa efectiva", if (tieneIngresos) "${dec(resultado.tarifaEfectiva * 100, 1)} %" else "Sin datos")
                )
            ),
            SeccionDoc(
                "7. Motivos de Obligación de Declarar",
                listOf(
                    LineaDoc("Motivos", if (resultado.motivos.isNotEmpty()) resultado.motivos.joinToString("; ") + "." else "Con estos datos no superas ningún tope para declarar.")
                )
            ),
            SeccionDoc(
                "8. Hallazgos de Validación",
                listOf(
                    LineaDoc("Resultados", if (hallazgos.isEmpty()) "Sin hallazgos" else hallazgos.joinToString("\n") { "[${it.severidad}] ${it.mensaje}" })
                )
            ),
            SeccionDoc(
                "9. Parámetros Usados (Reglas 2025)",
                listOf(
                    LineaDoc("UVT año gravable 2025", "${fm(Reglas2025.uvt.valor)} (Fuente: DIAN)"),
                    LineaDoc("Límite conjunto deducciones", "${Reglas2025.topeLimiteConjuntoUvt.valor.toInt()} UVT y $porcentajeLimite % (Fuente: Estatuto Tributario)")
                )
            ),
            SeccionDoc(
                "10. Limitaciones",
                listOf(
                    LineaDoc("Nota", "Este modelo no modela costos de rentas de capital, dependientes del Art. 387 ni la deducción del 1 % por factura electrónica. No contrastado con el simulador oficial de la DIAN.")
                )
            )
        )
    }
}

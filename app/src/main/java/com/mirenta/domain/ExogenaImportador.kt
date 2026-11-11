package com.mirenta.domain

data class ErrorFila(
    val linea: Int,
    val motivo: String
)

data class ResultadoImportacion(
    val filas: Int,
    val salario: Long,
    val honorarios: Long,
    val arriendos: Long,
    val intereses: Long,
    val aportes: Long,
    val retenciones: Long,
    val errores: List<ErrorFila>
)

/** Lector de la plantilla de exógena de Mi Renta en Kotlin puro (sin imports de Android). */
object ExogenaImportador {
    private val headersEsperados = listOf("pagador", "tipo_renta", "valor_bruto", "aporte_salud", "aporte_pension", "retencion")

    fun leer(texto: String): ResultadoImportacion {
        val lineasOriginales = texto.lines()
            .map { it.removePrefix("\uFEFF") } // Ignorar BOM

        val lineasFiltradas = mutableListOf<Pair<Int, String>>()
        for ((index, linea) in lineasOriginales.withIndex()) {
            if (linea.isNotBlank()) {
                lineasFiltradas.add((index + 1) to linea.trim())
            }
        }

        if (lineasFiltradas.isEmpty()) {
            return ResultadoImportacion(0, 0, 0, 0, 0, 0, 0, listOf(ErrorFila(1, "Archivo vacío")))
        }

        val (_, headerLinea) = lineasFiltradas[0]
        val delimitador = if (headerLinea.contains(';')) ';' else ','
        val headers = headerLinea.split(delimitador).map { it.trim().lowercase() }

        if (headers != headersEsperados) {
            return ResultadoImportacion(0, 0, 0, 0, 0, 0, 0, listOf(ErrorFila(1, "Encabezado incorrecto o ausente")))
        }

        val dataLines = lineasFiltradas.drop(1)
        if (dataLines.size > 500) {
            return ResultadoImportacion(0, 0, 0, 0, 0, 0, 0, listOf(ErrorFila(1, "El archivo excede el máximo de 500 filas de datos")))
        }

        var filasValidas = 0
        var salario = 0L
        var honorarios = 0L
        var arriendos = 0L
        var intereses = 0L
        var aportes = 0L
        var retenciones = 0L
        val errores = mutableListOf<ErrorFila>()

        for ((lineaNum, rawLine) in dataLines) {
            val cols = rawLine.split(delimitador).map { it.trim() }
            if (cols.size != 6) {
                errores.add(ErrorFila(lineaNum, "Número de columnas inválido (esperadas 6, encontradas ${cols.size})"))
                continue
            }

            val tipoRentaRaw = cols[1]
            val tipoRentaNorm = normalizarTipoRenta(tipoRentaRaw)
            val valorBrutoStr = cols[2]
            val saludStr = cols[3]
            val pensionStr = cols[4]
            val retencionStr = cols[5]

            if (tipoRentaNorm == null) {
                errores.add(ErrorFila(lineaNum, "tipo_renta inválido: '$tipoRentaRaw'"))
                continue
            }

            val valorBruto = parseMonto(valorBrutoStr)
            if (valorBruto == null || valorBruto <= 0) {
                errores.add(ErrorFila(lineaNum, "valor_bruto inválido o no positivo: '$valorBrutoStr'"))
                continue
            }

            val salud = if (saludStr.isEmpty()) 0L else parseMonto(saludStr)
            if (salud == null || salud < 0) {
                errores.add(ErrorFila(lineaNum, "aporte_salud inválido: '$saludStr'"))
                continue
            }

            val pension = if (pensionStr.isEmpty()) 0L else parseMonto(pensionStr)
            if (pension == null || pension < 0) {
                errores.add(ErrorFila(lineaNum, "aporte_pension inválido: '$pensionStr'"))
                continue
            }

            val retencion = if (retencionStr.isEmpty()) 0L else parseMonto(retencionStr)
            if (retencion == null || retencion < 0) {
                errores.add(ErrorFila(lineaNum, "retencion inválida: '$retencionStr'"))
                continue
            }

            filasValidas++
            when (tipoRentaNorm) {
                "salario" -> salario += valorBruto
                "honorarios" -> honorarios += valorBruto
                "arriendos" -> arriendos += valorBruto
                "intereses" -> intereses += valorBruto
            }
            aportes += (salud + pension)
            retenciones += retencion
        }

        return ResultadoImportacion(
            filas = filasValidas,
            salario = salario,
            honorarios = honorarios,
            arriendos = arriendos,
            intereses = intereses,
            aportes = aportes,
            retenciones = retenciones,
            errores = errores
        )
    }

    private fun normalizarTipoRenta(t: String): String? {
        val limpio = t.trim().lowercase()
            .replace('á', 'a')
            .replace('é', 'e')
            .replace('í', 'i')
            .replace('ó', 'o')
            .replace('ú', 'u')
        return when (limpio) {
            "salario" -> "salario"
            "honorarios" -> "honorarios"
            "arriendos" -> "arriendos"
            "intereses" -> "intereses"
            else -> null
        }
    }

    private fun parseMonto(s: String): Long? {
        val limpio = s.trim().removePrefix("$").trim()
        if (limpio.isEmpty()) return 0L
        if (limpio.contains(',') || limpio.contains(' ')) return null

        val sinPuntos = limpio.replace(".", "")
        if (!sinPuntos.all { it.isDigit() }) return null

        if (limpio.contains('.')) {
            val parts = limpio.split('.')
            if (parts[0].isEmpty() || parts[0].length > 3) return null
            for (i in 1 until parts.size) {
                if (parts[i].length != 3 || !parts[i].all { it.isDigit() }) return null
            }
        }

        return sinPuntos.toLongOrNull()
    }
}

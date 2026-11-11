package com.mirenta.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.mirenta.domain.SeccionDoc
import java.io.OutputStream

object PdfGenerador {
    private fun dibujarTextoMultilinea(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        paint: Paint,
        maxWidth: Float,
        lineHeight: Float
    ): Float {
        val words = text.split(" ")
        var currentLine = ""
        var currentY = y
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) > maxWidth) {
                canvas.drawText(currentLine, x, currentY, paint)
                currentY += lineHeight
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine, x, currentY, paint)
            currentY += lineHeight
        }
        return currentY
    }

    fun generarPdf(secciones: List<SeccionDoc>, outputStream: OutputStream) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points

        val paintSection = Paint().apply {
            isAntiAlias = true
            textSize = 12f
            isFakeBoldText = true
            color = Color.parseColor("#10B981")
        }
        val paintLabel = Paint().apply {
            isAntiAlias = true
            textSize = 10f
            color = Color.parseColor("#4B5563")
        }
        val paintValue = Paint().apply {
            isAntiAlias = true
            textSize = 10f
            isFakeBoldText = true
            color = Color.parseColor("#1F2937")
        }

        val margin = 40f
        val pageWidth = 595f
        val pageHeight = 842f
        val contentWidth = pageWidth - (margin * 2)

        var page = document.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        var y = margin + 10f
        var pageNumber = 1

        fun nuevaPagina() {
            document.finishPage(page)
            pageNumber++
            val nextInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            page = document.startPage(nextInfo)
            canvas = page.canvas
            y = margin + 10f
        }

        for (seccion in secciones) {
            if (y > pageHeight - margin - 50f) {
                nuevaPagina()
            }
            canvas.drawText(seccion.titulo, margin, y, paintSection)
            y += 18f

            for (linea in seccion.lineas) {
                if (y > pageHeight - margin - 30f) {
                    nuevaPagina()
                }
                canvas.drawText(linea.etiqueta, margin + 10f, y, paintLabel)
                val valueX = margin + 180f
                val valueWidth = contentWidth - 180f
                y = dibujarTextoMultilinea(canvas, linea.valor, valueX, y, paintValue, valueWidth, 14f)
                y += 4f
            }
            y += 10f
        }

        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
    }
}

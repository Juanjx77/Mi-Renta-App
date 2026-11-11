package com.mirenta.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirenta.domain.ExogenaImportador
import com.mirenta.domain.ResultadoImportacion
import com.mirenta.navigation.Rutas
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.Etiqueta
import com.mirenta.ui.components.Fila
import com.mirenta.ui.components.FilasCard
import com.mirenta.ui.components.LocalShowMessage
import com.mirenta.ui.components.Pill
import com.mirenta.ui.components.PillTipo
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.components.Tarjeta
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC
import com.mirenta.util.fm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun nombreDeArchivo(context: Context, uri: Uri): String =
    context.contentResolver.query(uri, null, null, null, null)?.use { c ->
        val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (i >= 0 && c.moveToFirst()) c.getString(i) else null
    } ?: "archivo_exogena.csv"

private fun Modifier.bordeDiscontinuo(color: Color, radio: Dp = 18.dp) = drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(radio.toPx()),
        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f))),
    )
}

@Composable
fun ExogenaScreen(vm: RentaViewModel, onNav: (String) -> Unit) {
    val cargada = vm.exogena
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mostrar = LocalShowMessage.current

    var resultadoImportacion by remember { mutableStateOf<ResultadoImportacion?>(null) }
    var nombreArchivoActual by remember { mutableStateOf<String?>(null) }
    var errorArchivo by remember { mutableStateOf<String?>(null) }

    val selector = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val size = context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
                    if (size > 1024 * 1024) {
                        errorArchivo = "El archivo supera el tamaño máximo permitido de 1 MB."
                        resultadoImportacion = null
                        return@launch
                    }
                    val texto = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.bufferedReader(Charsets.UTF_8).readText()
                    } ?: ""
                    val nombre = nombreDeArchivo(context, uri)
                    val resultado = ExogenaImportador.leer(texto)
                    nombreArchivoActual = nombre
                    resultadoImportacion = resultado
                    errorArchivo = null
                } catch (e: Exception) {
                    errorArchivo = "Error al leer el archivo: ${e.localizedMessage}"
                    resultadoImportacion = null
                }
            }
        }
    }

    val tipos = arrayOf(
        "text/csv",
        "text/comma-separated-values",
        "text/plain",
        "application/vnd.ms-excel",
        "*/*"
    )

    ScreenScaffold(
        titulo = "Información exógena",
        sub = "Reporte DIAN · año gravable 2025",
        footer = {
            if (resultadoImportacion != null && resultadoImportacion!!.errores.isEmpty()) {
                BotonPrimario("Aplicar a mi declaración", Icons.Outlined.Check) {
                    vm.aplicarExogena(nombreArchivoActual ?: "exogena.csv", resultadoImportacion!!)
                    mostrar("Información de exógena aplicada a la declaración")
                    onNav(Rutas.Inicio)
                }
            } else if (cargada != null) {
                BotonPrimario("Probar en el simulador", Icons.Outlined.Calculate) { onNav(Rutas.Simulador) }
            } else {
                BotonContorno("Entender la exógena") { onNav(Rutas.Aprende) }
            }
        },
    ) {
        Aviso(
            AvisoTipo.Edu,
            negrita = "¿Qué es?",
            texto = " Es el reporte que la DIAN arma con lo que terceros (empleadores, bancos, notarías, comercios) le informaron sobre ti. Incluye tus ingresos, pero también retenciones, saldos, consumos con tarjeta y compras.",
        )

        Aviso(
            AvisoTipo.Info,
            texto = "Esta plantilla es de Mi Renta, no es el formato oficial de la DIAN.",
        )

        if (errorArchivo != null) {
            Aviso(AvisoTipo.Alerta, texto = errorArchivo!!)
        }

        // Subir archivo / Elegir archivo (.csv)
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(18.dp))
                .bordeDiscontinuo(Color(0xFF8AA79A))
                .clickable { selector.launch(tipos) }
                .padding(horizontal = 14.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Outlined.UploadFile, null, tint = RC.Green, modifier = Modifier.size(24.dp))
            Text(
                "Elegir archivo (.csv)",
                color = RC.Green,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                "Plantilla CSV de exógena de Mi Renta",
                color = RC.Label,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }

        BotonContorno("Usar datos de ejemplo (cifras ilustrativas)") {
            vm.cargarExogena("exogena_2025_ejemplo.csv")
            val ejemploCsv = "pagador,tipo_renta,valor_bruto,aporte_salud,aporte_pension,retencion\nEmpresa Ejemplo S.A.S.,salario,96000000,3840000,3840000,4200000\nCliente Ficticio Ltda.,honorarios,24000000,0,0,2400000\nArrendatario Ficticio,arriendos,12000000,,,\nBanco Ficticio,intereses,600000,,,"
            val r = ExogenaImportador.leer(ejemploCsv)
            vm.aplicarExogena("exogena_2025_ejemplo.csv", r)
            mostrar("Cifras de ejemplo aplicadas a la declaración")
            onNav(Rutas.Inicio)
        }

        if (resultadoImportacion != null) {
            val r = resultadoImportacion!!
            Tarjeta {
                Etiqueta("REVISIÓN DE EXÓGENA")
                Text("Archivo: ${nombreArchivoActual ?: "Desconocido"}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Filas leídas: ${r.filas}", fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                FilasCard(
                    listOf(
                        Fila("Salario", fm(r.salario.toDouble())),
                        Fila("Honorarios", fm(r.honorarios.toDouble())),
                        Fila("Arriendos", fm(r.arriendos.toDouble())),
                        Fila("Intereses", fm(r.intereses.toDouble())),
                        Fila("Aportes", fm(r.aportes.toDouble())),
                        Fila("Retenciones", fm(r.retenciones.toDouble())),
                    )
                )
                if (r.errores.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Errores encontrados (${r.errores.size}):", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    val mostrados = r.errores.take(10)
                    mostrados.forEach { err ->
                        Text("• Línea ${err.linea}: ${err.motivo}", color = Color(0xFFEF4444), fontSize = 12.sp)
                    }
                    if (r.errores.size > 10) {
                        Text("y ${r.errores.size - 10} más", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(Modifier.height(8.dp))
                    Text("¡Archivo válido sin errores!", color = RC.Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (cargada != null && resultadoImportacion == null) {
            Tarjeta {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 11.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(34.dp).background(Color(0xFFE8F4EC), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.FactCheck, null, tint = RC.Green, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(cargada, fontSize = 14.sp)
                            Text("Cifras de ejemplo aplicadas", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Pill("Cargado", PillTipo.Ok)
                }
            }
            BotonContorno("Quitar exógena de ejemplo") { vm.quitarExogena() }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun ExogenaVaciaPreview() = MiRentaTheme { ExogenaScreen(RentaViewModel()) {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun ExogenaCargadaPreview() = MiRentaTheme {
    ExogenaScreen(RentaViewModel().apply { cargarExogena("exogena_2025_ejemplo.xlsx") }) {}
}

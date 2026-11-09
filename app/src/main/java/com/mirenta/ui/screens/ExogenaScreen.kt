package com.mirenta.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mirenta.navigation.Rutas
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.Fila
import com.mirenta.ui.components.FilasCard
import com.mirenta.ui.components.Pill
import com.mirenta.ui.components.PillTipo
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.components.Tarjeta
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC

private fun nombreDeArchivo(context: Context, uri: Uri): String =
    context.contentResolver.query(uri, null, null, null, null)?.use { c ->
        val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (i >= 0 && c.moveToFirst()) c.getString(i) else null
    } ?: "archivo_exogena"

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
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.cargarExogena(nombreDeArchivo(context, uri))
    }
    val tipos = arrayOf(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-excel",
        "text/csv",
        "application/pdf",
    )

    ScreenScaffold(
        titulo = "Información exógena",
        sub = "Reporte DIAN · año gravable 2025",
        footer = {
            if (cargada != null) {
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
        if (cargada == null) {
            // Subir archivo
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
                    "Sube tu archivo de exógena",
                    color = RC.Green,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "Excel, CSV o PDF descargado de la DIAN",
                    color = RC.Label,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
            BotonContorno("Usar archivo de ejemplo") { vm.cargarExogena("exogena_2025_ejemplo.xlsx") }
            Tarjeta {
                Text(
                    "CÓMO DESCARGARLO",
                    color = RC.Label,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 2.dp),
                )
                listOf(
                    "Entra al portal de la DIAN con tu usuario.",
                    "Busca “Consulta de información reportada por terceros”.",
                    "Elige el año gravable 2025 y descarga el reporte.",
                    "Súbelo aquí para ver tus ingresos y retenciones.",
                ).forEachIndexed { i, paso ->
                    Text("${i + 1}. $paso", fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            Aviso(
                AvisoTipo.Alerta,
                texto = "Es un reporte orientativo: solo muestra lo que otros reportaron. Si algo no cuadra, pídele al tercero que lo corrija ante la DIAN.",
            )
        } else {
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
                            Text("Lectura simulada · cifras de ejemplo", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Pill("Cargado", PillTipo.Ok)
                }
            }
            Aviso(AvisoTipo.Info, negrita = "Ingresos reportados por terceros: $ 132.600.000")
            FilasCard(
                encabezado = "LO QUE REPORTARON TERCEROS",
                filas = listOf(
                    Fila("Salarios y pagos laborales", "$ 96.000.000", "[Pagador]", Icons.Outlined.Work),
                    Fila("Honorarios", "$ 24.000.000", "[Cliente]", Icons.Outlined.Edit),
                    Fila("Arrendamientos", "$ 12.000.000", "[Inmueble]", Icons.Outlined.Home),
                    Fila("Intereses bancarios", "$ 600.000", "[Banco]", Icons.Outlined.Paid),
                    Fila("Retenciones en la fuente", "$ 6.600.000", "Descontado por pagadores", Icons.Outlined.Receipt),
                    Fila("Saldo en cuentas", "$ 8.000.000", "[Banco]", Icons.Outlined.AccountBalance),
                    Fila("Consumos con tarjeta", "$ 0", "No cuenta para el tope", Icons.Outlined.CreditCard),
                ),
            )
            Aviso(
                AvisoTipo.Alerta,
                texto = "Compara con tus soportes: la exógena puede estar incompleta. La responsabilidad de lo declarado es tuya.",
            )
            BotonContorno("Reemplazar archivo") { vm.quitarExogena() }
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

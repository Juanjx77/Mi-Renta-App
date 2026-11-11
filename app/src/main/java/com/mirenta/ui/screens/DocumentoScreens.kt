package com.mirenta.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirenta.domain.DocumentoDeclaracion
import com.mirenta.domain.PerfilDatos
import com.mirenta.domain.Severidad
import com.mirenta.domain.ValidacionesNegocio
import com.mirenta.navigation.Rutas
import com.mirenta.ui.PerfilViewModel
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.LocalShowMessage
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC
import com.mirenta.util.PdfGenerador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VistaScreen(vm: RentaViewModel, perfil: PerfilDatos, onBack: () -> Unit, onNav: (String) -> Unit) {
    ScreenScaffold(
        titulo = "Vista previa del documento",
        sub = "Generado con tus datos de la declaración",
        onBack = onBack,
        footer = {
            BotonPrimario("Descargar PDF", Icons.Outlined.Download) { onNav(Rutas.Pdf) }
        },
    ) {
        DocumentoCard(vm, perfil)
    }
}

@Composable
fun PdfScreen(vm: RentaViewModel, perfil: PerfilDatos, onBack: () -> Unit, onInicio: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mostrar = LocalShowMessage.current
    val v = vm.declaracion
    val o = vm.resultadoDeclaracion
    val hallazgos = remember(v) { ValidacionesNegocio.validar(v) }
    val tieneErrores = hallazgos.any { it.severidad == Severidad.ERROR }
    val fecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-CO")).format(Date()) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val secciones = DocumentoDeclaracion.generar(v, o, hallazgos, perfil, fecha)
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        PdfGenerador.generarPdf(secciones, outputStream)
                    }
                    scope.launch(Dispatchers.Main) {
                        mostrar("PDF guardado exitosamente")
                    }
                } catch (e: Exception) {
                    scope.launch(Dispatchers.Main) {
                        mostrar("Error al generar el PDF: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    ScreenScaffold(
        titulo = "PDF generado",
        sub = "Declaración de renta · Borrador 2025",
        onBack = onBack,
        footer = {
            if (tieneErrores) {
                Aviso(AvisoTipo.Alerta, texto = "No se puede generar el PDF porque hay errores de validación sin corregir.")
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario(
                "Guardar como PDF",
                Icons.Outlined.Download,
                onClick = {
                    if (!tieneErrores) {
                        launcher.launch("MiRenta_borrador_2025.pdf")
                    }
                }
            )
            BotonContorno("Volver al inicio", Icons.Outlined.Home, onClick = onInicio)
        },
    ) {
        DocumentoCard(vm, perfil)
    }
}

@Composable
fun DocumentoCard(vm: RentaViewModel, perfil: PerfilDatos?) {
    val v = vm.declaracion
    val o = vm.resultadoDeclaracion
    val hallazgos = remember(v) { ValidacionesNegocio.validar(v) }
    val fecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-CO")).format(Date()) }
    val secciones = remember(v, o, hallazgos, perfil, fecha) {
        DocumentoDeclaracion.generar(v, o, hallazgos, perfil, fecha)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFCDD5DF))
            .padding(14.dp),
    ) {
        secciones.forEach { seccion ->
            Text(
                seccion.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RC.Green,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            seccion.lineas.forEach { linea ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(linea.etiqueta, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f), color = RC.Label)
                    Text(linea.valor, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            HorizontalDivider(color = RC.Border, modifier = Modifier.padding(vertical = 6.dp))
        }
        val exogenaTexto = if (vm.exogena != null) "${vm.exogena} (plantilla de ejemplo cargada)" else "No se ha cargado plantilla de exógena (es una plantilla de ejemplo de MiRenta)."
        Text("Exógena: $exogenaTexto", fontSize = 12.sp, color = RC.Label, modifier = Modifier.padding(top = 4.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun VistaPreview() = MiRentaTheme {
    VistaScreen(RentaViewModel().apply { cargarExogena("exogena_2025_ejemplo.xlsx") }, PerfilDatos(nombre = "Juan Pérez"), {}, {})
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun PdfPreview() = MiRentaTheme {
    PdfScreen(RentaViewModel().apply { cargarEjemplo() }, PerfilDatos(nombre = "Juan Pérez"), {}, {})
}

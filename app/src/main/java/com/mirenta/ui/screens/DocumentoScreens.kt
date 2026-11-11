package com.mirenta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirenta.navigation.Rutas
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.LocalShowMessage
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC
import com.mirenta.util.dec
import com.mirenta.util.fm
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun VistaScreen(vm: RentaViewModel, onBack: () -> Unit, onNav: (String) -> Unit) {
    val mostrar = LocalShowMessage.current
    ScreenScaffold(
        titulo = "Vista previa del documento",
        sub = "Generado con tus datos del simulador",
        onBack = onBack,
        footer = {
            BotonPrimario("Descargar PDF", Icons.Outlined.Download) { onNav(Rutas.Pdf) }
            BotonContorno("Compartir", Icons.Outlined.Share) {
                mostrar("Hoja de compartir del sistema (sin mockup)")
            }
        },
    ) {
        DocumentoCard(vm, esPdf = false)
    }
}

@Composable
fun PdfScreen(vm: RentaViewModel, onBack: () -> Unit, onInicio: () -> Unit) {
    val mostrar = LocalShowMessage.current
    ScreenScaffold(
        titulo = "PDF generado",
        sub = "Declaración de renta · 1 página",
        onBack = onBack,
        footer = {
            BotonPrimario("Guardar como PDF", Icons.Outlined.Download) {
                // TODO: generar el archivo con android.graphics.pdf.PdfDocument o PrintManager
                mostrar("Pendiente: exportar a PDF")
            }
            BotonContorno("Volver al inicio", Icons.Outlined.Home, onClick = onInicio)
        },
    ) {
        DocumentoCard(vm, esPdf = true)
    }
}

@Composable
fun DocumentoCard(vm: RentaViewModel, esPdf: Boolean) {
    val v = vm.declaracion
    val o = vm.resultadoDeclaracion
    val fecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-CO")).format(Date()) }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFCDD5DF))
            .padding(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mi Renta", color = RC.Green, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            if (!esPdf) {
                Box(Modifier.border(1.dp, Color(0xFF8A99AD)).padding(horizontal = 6.dp)) {
                    Text("BORRADOR", fontSize = 11.sp)
                }
            }
        }
        Text(
            "Simulación de declaración de renta · Personas naturales",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
        )
        Text("Formulario 210 · AG 2025 · Generado el $fecha", fontSize = 12.sp, color = RC.Label)
        Sep()
        Sec("1. Contribuyente")
        Txt("[Nombre y apellidos] · [Documento]")
        Sep()
        Sec("2. Datos que ingresaste")
        Dr("Salario", fm(v.salario.toDouble()))
        Dr("Honorarios", fm(v.honorarios.toDouble()))
        Dr("Arriendos", fm(v.arriendos.toDouble()))
        Dr("Intereses financieros", fm(v.intereses.toDouble()))
        Dr("Aportes a salud y pensión", fm(v.aportes.toDouble()))
        Dr("Intereses de vivienda", fm(v.vivienda.toDouble()))
        Dr("Dependientes", v.dependientes.toString())
        Dr("Salud prepagada", fm(v.prepagada.toDouble()))
        Dr("AFC / pensión voluntaria", fm(v.afc.toDouble()))
        Dr("Retenciones", fm(v.retenciones.toDouble()))
        Dr("Consumos y compras", fm(v.consumos.toDouble()))
        Sep()
        Sec("3. Depuración")
        Dr("Ingresos laborales", fm(o.laborales))
        Dr("(−) No constitutivos de renta", fm(o.noConstitutivos))
        Dr("(−) Deducciones", fm(o.deducciones))
        Dr("(−) Renta exenta 25%", fm(o.exenta))
        Dr("Renta líquida de trabajo", fm(o.rentaLiquidaTrabajo))
        Dr("(+) Rentas de capital", fm(o.capital))
        Dr("Renta gravable", fm(o.rentaGravable) + " · " + dec(o.uvt, 2) + " UVT")
        Sep()
        Sec("4. Liquidación")
        Dr("Impuesto a cargo", fm(o.impuesto))
        Dr("(−) Retenciones", fm(v.retenciones.toDouble()))
        Dr(if (o.saldo < 0) "Saldo a favor" else "Saldo a pagar", fm(abs(o.saldo)))
        Dr("Tarifa efectiva", dec(o.tarifaEfectiva * 100, 1) + "%")
        Sep()
        Sec("5. Patrimonio")
        Dr("Bruto", fm(v.patrimonio.toDouble()))
        Dr("Deudas", fm(v.deudas.toDouble()))
        Dr("Líquido", fm(o.patrimonioLiquido))
        Sep()
        Sec("6. ¿Debes declarar?")
        Txt(if (o.motivos.isNotEmpty()) "Sí, por: " + o.motivos.joinToString("; ") + "." else "Con estos datos no superas ningún tope.")
        Sep()
        Sec("7. Información exógena")
        Txt(if (vm.exogena != null) "${vm.exogena} (cargada)" else "No cargada. Súbela para contrastar tus cifras.")
        Sep()
        Text(
            "UVT 2025: $ 49.799. Simulación con fórmulas simplificadas. No reemplaza la asesoría de un contador ni la presentación en el portal de la DIAN.",
            fontSize = 12.sp,
            color = RC.Label,
        )
    }
}

@Composable
private fun Sep() = HorizontalDivider(color = RC.Border, modifier = Modifier.padding(vertical = 8.dp))

@Composable
private fun Sec(t: String) = Text(t, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 18.sp)

@Composable
private fun Txt(t: String) = Text(t, fontSize = 12.sp, lineHeight = 18.sp)

@Composable
private fun Dr(a: String, b: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(a, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
        Text(b, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun VistaPreview() = MiRentaTheme {
    VistaScreen(RentaViewModel().apply { cargarExogena("exogena_2025_ejemplo.xlsx") }, {}, {})
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun PdfPreview() = MiRentaTheme {
    PdfScreen(RentaViewModel().apply { cargarEjemplo() }, {}, {})
}

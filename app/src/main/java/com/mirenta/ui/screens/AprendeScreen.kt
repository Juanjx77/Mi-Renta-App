package com.mirenta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC

private val Lecciones = listOf(
    "¿Quién debe declarar?" to "Si en 2025 cumpliste alguno: ingresos brutos ≥ 1.400 UVT (\$ 69,7 M), patrimonio bruto ≥ 4.500 UVT (\$ 224,1 M), o consumos con tarjeta, compras o consignaciones ≥ 1.400 UVT. Verifica los topes vigentes en la DIAN.",
    "¿Qué es la UVT?" to "Unidad de Valor Tributario. Es la medida con la que la DIAN fija topes y tarifas. Para el año gravable 2025 vale \$ 49.799.",
    "¿Qué es la información exógena?" to "Reporte de la DIAN con los datos que terceros (empleadores, bancos, notarías, comercios) informaron sobre ti: salarios, retenciones, intereses, consumos, compras y operaciones inmobiliarias. Es orientativo y no reemplaza tus soportes. Si hay errores, el tercero debe corregirlo.",
    "Cédulas" to "Tus ingresos se agrupan por tipo. En la cédula general van rentas de trabajo y de capital; los dividendos y las pensiones tienen tratamiento propio.",
    "Ingresos no constitutivos de renta" to "Valores que la ley no considera renta, como los aportes obligatorios a salud y pensión. Se restan completos.",
    "Rentas exentas" to "Parte de tus ingresos laborales que no paga impuesto: 25% de lo que queda tras restar lo anterior, con tope de 790 UVT al año.",
    "Deducciones" to "Gastos que restan base: intereses de vivienda, dependientes, salud prepagada, AFC y pensión voluntaria, cada uno con su tope.",
    "Límite del 40% o 1.340 UVT" to "Las deducciones y rentas exentas no pueden superar el 40% de tu ingreso neto ni 1.340 UVT: aplica el menor.",
    "Retención vs. impuesto" to "La retención es un anticipo que te descuentan durante el año. Si es mayor al impuesto, tienes saldo a favor; si es menor, pagas la diferencia.",
    "Tarifa marginal vs. efectiva" to "La marginal es la del último tramo de tu renta. La efectiva es el impuesto dividido entre tus ingresos totales; siempre es menor.",
    "Patrimonio bruto y líquido" to "Bruto: todo lo que tienes. Líquido: bruto menos deudas. El bruto es el que cuenta para el tope de declarar.",
    "Calendario 2026" to "Los vencimientos del año gravable 2025 van del 12 de agosto al 26 de octubre de 2026, según los dos últimos dígitos de tu cédula o NIT.",
)

@Composable
fun AprendeScreen() {
    ScreenScaffold(titulo = "Aprende", sub = "La renta en palabras simples") {
        Lecciones.forEach { (titulo, texto) -> Leccion(titulo, texto) }
        Text(
            "Contenido educativo con cifras del año gravable 2025. No reemplaza la asesoría de un contador.",
            fontSize = 12.sp,
            color = RC.Label,
        )
    }
}

@Composable
private fun Leccion(titulo: String, texto: String) {
    var abierta by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, RC.Border, RoundedCornerShape(14.dp))
            .clickable { abierta = !abierta }
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(if (abierta) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null)
        }
        if (abierta) {
            Text(
                texto,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFF3D4D46),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun AprendePreview() = MiRentaTheme { AprendeScreen() }

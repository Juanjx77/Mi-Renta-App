package com.mirenta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirenta.domain.Severidad
import com.mirenta.domain.ValidacionesNegocio
import com.mirenta.navigation.Rutas
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.Casilla
import com.mirenta.ui.components.Etiqueta
import com.mirenta.ui.components.Fila
import com.mirenta.ui.components.FilasCard
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.components.Tarjeta
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC
import com.mirenta.util.fm
import kotlin.math.abs

@Composable
fun FormularioScreen(onNav: (String) -> Unit) {
    ScreenScaffold(
        titulo = "Formulario 210",
        sub = "AG 2025 · cifras de ejemplo",
        footer = {
            BotonPrimario("Verificar y exportar", Icons.Outlined.FactCheck) { onNav(Rutas.Verificar) }
        },
    ) {
        Aviso(AvisoTipo.Alerta, texto = "Revisa cada valor antes de presentar en el portal de la DIAN.")
        FilasCard(
            encabezado = "CÉDULA GENERAL · RENTAS DE TRABAJO",
            onNav = onNav,
            filas = listOf(
                Fila("Ingresos brutos", "$ 120.000.000"),
                Fila("(−) Ingresos no constitutivos de renta", "$ 7.680.000"),
                Fila("(−) Rentas exentas y deducciones", "$ 30.330.000"),
                Fila("Renta líquida de trabajo ›", "$ 81.990.000", negrita = true, destino = Rutas.Detalle),
            ),
        )
        FilasCard(
            encabezado = "CÉDULA GENERAL · RENTAS DE CAPITAL",
            filas = listOf(
                Fila("Ingresos brutos", "$ 12.600.000"),
                Fila("(−) Costos y deducciones", "$ 0"),
                Fila("Renta líquida de capital", "$ 12.600.000", negrita = true),
            ),
        )
        FilasCard(
            encabezado = "LIQUIDACIÓN DEL IMPUESTO",
            filas = listOf(
                Fila("Renta líquida gravable", "$ 94.590.000", "1.899,44 UVT"),
                Fila("Impuesto a cargo", "$ 8.558.000"),
                Fila("(−) Retenciones", "$ 6.600.000"),
                Fila("(−) Anticipo del año anterior", "$ 0"),
                Fila("Saldo a pagar", "$ 1.958.000", negrita = true),
            ),
        )
        FilasCard(
            encabezado = "PATRIMONIO AL 31 DE DICIEMBRE",
            onNav = onNav,
            filas = listOf(Fila("Patrimonio líquido", "$ 133.000.000", destino = Rutas.Patrimonio)),
        )
        Text("Toca cualquier valor para ver su fórmula y soportes.", fontSize = 12.sp, color = RC.Label)
    }
}

@Composable
fun DetalleScreen(onBack: () -> Unit) {
    ScreenScaffold(
        titulo = "Renta líquida · Trabajo",
        sub = "Formulario 210 · cifras de ejemplo",
        onBack = onBack,
    ) {
        Tarjeta {
            Etiqueta("VALOR CALCULADO")
            Text(
                "$ 81.990.000",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )
        }
        Aviso(
            AvisoTipo.Info,
            negrita = "Fórmula",
            texto = "\nIngresos brutos − ingresos no constitutivos de renta − rentas exentas y deducciones\n120.000.000 − 7.680.000 − 30.330.000",
        )
        FilasCard(
            encabezado = "DATOS DE ORIGEN",
            filas = listOf(
                Fila("Ingresos brutos", "$ 120.000.000", "Salario + honorarios"),
                Fila("No constitutivos", "$ 7.680.000", "Aportes a salud y pensión"),
                Fila("Exentas y deducciones", "$ 30.330.000", "Dentro del límite del 40%"),
            ),
        )
        FilasCard(
            encabezado = "PARÁMETROS USADOS",
            filas = listOf(
                Fila("UVT año gravable 2025", "$ 49.799"),
                Fila("Límite conjunto", "1.340 UVT"),
            ),
        )
        Aviso(
            AvisoTipo.Edu,
            negrita = "Aprende:",
            texto = " los ingresos no constitutivos de renta, como aportes a salud y pensión, no se consideran renta.",
        )
    }
}

@Composable
fun VerificarScreen(vm: RentaViewModel, onBack: () -> Unit, onNav: (String) -> Unit) {
    val o = vm.resultado
    val hallazgos = remember(vm.entradas) { ValidacionesNegocio.validar(vm.entradas) }
    val tieneErrores = hallazgos.any { it.severidad == Severidad.ERROR }
    val tieneAdvertencias = hallazgos.any { it.severidad == Severidad.ADVERTENCIA }

    val (colorSemaforo, textoSemaforo) = when {
        tieneErrores -> Color(0xFFEF4444) to "Se encontraron errores que impiden continuar"
        tieneAdvertencias -> Color(0xFFF59E0B) to "Hay advertencias que requieren revisión"
        else -> Color(0xFF10B981) to "Validación exitosa · Todo en orden"
    }

    ScreenScaffold(
        titulo = "Verificar y exportar",
        onBack = onBack,
        footer = {
            BotonPrimario(
                "Generar documento",
                Icons.Outlined.Description,
                onClick = { if (!tieneErrores) onNav(Rutas.Vista) },
            )
            if (tieneErrores) {
                Text(
                    "Debes corregir los errores antes de generar el documento.",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            BotonContorno("Volver al formulario", Icons.AutoMirrored.Outlined.ArrowBack, onClick = onBack)
        },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorSemaforo.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .border(1.dp, colorSemaforo, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(colorSemaforo),
            )
            Text(textoSemaforo, color = Color(0xFF1F2937), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (hallazgos.isNotEmpty()) {
            Tarjeta {
                Etiqueta("HALLAZGOS DE VALIDACIÓN")
                hallazgos.forEachIndexed { idx, h ->
                    if (idx > 0) HorizontalDivider(color = RC.Divider, modifier = Modifier.padding(vertical = 6.dp))
                    Column(Modifier.padding(vertical = 4.dp)) {
                        val colorSev = when (h.severidad) {
                            Severidad.ERROR -> Color(0xFFEF4444)
                            Severidad.ADVERTENCIA -> Color(0xFFD97706)
                            Severidad.INFO -> RC.Green
                        }
                        val tituloSev = when (h.severidad) {
                            Severidad.ERROR -> "ERROR"
                            Severidad.ADVERTENCIA -> "ADVERTENCIA"
                            Severidad.INFO -> "INFO"
                        }
                        Text("$tituloSev [${h.codigo}]", color = colorSev, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(h.mensaje, fontSize = 13.sp, color = Color(0xFF374151), modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }

        FilasCard(
            encabezado = "RESUMEN · CIFRAS DE EJEMPLO",
            filas = listOf(
                Fila("Renta líquida gravable", fm(o.rentaGravable)),
                Fila("Impuesto a cargo", fm(o.impuesto)),
                Fila("Retenciones", fm(vm.entradas.retenciones.toDouble())),
                Fila(if (o.saldo < 0) "Saldo a favor" else "Saldo a pagar", fm(abs(o.saldo)), negrita = true),
            ),
        )
        Tarjeta {
            Casilla("Revisé mi información exógena y soportes")
            Casilla("Revisé deducciones y rentas exentas")
            Casilla("Revisé retenciones y patrimonio")
            Casilla("Entiendo que debo presentar y pagar yo mismo en el portal de la DIAN")
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1100)
@Composable
private fun FormularioPreview() = MiRentaTheme { FormularioScreen {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun DetallePreview() = MiRentaTheme { DetalleScreen {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun VerificarPreview() = MiRentaTheme {
    VerificarScreen(RentaViewModel().apply { cargarEjemplo() }, {}, {})
}

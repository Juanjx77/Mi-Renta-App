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
import com.mirenta.domain.Reglas2025
import com.mirenta.domain.SeccionesDeclaracion
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
import java.util.Locale
import kotlin.math.abs

@Composable
fun FormularioScreen(vm: RentaViewModel, onNav: (String) -> Unit) {
    val v = vm.declaracion
    val o = vm.resultadoDeclaracion
    val s = v.seccionesGuardadas

    val tieneIngresos = s.contains(SeccionesDeclaracion.Ingresos)
    val tieneDeducciones = s.contains(SeccionesDeclaracion.Deducciones)
    val tieneRetenciones = s.contains(SeccionesDeclaracion.Retenciones)
    val tienePatrimonio = s.contains(SeccionesDeclaracion.Patrimonio)

    ScreenScaffold(
        titulo = "Formulario 210",
        sub = "AG 2025",
        footer = {
            BotonPrimario("Verificar y exportar", Icons.Outlined.FactCheck) { onNav(Rutas.Verificar) }
        },
    ) {
        Aviso(AvisoTipo.Alerta, texto = "Revisa cada valor antes de presentar en el portal de la DIAN.")
        FilasCard(
            encabezado = "CÉDULA GENERAL · RENTAS DE TRABAJO",
            onNav = onNav,
            filas = listOf(
                Fila("Ingresos brutos", if (tieneIngresos) fm(o.laborales) else "Sin datos"),
                Fila("(−) Ingresos no constitutivos de renta", if (tieneIngresos) fm(o.noConstitutivos) else "Sin datos"),
                Fila("(−) Rentas exentas y deducciones", if (tieneDeducciones) fm(o.beneficios) else "Sin datos"),
                Fila("Renta líquida de trabajo ›", if (tieneIngresos || tieneDeducciones) fm(o.rentaLiquidaTrabajo) else "Sin datos", negrita = true, destino = Rutas.Detalle),
            ),
        )
        FilasCard(
            encabezado = "CÉDULA GENERAL · RENTAS DE CAPITAL",
            filas = listOf(
                Fila("Ingresos brutos", if (tieneIngresos) fm(o.capital) else "Sin datos"),
                Fila("(−) Costos y deducciones", "No calculado"),
                Fila("Renta líquida de capital", if (tieneIngresos) fm(o.capital) else "Sin datos", negrita = true),
            ),
        )
        val uvtStr = String.format(Locale("es", "CO"), "%.2f", o.uvt).replace('.', ',') + " UVT"
        FilasCard(
            encabezado = "LIQUIDACIÓN DEL IMPUESTO",
            filas = listOf(
                Fila("Renta líquida gravable", if (tieneIngresos) fm(o.rentaGravable) else "Sin datos", if (tieneIngresos) uvtStr else "Sin datos"),
                Fila("Impuesto a cargo", if (tieneIngresos) fm(o.impuesto) else "Sin datos"),
                Fila("(−) Retenciones", if (tieneRetenciones) fm(v.retenciones.toDouble()) else "Sin datos"),
                Fila("(−) Anticipo del año anterior", "No calculado"),
                Fila(if (o.saldo < 0) "Saldo a favor" else "Saldo a pagar", if (tieneIngresos) fm(abs(o.saldo)) else "Sin datos", negrita = true),
            ),
        )
        FilasCard(
            encabezado = "PATRIMONIO AL 31 DE DICIEMBRE",
            onNav = onNav,
            filas = listOf(Fila("Patrimonio líquido", if (tienePatrimonio) fm(o.patrimonioLiquido) else "Sin datos", destino = Rutas.Patrimonio)),
        )
        Text("Toca cualquier valor para ver su fórmula y soportes.", fontSize = 12.sp, color = RC.Label)
    }
}

@Composable
fun DetalleScreen(vm: RentaViewModel, onBack: () -> Unit) {
    val v = vm.declaracion
    val o = vm.resultadoDeclaracion
    val s = v.seccionesGuardadas

    val tieneIngresos = s.contains(SeccionesDeclaracion.Ingresos)
    val tieneDeducciones = s.contains(SeccionesDeclaracion.Deducciones)

    val valorRentaLiquida = if (tieneIngresos || tieneDeducciones) fm(o.rentaLiquidaTrabajo) else "Sin datos"
    val formulaTexto = "\nIngresos brutos − ingresos no constitutivos de renta − rentas exentas y deducciones\n${if (tieneIngresos) fm(o.laborales) else "0"} − ${if (tieneIngresos) fm(o.noConstitutivos) else "0"} − ${if (tieneDeducciones) fm(o.beneficios) else "0"}"

    val porcentajeLimite = (Reglas2025.porcentajeLimiteConjunto.valor * 100).toInt()

    ScreenScaffold(
        titulo = "Renta líquida · Trabajo",
        sub = "Formulario 210",
        onBack = onBack,
    ) {
        Tarjeta {
            Etiqueta("VALOR CALCULADO")
            Text(
                valorRentaLiquida,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )
        }
        Aviso(
            AvisoTipo.Info,
            negrita = "Fórmula",
            texto = formulaTexto,
        )
        FilasCard(
            encabezado = "DATOS DE ORIGEN",
            filas = listOf(
                Fila("Ingresos brutos", if (tieneIngresos) fm(o.laborales) else "Sin datos", "Salario + honorarios"),
                Fila("No constitutivos", if (tieneIngresos) fm(o.noConstitutivos) else "Sin datos", "Aportes a salud y pensión"),
                Fila("Exentas y deducciones", if (tieneDeducciones) fm(o.beneficios) else "Sin datos", "Dentro del límite del $porcentajeLimite %"),
            ),
        )
        FilasCard(
            encabezado = "PARÁMETROS USADOS",
            filas = listOf(
                Fila("UVT año gravable 2025", fm(Reglas2025.uvt.valor)),
                Fila("Límite conjunto", "${Reglas2025.topeLimiteConjuntoUvt.valor.toInt()} UVT"),
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
    val o = vm.resultadoDeclaracion
    val hallazgos = remember(vm.declaracion) { ValidacionesNegocio.validar(vm.declaracion) }
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
            encabezado = "RESUMEN",
            filas = listOf(
                Fila("Renta líquida gravable", fm(o.rentaGravable)),
                Fila("Impuesto a cargo", fm(o.impuesto)),
                Fila("Retenciones", fm(vm.declaracion.retenciones.toDouble())),
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
private fun FormularioPreview() = MiRentaTheme { FormularioScreen(RentaViewModel().apply { cargarEjemplo() }) {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun DetallePreview() = MiRentaTheme { DetalleScreen(RentaViewModel().apply { cargarEjemplo() }) {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun VerificarPreview() = MiRentaTheme {
    VerificarScreen(RentaViewModel().apply { cargarEjemplo() }, {}, {})
}

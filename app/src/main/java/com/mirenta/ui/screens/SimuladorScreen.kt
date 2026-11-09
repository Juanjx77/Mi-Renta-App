package com.mirenta.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirenta.domain.Campo
import com.mirenta.navigation.Rutas
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.CampoTexto
import com.mirenta.ui.components.Etiqueta
import com.mirenta.ui.components.Fila
import com.mirenta.ui.components.FilasCard
import com.mirenta.ui.components.MilesTransformation
import com.mirenta.ui.components.Pill
import com.mirenta.ui.components.PillTipo
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.components.Tarjeta
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC
import com.mirenta.util.dec
import com.mirenta.util.fm
import com.mirenta.util.miles
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimuladorScreen(vm: RentaViewModel, onNav: (String) -> Unit) {
    val e = vm.entradas
    val resultadosRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(vm.calculado) {
        if (vm.calculado) {
            delay(120)
            resultadosRequester.bringIntoView()
        }
    }

    ScreenScaffold(
        titulo = "Simulador",
        sub = "Juega con tus cifras y mira el impuesto al instante",
    ) {
        if (vm.exogena != null) {
            Aviso(AvisoTipo.Info, texto = "Valores precargados con tu exógena (ejemplo).")
        }

        Campo.entries.forEach { c ->
            Column {
                if (c.seccion.isNotEmpty()) Etiqueta(c.seccion)
                val valor = e[c]
                CampoTexto(
                    etiqueta = c.etiqueta,
                    placeholder = if (c.ejemplo > 0) miles(c.ejemplo) else "0",
                    valor = if (valor > 0) valor.toString() else "",
                    onCambio = { t -> vm.cambiar(c, t.filter { it.isDigit() }.take(12).toLongOrNull() ?: 0L) },
                    teclado = KeyboardType.Number,
                    transformacion = if (c.soloCantidad) VisualTransformation.None else MilesTransformation,
                )
                Text(
                    buildAnnotatedString {
                        append(c.ayuda)
                        if (valor > 0) {
                            append(" · ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(if (c.soloCantidad) valor.toString() else fm(valor.toDouble()))
                            }
                        }
                    },
                    fontSize = 12.sp,
                    color = RC.Label,
                )
            }
        }

        BotonPrimario("Calcular mi renta", Icons.Outlined.Percent) { vm.calcular() }

        if (vm.calculado) {
            val o = vm.resultado
            val aFavor = o.saldo < 0
            Column(
                Modifier.bringIntoViewRequester(resultadosRequester),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFC5E3CF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(
                        if (aFavor) "SALDO A FAVOR" else "SALDO ESTIMADO A PAGAR",
                        fontSize = 11.sp,
                        color = RC.Muted,
                    )
                    Text(fm(abs(o.saldo)), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Impuesto ${fm(o.impuesto)} · tarifa efectiva ${dec(o.tarifaEfectiva * 100, 1)}%",
                        fontSize = 12.sp,
                        color = RC.Label,
                    )
                }

                FilasCard(
                    encabezado = "PASO A PASO",
                    filas = listOf(
                        Fila("Ingresos laborales", fm(o.laborales), "Salario + honorarios"),
                        Fila("(−) No constitutivos de renta", fm(o.noConstitutivos), "Aportes a salud y pensión"),
                        Fila("(−) Deducciones", fm(o.deducciones)),
                        Fila("(−) Renta exenta 25%", fm(o.exenta), "Tope 790 UVT"),
                        Fila("Beneficios aplicados", fm(o.beneficios), "Límite: ${fm(o.limite)} (40% o 1.340 UVT)"),
                        Fila("Renta líquida de trabajo", fm(o.rentaLiquidaTrabajo), negrita = true),
                        Fila("(+) Rentas de capital", fm(o.capital)),
                        Fila("Renta gravable", fm(o.rentaGravable), "${dec(o.uvt, 2)} UVT", negrita = true),
                        Fila("Impuesto a cargo", fm(o.impuesto), negrita = true),
                        Fila("(−) Retenciones", fm(e.retenciones.toDouble())),
                    ),
                )

                Tarjeta {
                    Etiqueta("¿DEBES DECLARAR?")
                    Row(
                        Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (o.motivos.isNotEmpty()) {
                            Pill("Sí", PillTipo.Aviso)
                            Text("Por: " + o.motivos.joinToString("; ") + ".", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        } else {
                            Pill("No", PillTipo.Ok)
                            Text("Con estos datos no superas ningún tope.", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        }
                    }
                    HorizontalDivider(color = RC.Divider)
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Patrimonio líquido", fontSize = 14.sp)
                        Text(fm(o.patrimonioLiquido), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Aviso(
                    AvisoTipo.Edu,
                    negrita = "Tarifa marginal ${(o.tarifaMarginal * 100).roundToInt()}%:",
                    texto = " cada $ 1.000.000 más de renta gravable te costaría ≈ ${fm(o.tarifaMarginal * 1e6)}. Por eso un aporte a AFC puede ahorrarte hasta ese valor, si no pasas los límites.",
                )
                BotonPrimario("Generar documento con mis datos", Icons.Outlined.Description) { onNav(Rutas.Vista) }
            }
        }

        BotonContorno("Restablecer ejemplo") { vm.cargarEjemplo() }
        BotonContorno("Limpiar todo") { vm.limpiar() }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun SimuladorPreview() = MiRentaTheme { SimuladorScreen(RentaViewModel().apply { cargarEjemplo() }) {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun SimuladorCalculadoPreview() = MiRentaTheme {
    SimuladorScreen(RentaViewModel().apply { cargarEjemplo(); calcular() }) {}
}

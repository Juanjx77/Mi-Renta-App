package com.mirenta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mirenta.domain.Entradas
import com.mirenta.navigation.Rutas
import com.mirenta.ui.AuthViewModel
import com.mirenta.ui.PerfilViewModel
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.Fila
import com.mirenta.ui.components.FilasCard
import com.mirenta.ui.components.PillTipo
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC
import com.mirenta.util.fm
import kotlin.math.abs

@Composable
fun InicioScreen(
    vm: RentaViewModel,
    authViewModel: AuthViewModel = viewModel(),
    perfilViewModel: PerfilViewModel = viewModel(),
    onNav: (String) -> Unit,
    onLogout: () -> Unit,
) {
    LaunchedEffect(Unit) {
        perfilViewModel.cargar()
    }

    val perfil = perfilViewModel.perfil
    val primerNombre = perfil.nombre.trim().split(Regex("\\s+")).firstOrNull()?.takeIf { it.isNotBlank() }
    val tituloHola = if (primerNombre != null) "Hola, $primerNombre" else "Hola"

    val v = vm.declaracion
    val o = vm.resultadoDeclaracion
    val vacio = v == Entradas()

    val saldoStr = if (vacio) "$ 0" else fm(abs(o.saldo))
    val ingresosStr = if (vacio) "$ 0" else fm(o.laborales + o.capital)
    val rentaGravStr = if (vacio) "$ 0" else fm(o.rentaGravable)
    val impuestoStr = if (vacio) "$ 0" else fm(o.impuesto)
    val tituloSaldo = if (o.saldo < 0) "SALDO A FAVOR" else "SALDO ESTIMADO A PAGAR"

    val perfilCompleto = perfil.nombre.isNotBlank() && perfil.numeroDocumento.isNotBlank()
    val pillDatos = if (perfilCompleto) "Completo" to PillTipo.Ok else "Pendiente" to PillTipo.Aviso
    val pillIngresos = if (v.salario + v.honorarios + v.arriendos + v.intereses > 0) "Completo" to PillTipo.Ok else "Pendiente" to PillTipo.Aviso
    val tieneDeducciones = v.aportes > 0 || v.vivienda > 0 || v.dependientes > 0 || v.prepagada > 0 || v.afc > 0
    val pillDeducciones = if (tieneDeducciones) "Completo" to PillTipo.Ok else "Sin beneficios" to PillTipo.Neutro

    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoLogout = false
                        authViewModel.cerrarSesion {
                            onLogout()
                        }
                    },
                ) {
                    Text("Sí, cerrar sesión", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    ScreenScaffold(titulo = tituloHola, pre = "Año gravable 2025") {
        // Resumen
        Column(
            Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(22.dp))
                .background(Color.White, RoundedCornerShape(22.dp))
                .padding(16.dp),
        ) {
            Text(tituloSaldo, color = RC.Muted, fontSize = 12.sp)
            Text(saldoStr, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(RC.Divider)) {
                Box(Modifier.fillMaxWidth(if (vacio) 0.0f else 0.64f).fillMaxHeight().clip(CircleShape).background(RC.Mid))
            }
            Spacer(Modifier.height(6.dp))
            Text("Progreso referencial (ejemplo) · 64% completo", fontSize = 14.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Dato("INGRESOS", ingresosStr, Modifier.weight(1f))
            Dato("RENTA GRAV.", rentaGravStr, Modifier.weight(1f))
            Dato("IMPUESTO", impuestoStr, Modifier.weight(1f))
        }
        FilasCard(
            onNav = onNav,
            filas = listOf(
                Fila("Ingresos", icono = Icons.Outlined.Paid, pill = pillIngresos, destino = Rutas.Ingresos),
                Fila("Datos personales", icono = Icons.Outlined.Person, pill = pillDatos, destino = Rutas.Datos),
                Fila("Patrimonio", icono = Icons.Outlined.AccountBalance, pill = "Completo" to PillTipo.Ok, destino = Rutas.Patrimonio),
                Fila(
                    "Información exógena",
                    icono = Icons.Outlined.FactCheck,
                    pill = if (vm.exogena != null) "Cargada" to PillTipo.Ok else "Falta subir" to PillTipo.Aviso,
                    destino = Rutas.Exogena,
                ),
                Fila("Simulador", icono = Icons.Outlined.Percent, pill = "Pruébalo" to PillTipo.Neutro, destino = Rutas.Simulador),
                Fila("Soportes", icono = Icons.Outlined.AttachFile, pill = "1 faltante" to PillTipo.Aviso, destino = Rutas.Soportes),
                Fila("Deducciones", icono = Icons.Outlined.Percent, pill = pillDeducciones, destino = Rutas.Deducciones),
                Fila("Retenciones", icono = Icons.Outlined.Receipt, pill = "Sin iniciar" to PillTipo.Neutro, destino = Rutas.Retenciones),
            ),
        )
        // Siguiente paso
        Row(
            Modifier
                .fillMaxWidth()
                .background(RC.Lima, RoundedCornerShape(18.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Siguiente paso: revisar deducciones",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(RC.Dark)
                    .clickable { onNav(Rutas.Deducciones) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            ) {
                Text("Ir →", color = RC.Lima, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        BotonContorno(
            "Cerrar sesión",
            icono = Icons.AutoMirrored.Outlined.Logout,
            color = Color(0xFF6B7280),
            onClick = { mostrarDialogoLogout = true },
        )
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String, modifier: Modifier) {
    Column(
        modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(etiqueta, fontSize = 11.sp, color = RC.Label)
        Text(valor, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2A1C06))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun InicioPreview() = MiRentaTheme { InicioScreen(RentaViewModel(), onNav = {}, onLogout = {}) }

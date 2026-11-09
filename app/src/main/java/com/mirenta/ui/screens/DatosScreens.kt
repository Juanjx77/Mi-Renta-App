package com.mirenta.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mirenta.domain.PerfilDatos
import com.mirenta.domain.Validadores
import com.mirenta.ui.PerfilViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.CampoTexto
import com.mirenta.ui.components.Etiqueta
import com.mirenta.ui.components.Fila
import com.mirenta.ui.components.FilasCard
import com.mirenta.ui.components.LocalShowMessage
import com.mirenta.ui.components.PillTipo
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.components.Selector
import com.mirenta.ui.components.Tarjeta
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC

@Composable
fun DatosScreen(
    onBack: () -> Unit,
    onGuardar: () -> Unit,
    perfilViewModel: PerfilViewModel = viewModel(),
) {
    val mostrar = LocalShowMessage.current
    val cargando by perfilViewModel.cargando.collectAsState()
    val errorRed by perfilViewModel.errorRed.collectAsState()
    val perfilActual = perfilViewModel.perfil

    var nombre by remember(perfilActual) { mutableStateOf(perfilActual.nombre) }
    var tipoDocumento by remember(perfilActual) { mutableStateOf(perfilActual.tipoDocumento) }
    var numeroDocumento by remember(perfilActual) { mutableStateOf(perfilActual.numeroDocumento) }
    var telefono by remember(perfilActual) { mutableStateOf(perfilActual.telefono) }
    var fechaNacimiento by remember(perfilActual) { mutableStateOf(perfilActual.fechaNacimiento) }
    var tieneRut by remember(perfilActual) { mutableStateOf(perfilActual.tieneRut) }
    var responsableIva by remember(perfilActual) { mutableStateOf(perfilActual.responsableIva) }

    var intentado by remember { mutableStateOf(false) }

    val errorNombre = if (intentado) Validadores.nombre(nombre) else null
    val errorDocumento = if (intentado) Validadores.documento(numeroDocumento) else null
    val errorTelefono = if (intentado) Validadores.telefono(telefono) else null
    val errorFecha = if (intentado) Validadores.fechaNacimiento(fechaNacimiento) else null

    ScreenScaffold(
        titulo = "Datos personales",
        sub = "Bloque 1 de 5",
        onBack = onBack,
        footer = {
            if (errorRed != null) {
                Aviso(AvisoTipo.Alerta, negrita = "Error de red: ", texto = errorRed!!)
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario(
                if (cargando) "Guardando..." else "Guardar",
                Icons.Outlined.Check,
                onClick = {
                    intentado = true
                    if (errorNombre == null && errorDocumento == null && errorTelefono == null && errorFecha == null) {
                        val nuevoPerfil = PerfilDatos(
                            nombre = nombre,
                            tipoDocumento = tipoDocumento,
                            numeroDocumento = numeroDocumento,
                            telefono = telefono,
                            fechaNacimiento = fechaNacimiento,
                            tieneRut = tieneRut,
                            responsableIva = responsableIva,
                        )
                        perfilViewModel.guardar(nuevoPerfil) {
                            onGuardar()
                        }
                    }
                },
            )
        },
    ) {
        if (cargando && nombre.isEmpty() && numeroDocumento.isEmpty()) {
            Text("Cargando datos...", color = RC.Muted, fontSize = 14.sp)
        }

        CampoTexto("Nombre completo", "[Nombre y apellidos]", nombre, { nombre = it })
        if (errorNombre != null) {
            Text(errorNombre, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        Selector("Tipo de documento", listOf("Cédula de ciudadanía", "Cédula de extranjería", "Pasaporte"), tipoDocumento) { tipoDocumento = it }

        CampoTexto("Número de documento", "[Número]", numeroDocumento, { numeroDocumento = it }, teclado = KeyboardType.Number)
        if (errorDocumento != null) {
            Text(errorDocumento, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Teléfono de contacto", "3001234567", telefono, { telefono = it }, teclado = KeyboardType.Phone)
        if (errorTelefono != null) {
            Text(errorTelefono, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Fecha de nacimiento (AAAA-MM-DD)", "1990-05-15", fechaNacimiento, { fechaNacimiento = it })
        if (errorFecha != null) {
            Text(errorFecha, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        Selector("¿Tienes RUT?", listOf("Sí", "No"), tieneRut) { tieneRut = it }
        Selector("¿Eres responsable de IVA?", listOf("No", "Sí"), responsableIva) { responsableIva = it }
        Tarjeta {
            Etiqueta("DEPENDIENTES")
            Text(
                "Ningún dependiente registrado",
                fontSize = 12.sp,
                color = RC.Label,
                modifier = Modifier.padding(top = 6.dp, bottom = 10.dp),
            )
        }
        Row(
            Modifier
                .clickable { mostrar("Formulario de dependiente (sin mockup)") }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(Icons.Outlined.Add, null, tint = RC.Green, modifier = Modifier.size(18.dp))
            Text("Agregar dependiente", color = RC.Green, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PatrimonioScreen(onBack: () -> Unit) {
    val mostrar = LocalShowMessage.current
    ScreenScaffold(
        titulo = "Patrimonio",
        sub = "Al 31 de diciembre de 2025 · cifras de ejemplo",
        onBack = onBack,
        footer = {
            BotonPrimario("Agregar bien o deuda", Icons.Outlined.Add) {
                mostrar("Formulario de bien o deuda (sin mockup)")
            }
        },
    ) {
        FilasCard(
            encabezado = "BIENES",
            filas = listOf(
                Fila("Inmueble", "$ 180.000.000", "[Dirección]", Icons.Outlined.Home),
                Fila("Vehículo", "$ 35.000.000", "[Placa]", Icons.Outlined.DirectionsCar),
                Fila("Cuenta bancaria", "$ 8.000.000", "[Banco]", Icons.Outlined.AccountBalance),
            ),
        )
        FilasCard(
            encabezado = "DEUDAS",
            filas = listOf(Fila("Crédito hipotecario", "$ 90.000.000", "[Entidad]", Icons.Outlined.AccountBalance)),
        )
        FilasCard(
            listOf(
                Fila("Patrimonio bruto", "$ 223.000.000"),
                Fila("Deudas", "$ 90.000.000"),
                Fila("Patrimonio líquido", "$ 133.000.000", negrita = true),
            ),
        )
    }
}

@Composable
fun DeduccionesScreen(onBack: () -> Unit) {
    val mostrar = LocalShowMessage.current
    ScreenScaffold(
        titulo = "Deducciones y rentas exentas",
        sub = "Cédula general · cifras de ejemplo",
        onBack = onBack,
        footer = {
            BotonPrimario("Agregar beneficio", Icons.Outlined.Add) {
                mostrar("Formulario de beneficio (sin mockup)")
            }
        },
    ) {
        FilasCard(
            listOf(
                Fila("Renta exenta laboral 25%", "$ 27.330.000", "Automática · tope 790 UVT anuales", Icons.Outlined.Percent),
                Fila("Intereses de vivienda", "$ 3.000.000", icono = Icons.Outlined.Home),
                Fila("Dependientes", "$ 0", "Ninguno registrado", Icons.Outlined.Groups),
                Fila("Salud prepagada", "$ 0", icono = Icons.Outlined.Favorite),
                Fila("Aportes AFC o pensión voluntaria", "$ 0", icono = Icons.Outlined.Paid),
                Fila("Compras con factura electrónica (1%)", "$ 0", icono = Icons.Outlined.Receipt),
                Fila("Total beneficios", "$ 30.330.000", negrita = true),
            ),
        )
        FilasCard(
            listOf(Fila("Límite 40% o 1.340 UVT", "61% usado", "Usado $ 30.330.000 de $ 49.968.000")),
        )
    }
}

@Composable
fun RetencionesScreen(onBack: () -> Unit) {
    val mostrar = LocalShowMessage.current
    ScreenScaffold(
        titulo = "Retenciones y anticipos",
        sub = "Bloque 5 de 5 · cifras de ejemplo",
        onBack = onBack,
        footer = {
            BotonPrimario("Agregar retención", Icons.Outlined.Add) {
                mostrar("Formulario de retención (sin mockup)")
            }
        },
    ) {
        FilasCard(
            encabezado = "RETENCIÓN EN LA FUENTE POR PAGADOR",
            filas = listOf(
                Fila("[Pagador]", "$ 4.200.000", "Salario", Icons.Outlined.Work),
                Fila("[Cliente]", "$ 2.400.000", "Honorarios", Icons.Outlined.Person),
                Fila("Anticipo del año anterior", "$ 0"),
                Fila("Saldo a favor anterior", "$ 0"),
                Fila("Total", "$ 6.600.000", negrita = true),
            ),
        )
        Aviso(AvisoTipo.Alerta, texto = "Falta el certificado de ingresos y retenciones del empleador.")
    }
}

@Composable
fun SoportesScreen(onBack: () -> Unit) {
    val mostrar = LocalShowMessage.current
    ScreenScaffold(
        titulo = "Soportes",
        sub = "5 registrados · 1 faltante",
        onBack = onBack,
        footer = {
            BotonPrimario("Adjuntar soporte", Icons.Outlined.UploadFile) {
                mostrar("Selector de archivo o cámara (sin mockup)")
            }
        },
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Text("Todos", color = RC.Green, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Adjuntos", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Faltantes", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        FilasCard(
            listOf(
                Fila("[certificado_ingresos].pdf", sub = "Salario · [Pagador]", icono = Icons.Outlined.AttachFile, pill = "Adjunto" to PillTipo.Neutro),
                Fila("[contrato_arriendo].pdf", sub = "Arriendo · [Inmueble]", icono = Icons.Outlined.AttachFile, pill = "Adjunto" to PillTipo.Neutro),
                Fila("[certificado_banco].pdf", sub = "Intereses · [Banco]", icono = Icons.Outlined.AttachFile, pill = "Adjunto" to PillTipo.Neutro),
                Fila("[factura_vivienda].jpg", sub = "Intereses de vivienda", icono = Icons.Outlined.AttachFile, pill = "Adjunto" to PillTipo.Neutro),
                Fila("Certificado de retenciones", sub = "Honorarios · [Cliente]", icono = Icons.Outlined.Warning, pill = "Falta" to PillTipo.Aviso),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun DatosPreview() = MiRentaTheme { DatosScreen({}, {}) }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun PatrimonioPreview() = MiRentaTheme { PatrimonioScreen {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun DeduccionesPreview() = MiRentaTheme { DeduccionesScreen {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun RetencionesPreview() = MiRentaTheme { RetencionesScreen {} }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun SoportesPreview() = MiRentaTheme { SoportesScreen {} }

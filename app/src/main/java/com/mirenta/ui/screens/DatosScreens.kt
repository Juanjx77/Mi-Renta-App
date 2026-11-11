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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Check
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
import com.mirenta.domain.Campo
import com.mirenta.domain.PerfilDatos
import com.mirenta.domain.Reglas2025
import com.mirenta.domain.SeccionesDeclaracion
import com.mirenta.domain.Validadores
import com.mirenta.ui.PerfilViewModel
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.CampoEntrada
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
import com.mirenta.util.fm
import kotlin.math.abs

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
fun PatrimonioScreen(vm: RentaViewModel, onBack: () -> Unit, onGuardar: () -> Unit) {
    val e = vm.declaracion
    val o = vm.resultadoDeclaracion
    val errorRed by vm.errorRed.collectAsState()
    var intento by remember { mutableStateOf(false) }

    var patStr by remember(e.patrimonio) { mutableStateOf(if (e.patrimonio > 0) e.patrimonio.toString() else "") }
    var deuStr by remember(e.deudas) { mutableStateOf(if (e.deudas > 0) e.deudas.toString() else "") }
    var conStr by remember(e.consumos) { mutableStateOf(if (e.consumos > 0) e.consumos.toString() else "") }

    val esVacio = patStr.isBlank() || deuStr.isBlank() || conStr.isBlank()
    val tieneNegativos = e.patrimonio < 0 || e.deudas < 0 || e.consumos < 0

    ScreenScaffold(
        titulo = "Patrimonio",
        sub = "Al 31 de diciembre de 2025",
        onBack = onBack,
        footer = {
            if (errorRed != null) {
                Aviso(AvisoTipo.Alerta, negrita = "Error de red: ", texto = errorRed!!)
                Spacer(Modifier.height(4.dp))
            }
            if (intento && (esVacio || tieneNegativos)) {
                val mensaje = if (esVacio) "Completa todos los campos antes de guardar." else "No se permiten montos negativos en el patrimonio."
                Aviso(AvisoTipo.Alerta, texto = mensaje)
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario("Guardar", Icons.Outlined.Check) {
                intento = true
                if (!esVacio && !tieneNegativos) {
                    vm.guardarSeccion(SeccionesDeclaracion.Patrimonio) {
                        onGuardar()
                    }
                }
            }
        },
    ) {
        Aviso(
            AvisoTipo.Info,
            texto = "Aquí registras totales. El detalle por bien no se guarda en esta versión."
        )

        val topePatrimonio = Reglas2025.topePatrimonioDeclararUvt.valor * Reglas2025.uvt.valor
        if (e.patrimonio >= topePatrimonio) {
            Aviso(AvisoTipo.Info, texto = "Tu patrimonio bruto supera el tope de ${fm(topePatrimonio)} (${Reglas2025.topePatrimonioDeclararUvt.valor.toInt()} UVT), lo que te obliga a declarar por patrimonio.")
        }

        val topeConsumos = Reglas2025.topeConsumosDeclararUvt.valor * Reglas2025.uvt.valor
        if (e.consumos >= topeConsumos) {
            Aviso(AvisoTipo.Info, texto = "Tus consumos superan el tope de ${fm(topeConsumos)} (${Reglas2025.topeConsumosDeclararUvt.valor.toInt()} UVT), lo que te obliga a declarar por consumos.")
        }

        if (o.patrimonioLiquido < 0) {
            Aviso(AvisoTipo.Alerta, texto = "Atención: Tu patrimonio líquido es negativo (tus deudas superan a tu patrimonio bruto).")
        }

        CampoEntrada(
            campo = Campo.Patrimonio,
            valor = e.patrimonio,
            onCambio = { t ->
                val filtrado = t.filter { it.isDigit() }.take(12)
                patStr = filtrado
                vm.cambiarDeclaracion(Campo.Patrimonio, filtrado.toLongOrNull() ?: 0L)
            }
        )

        CampoEntrada(
            campo = Campo.Deudas,
            valor = e.deudas,
            onCambio = { t ->
                val filtrado = t.filter { it.isDigit() }.take(12)
                deuStr = filtrado
                vm.cambiarDeclaracion(Campo.Deudas, filtrado.toLongOrNull() ?: 0L)
            }
        )

        CampoEntrada(
            campo = Campo.Consumos,
            valor = e.consumos,
            onCambio = { t ->
                val filtrado = t.filter { it.isDigit() }.take(12)
                conStr = filtrado
                vm.cambiarDeclaracion(Campo.Consumos, filtrado.toLongOrNull() ?: 0L)
            }
        )

        FilasCard(
            encabezado = "RESUMEN PATRIMONIAL",
            filas = listOf(
                Fila("Patrimonio bruto", fm(e.patrimonio.toDouble())),
                Fila("Deudas", fm(e.deudas.toDouble())),
                Fila("Patrimonio líquido", fm(o.patrimonioLiquido), negrita = true),
            ),
        )
    }
}

@Composable
fun DeduccionesScreen(vm: RentaViewModel, onBack: () -> Unit, onGuardar: () -> Unit) {
    val e = vm.declaracion
    val o = vm.resultadoDeclaracion
    val errorRed by vm.errorRed.collectAsState()
    var intento by remember { mutableStateOf(false) }

    val tieneNegativos = e.aportes < 0 || e.vivienda < 0 || e.dependientes < 0 || e.prepagada < 0 || e.afc < 0

    ScreenScaffold(
        titulo = "Deducciones y rentas exentas",
        sub = "Cédula general · año gravable 2025",
        onBack = onBack,
        footer = {
            if (errorRed != null) {
                Aviso(AvisoTipo.Alerta, negrita = "Error de red: ", texto = errorRed!!)
                Spacer(Modifier.height(4.dp))
            }
            if (intento && tieneNegativos) {
                Aviso(AvisoTipo.Alerta, texto = "No se permiten montos negativos en las deducciones.")
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario("Guardar", Icons.Outlined.Check) {
                intento = true
                if (!tieneNegativos) {
                    vm.guardarSeccion(SeccionesDeclaracion.Deducciones) {
                        onGuardar()
                    }
                }
            }
        },
    ) {
        if (o.topeAlcanzado) {
            Aviso(AvisoTipo.Info, texto = "Has alcanzado el límite máximo de deducciones y rentas exentas (40% o 1.340 UVT).")
        }

        if (e.dependientes > Reglas2025.maxDependientes.valor.toLong()) {
            Aviso(AvisoTipo.Alerta, texto = "Has declarado más de los ${Reglas2025.maxDependientes.valor.toInt()} dependientes permitidos. Solo se contabilizarán hasta este máximo.")
        }

        listOf(Campo.Aportes, Campo.Vivienda, Campo.Dependientes, Campo.Prepagada, Campo.Afc).forEach { c ->
            CampoEntrada(
                campo = c,
                valor = e[c],
                onCambio = { t ->
                    val num = t.filter { it.isDigit() }.take(12).toLongOrNull() ?: 0L
                    vm.cambiarDeclaracion(c, num)
                }
            )
        }

        FilasCard(
            encabezado = "RESUMEN DE BENEFICIOS",
            filas = listOf(
                Fila("Deducciones", fm(o.deducciones - o.dependientesValor)),
                Fila("Renta exenta", fm(o.exenta)),
                Fila("Límite conjunto (40% o 1.340 UVT)", fm(o.limite)),
                Fila("Dependientes (fuera del límite)", fm(o.dependientesValor)),
                Fila("Beneficios aplicados", fm(o.beneficios), negrita = true),
            ),
        )
    }
}

@Composable
fun RetencionesScreen(vm: RentaViewModel, onBack: () -> Unit, onGuardar: () -> Unit) {
    val e = vm.declaracion
    val o = vm.resultadoDeclaracion
    val errorRed by vm.errorRed.collectAsState()
    var intento by remember { mutableStateOf(false) }

    val tieneNegativos = e.retenciones < 0

    ScreenScaffold(
        titulo = "Retenciones y anticipos",
        sub = "Bloque 5 de 5 · año gravable 2025",
        onBack = onBack,
        footer = {
            if (errorRed != null) {
                Aviso(AvisoTipo.Alerta, negrita = "Error de red: ", texto = errorRed!!)
                Spacer(Modifier.height(4.dp))
            }
            if (intento && tieneNegativos) {
                Aviso(AvisoTipo.Alerta, texto = "No se permiten montos negativos en las retenciones.")
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario("Guardar", Icons.Outlined.Check) {
                intento = true
                if (!tieneNegativos) {
                    vm.guardarSeccion(SeccionesDeclaracion.Retenciones) {
                        onGuardar()
                    }
                }
            }
        },
    ) {
        Aviso(
            AvisoTipo.Info,
            texto = "Las retenciones aparecen en el certificado de ingresos y retenciones (formulario 220) que te entrega tu empleador."
        )

        if (e.retenciones.toDouble() > o.impuesto) {
            Aviso(
                AvisoTipo.Info,
                texto = "Tus retenciones superan el impuesto a cargo, por lo que tienes un saldo a favor."
            )
        }

        CampoEntrada(
            campo = Campo.Retenciones,
            valor = e.retenciones,
            onCambio = { t ->
                val num = t.filter { it.isDigit() }.take(12).toLongOrNull() ?: 0L
                vm.cambiarDeclaracion(Campo.Retenciones, num)
            }
        )

        FilasCard(
            encabezado = "RESUMEN DE LIQUIDACIÓN",
            filas = listOf(
                Fila("Impuesto a cargo", fm(o.impuesto)),
                Fila("Retenciones", fm(e.retenciones.toDouble())),
                Fila(if (o.saldo < 0) "Saldo a favor" else "Saldo a pagar", fm(abs(o.saldo)), negrita = true),
            ),
        )
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
private fun PatrimonioPreview() = MiRentaTheme { PatrimonioScreen(RentaViewModel(), {}, {}) }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun DeduccionesPreview() = MiRentaTheme { DeduccionesScreen(RentaViewModel(), {}, {}) }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun RetencionesPreview() = MiRentaTheme { RetencionesScreen(RentaViewModel(), {}, {}) }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun SoportesPreview() = MiRentaTheme { SoportesScreen {} }

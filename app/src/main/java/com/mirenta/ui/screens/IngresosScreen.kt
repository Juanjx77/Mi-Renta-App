package com.mirenta.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirenta.domain.Campo
import com.mirenta.domain.SeccionesDeclaracion
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.CampoEntrada
import com.mirenta.ui.components.ScreenScaffold

@Composable
fun IngresosScreen(vm: RentaViewModel, onBack: () -> Unit, onGuardar: () -> Unit) {
    val e = vm.declaracion
    val errorRed by vm.errorRed.collectAsState()
    var intento by remember { mutableStateOf(false) }

    val todosCero = e.salario == 0L && e.honorarios == 0L && e.arriendos == 0L && e.intereses == 0L
    val tieneNegativos = e.salario < 0 || e.honorarios < 0 || e.arriendos < 0 || e.intereses < 0

    ScreenScaffold(
        titulo = "Ingresos",
        sub = "Bloque de ingresos brutos (laborales y de capital)",
        onBack = onBack,
        footer = {
            if (errorRed != null) {
                Aviso(AvisoTipo.Alerta, negrita = "Error de red: ", texto = errorRed!!)
                Spacer(Modifier.height(4.dp))
            }
            if (intento && (todosCero || tieneNegativos)) {
                val mensaje = if (todosCero) "Debes ingresar al menos un valor de ingresos." else "No se permiten montos negativos en los ingresos."
                Aviso(AvisoTipo.Alerta, texto = mensaje)
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario("Guardar", Icons.Outlined.Check) {
                intento = true
                if (!todosCero && !tieneNegativos) {
                    vm.guardarSeccion(SeccionesDeclaracion.Ingresos) {
                        onGuardar()
                    }
                }
            }
        },
    ) {
        listOf(Campo.Salario, Campo.Honorarios, Campo.Arriendos, Campo.Intereses).forEach { c ->
            CampoEntrada(
                campo = c,
                valor = e[c],
                onCambio = { t ->
                    val num = t.filter { it.isDigit() }.take(12).toLongOrNull() ?: 0L
                    vm.cambiarDeclaracion(c, num)
                }
            )
        }
    }
}

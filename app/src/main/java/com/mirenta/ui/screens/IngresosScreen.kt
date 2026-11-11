package com.mirenta.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mirenta.domain.Campo
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.CampoEntrada
import com.mirenta.ui.components.ScreenScaffold

@Composable
fun IngresosScreen(vm: RentaViewModel, onBack: () -> Unit, onGuardar: () -> Unit) {
    val e = vm.declaracion
    var intento by remember { mutableStateOf(false) }

    val tieneNegativos = e.salario < 0 || e.honorarios < 0 || e.arriendos < 0 || e.intereses < 0

    ScreenScaffold(
        titulo = "Ingresos",
        sub = "Bloque de ingresos brutos (laborales y de capital)",
        onBack = onBack,
        footer = {
            if (intento && tieneNegativos) {
                Aviso(AvisoTipo.Alerta, texto = "No se permiten montos negativos en los ingresos.")
            }
            BotonPrimario("Guardar", Icons.Outlined.Check) {
                intento = true
                if (!tieneNegativos) {
                    onGuardar()
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

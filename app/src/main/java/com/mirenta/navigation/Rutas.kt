package com.mirenta.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

object Rutas {
    const val Login = "login"
    const val Crear = "crear"
    const val Datos = "datos"
    const val Inicio = "inicio"
    const val Patrimonio = "patri"
    const val Exogena = "exogena"
    const val Simulador = "sim"
    const val Aprende = "aprende"
    const val Deducciones = "deduc"
    const val Retenciones = "reten"
    const val Formulario = "form"
    const val Detalle = "detalle"
    const val Verificar = "verif"
    const val Vista = "vista"
    const val Pdf = "pdf"
    const val Soportes = "soportes"
    const val Ingresos = "ingresos"
}

data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

/** Las 5 pestañas de la barra inferior del prototipo. */
val Pestanas = listOf(
    Pestana(Rutas.Inicio, "Inicio", Icons.Outlined.Home),
    Pestana(Rutas.Exogena, "Exógena", Icons.Outlined.FactCheck),
    Pestana(Rutas.Simulador, "Simular", Icons.Outlined.Calculate),
    Pestana(Rutas.Formulario, "Formulario", Icons.Outlined.Description),
    Pestana(Rutas.Aprende, "Aprende", Icons.Outlined.MenuBook),
)

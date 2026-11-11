package com.mirenta.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mirenta.data.AuthRepository
import com.mirenta.ui.AuthViewModel
import com.mirenta.ui.RentaViewModel
import com.mirenta.ui.components.LocalShowMessage
import com.mirenta.ui.screens.AprendeScreen
import com.mirenta.ui.screens.CrearScreen
import com.mirenta.ui.screens.DatosScreen
import com.mirenta.ui.screens.DeduccionesScreen
import com.mirenta.ui.screens.DetalleScreen
import com.mirenta.ui.screens.ExogenaScreen
import com.mirenta.ui.screens.FormularioScreen
import com.mirenta.ui.screens.IngresosScreen
import com.mirenta.ui.screens.InicioScreen
import com.mirenta.ui.screens.LoginScreen
import com.mirenta.ui.screens.PatrimonioScreen
import com.mirenta.ui.screens.PdfScreen
import com.mirenta.ui.screens.RetencionesScreen
import com.mirenta.ui.screens.SoportesScreen
import com.mirenta.ui.screens.SimuladorScreen
import com.mirenta.ui.screens.VerificarScreen
import com.mirenta.ui.screens.VistaScreen
import com.mirenta.ui.theme.RC
import kotlinx.coroutines.launch

@Composable
fun MiRentaApp() {
    val nav = rememberNavController()
    val vm: RentaViewModel = viewModel()
    val authVm: AuthViewModel = viewModel()
    val authRepo = remember { AuthRepository() }
    val startDestination = remember {
        if (authRepo.usuarioActual != null) Rutas.Inicio else Rutas.Login
    }

    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val mostrar: (String) -> Unit = { msg ->
        scope.launch {
            snack.currentSnackbarData?.dismiss()
            snack.showSnackbar(msg)
        }
    }

    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val conBarra = Pestanas.any { it.ruta == ruta }

    /** Ir a una pantalla: las pestañas reemplazan a la pestaña anterior, el resto se apila. */
    fun ir(destino: String) {
        if (Pestanas.any { it.ruta == destino }) {
            nav.navigate(destino) {
                popUpTo(Rutas.Inicio)
                launchSingleTop = true
            }
        } else {
            nav.navigate(destino) { launchSingleTop = true }
        }
    }

    fun volver() {
        nav.popBackStack()
    }

    /** Vuelve a Inicio; si todavía no está en la pila (primer ingreso), lo crea. */
    fun irAInicio() {
        if (!nav.popBackStack(Rutas.Inicio, false)) {
            nav.navigate(Rutas.Inicio) {
                popUpTo(nav.graph.id) { inclusive = true }
            }
        }
    }

    CompositionLocalProvider(LocalShowMessage provides mostrar) {
        Scaffold(
            containerColor = if (ruta == Rutas.Login) RC.Dark else RC.Bg,
            contentColor = RC.Dark,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    snack,
                    modifier = if (conBarra) Modifier else Modifier.navigationBarsPadding(),
                )
            },
            bottomBar = {
                if (conBarra) BarraPestanas(ruta) { ir(it) }
            },
        ) { padding ->
            Column(Modifier.padding(padding).fillMaxSize()) {
                NavHost(
                    navController = nav,
                    startDestination = startDestination,
                    modifier = Modifier.weight(1f),
                ) {
                    composable(Rutas.Login) {
                        LoginScreen(
                            authViewModel = authVm,
                            onIngresar = {
                                nav.navigate(Rutas.Inicio) {
                                    popUpTo(Rutas.Login) { inclusive = true }
                                }
                            },
                            onCrear = { ir(Rutas.Crear) },
                        )
                    }
                    composable(Rutas.Crear) {
                        CrearScreen(
                            authViewModel = authVm,
                            onBack = { volver() },
                            onCrear = {
                                nav.navigate(Rutas.Inicio) {
                                    popUpTo(Rutas.Login) { inclusive = true }
                                }
                            },
                            onYaTengoCuenta = { volver() },
                        )
                    }
                    composable(Rutas.Datos) {
                        DatosScreen(onBack = { irAInicio() }, onGuardar = { irAInicio() })
                    }
                    composable(Rutas.Ingresos) {
                        IngresosScreen(vm, onBack = { irAInicio() }, onGuardar = { irAInicio() })
                    }
                    composable(Rutas.Inicio) {
                        InicioScreen(
                            vm = vm,
                            authViewModel = authVm,
                            onNav = { ir(it) },
                            onLogout = {
                                vm.reiniciar()
                                nav.navigate(Rutas.Login) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                    composable(Rutas.Exogena) { ExogenaScreen(vm, onNav = { ir(it) }) }
                    composable(Rutas.Simulador) { SimuladorScreen(vm, onNav = { ir(it) }) }
                    composable(Rutas.Formulario) { FormularioScreen(onNav = { ir(it) }) }
                    composable(Rutas.Aprende) { AprendeScreen() }
                    composable(Rutas.Patrimonio) {
                        PatrimonioScreen(vm, onBack = { irAInicio() }, onGuardar = { irAInicio() })
                    }
                    composable(Rutas.Deducciones) {
                        DeduccionesScreen(vm, onBack = { irAInicio() }, onGuardar = { irAInicio() })
                    }
                    composable(Rutas.Retenciones) {
                        RetencionesScreen(vm, onBack = { irAInicio() }, onGuardar = { irAInicio() })
                    }
                    composable(Rutas.Soportes) { SoportesScreen(onBack = { volver() }) }
                    composable(Rutas.Detalle) { DetalleScreen(onBack = { volver() }) }
                    composable(Rutas.Verificar) {
                        VerificarScreen(vm, onBack = { volver() }, onNav = { ir(it) })
                    }
                    composable(Rutas.Vista) {
                        VistaScreen(vm, onBack = { volver() }, onNav = { ir(it) })
                    }
                    composable(Rutas.Pdf) {
                        PdfScreen(vm, onBack = { volver() }, onInicio = { irAInicio() })
                    }
                }
                if (!conBarra) {
                    // Mantiene la franja oscura bajo el contenido, como la barra de pestañas.
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .background(RC.Dark)
                            .windowInsetsBottomHeight(WindowInsets.navigationBars),
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraPestanas(rutaActual: String?, onSeleccionar: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(RC.Dark)
            .navigationBarsPadding(),
    ) {
        Pestanas.forEach { p ->
            val activa = p.ruta == rutaActual
            val color = if (activa) RC.Lima else RC.TabInactive
            Column(
                Modifier
                    .weight(1f)
                    .clickable(role = Role.Tab) { onSeleccionar(p.ruta) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(if (activa) RC.Lima else androidx.compose.ui.graphics.Color.Transparent),
                )
                Spacer(Modifier.height(10.dp))
                Icon(p.icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(4.dp))
                Text(
                    p.titulo,
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

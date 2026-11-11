package com.mirenta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mirenta.R
import com.mirenta.domain.Validadores
import com.mirenta.ui.AuthViewModel
import com.mirenta.ui.components.Aviso
import com.mirenta.ui.components.AvisoTipo
import com.mirenta.ui.components.BotonContorno
import com.mirenta.ui.components.BotonPrimario
import com.mirenta.ui.components.CampoTexto
import com.mirenta.ui.components.LocalShowMessage
import com.mirenta.ui.components.ScreenScaffold
import com.mirenta.ui.theme.MiRentaTheme
import com.mirenta.ui.theme.RC

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onIngresar: () -> Unit,
    onCrear: () -> Unit,
) {
    val mostrar = LocalShowMessage.current
    val cargando by authViewModel.cargando.collectAsState()
    val errorGeneral by authViewModel.errorGeneral.collectAsState()
    val mensajeInfo by authViewModel.mensajeInfo.collectAsState()

    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var intentado by remember { mutableStateOf(false) }

    val errorCorreo = if (intentado) Validadores.correo(correo) else null
    val errorClave = if (intentado) Validadores.claveLogin(clave) else null

    LaunchedEffect(mensajeInfo) {
        mensajeInfo?.let { mostrar(it) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(RC.Dark)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Logo Rentafácil",
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
            )
            Column {
                Text("Mi renta", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("Tu declaración de renta, simplificada.", color = RC.OnDarkMuted, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(36.dp))
        Text(
            buildAnnotatedString {
                append("Prepara tu renta ")
                withStyle(SpanStyle(color = RC.Lima)) { append("sin enredos") }
            },
            color = Color.White,
            fontSize = 34.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Inicia sesión para armar y revisar tu declaración.",
            color = RC.OnDarkMuted,
            fontSize = 15.sp,
        )
        Spacer(Modifier.height(22.dp))

        if (errorGeneral != null) {
            Aviso(AvisoTipo.Alerta, negrita = "Atención: ", texto = errorGeneral!!)
            Spacer(Modifier.height(14.dp))
        }

        CampoTexto(
            "Correo electrónico", "nombre@correo.com", correo, { correo = it },
            colorEtiqueta = Color.White, teclado = KeyboardType.Email,
        )
        if (errorCorreo != null) {
            Text(errorCorreo, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        CampoTexto(
            "Contraseña", "Contraseña", clave, { clave = it },
            colorEtiqueta = Color.White, teclado = KeyboardType.Password,
            transformacion = PasswordVisualTransformation(),
        )
        if (errorClave != null) {
            Text(errorClave, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        Text(
            "¿Olvidaste tu contraseña?",
            color = RC.Lima,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    intentado = true
                    if (Validadores.correo(correo) == null) {
                        authViewModel.recuperarClave(correo)
                    }
                }
                .padding(vertical = 10.dp),
        )
        Spacer(Modifier.height(8.dp))
        BotonPrimario(
            if (cargando) "Iniciando sesión..." else "Ingresar",
            Icons.AutoMirrored.Outlined.ArrowForward,
            onClick = {
                intentado = true
                val errC = Validadores.correo(correo)
                val errK = Validadores.claveLogin(clave)
                if (errC == null && errK == null) {
                    authViewModel.iniciarSesion(correo, clave, onIngresar)
                }
            },
        )
        Spacer(Modifier.height(12.dp))
        BotonContorno("Crear cuenta", color = Color.White, onClick = onCrear)
        Spacer(Modifier.height(32.dp))
        Text(
            "Esta herramienta organiza y calcula tu información tributaria. No reemplaza la asesoría de un contador.",
            color = RC.OnDarkMuted,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun CrearScreen(
    authViewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit,
    onCrear: () -> Unit,
    onYaTengoCuenta: () -> Unit,
) {
    val cargando by authViewModel.cargando.collectAsState()
    val errorGeneral by authViewModel.errorGeneral.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var terminosAceptados by remember { mutableStateOf(false) }
    var intentado by remember { mutableStateOf(false) }

    val errorNombre = if (intentado) Validadores.nombre(nombre) else null
    val errorCorreo = if (intentado) Validadores.correo(correo) else null
    val errorClave = if (intentado) Validadores.claveNueva(clave) else null
    val errorConfirmar = if (intentado) Validadores.confirmacion(clave, confirmar) else null
    val errorTerminos = if (intentado) Validadores.terminos(terminosAceptados) else null

    ScreenScaffold(
        titulo = "Crear cuenta",
        onBack = onBack,
        footer = {
            if (errorGeneral != null) {
                Aviso(AvisoTipo.Alerta, negrita = "Atención: ", texto = errorGeneral!!)
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario(if (cargando) "Creando cuenta..." else "Crear cuenta", onClick = {
                intentado = true
                val errN = Validadores.nombre(nombre)
                val errC = Validadores.correo(correo)
                val errK = Validadores.claveNueva(clave)
                val errCf = Validadores.confirmacion(clave, confirmar)
                val errT = Validadores.terminos(terminosAceptados)
                if (errN == null && errC == null && errK == null && errCf == null && errT == null) {
                    authViewModel.registrar(nombre, correo, clave, confirmar, terminosAceptados, onCrear)
                }
            })
            TextButton(onClick = onYaTengoCuenta, modifier = Modifier.fillMaxWidth()) {
                Text("Ya tengo cuenta", color = RC.Green, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
    ) {
        CampoTexto("Nombre completo", "[Nombre y apellidos]", nombre, { nombre = it })
        if (errorNombre != null) {
            Text(errorNombre, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Correo electrónico", "nombre@correo.com", correo, { correo = it }, teclado = KeyboardType.Email)
        if (errorCorreo != null) {
            Text(errorCorreo, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Contraseña", "Mínimo 8 caracteres", clave, { clave = it }, teclado = KeyboardType.Password, transformacion = PasswordVisualTransformation())
        if (errorClave != null) {
            Text(errorClave, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Confirmar contraseña", "Repite la contraseña", confirmar, { confirmar = it }, teclado = KeyboardType.Password, transformacion = PasswordVisualTransformation())
        if (errorConfirmar != null) {
            Text(errorConfirmar, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        Row(
            Modifier.fillMaxWidth().clickable { terminosAceptados = !terminosAceptados }.padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = terminosAceptados,
                onCheckedChange = { terminosAceptados = it },
                colors = CheckboxDefaults.colors(checkedColor = RC.Green),
            )
            Text("Acepto el tratamiento de mis datos personales y tributarios.", fontSize = 14.sp)
        }
        if (errorTerminos != null) {
            Text(errorTerminos, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp, bottom = 4.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun LoginPreview() = MiRentaTheme { LoginScreen(onIngresar = {}, onCrear = {}) }

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun CrearPreview() = MiRentaTheme { CrearScreen(onBack = {}, onCrear = {}, onYaTengoCuenta = {}) }

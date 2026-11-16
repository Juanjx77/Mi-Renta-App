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

    val errorCorreo = if (authViewModel.loginIntentado) Validadores.correo(authViewModel.loginCorreo) else null
    val errorClave = if (authViewModel.loginIntentado) Validadores.claveLogin(authViewModel.loginClave) else null

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
            "Correo electrónico", "nombre@correo.com", authViewModel.loginCorreo, { authViewModel.loginCorreo = it },
            colorEtiqueta = Color.White, teclado = KeyboardType.Email,
        )
        if (errorCorreo != null) {
            Text(errorCorreo, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        CampoTexto(
            "Contraseña", "Contraseña", authViewModel.loginClave, { authViewModel.loginClave = it },
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
                    authViewModel.loginIntentado = true
                    if (Validadores.correo(authViewModel.loginCorreo) == null) {
                        authViewModel.recuperarClave()
                    }
                }
                .padding(vertical = 10.dp),
        )
        Spacer(Modifier.height(8.dp))
        BotonPrimario(
            if (cargando) "Iniciando sesión..." else "Ingresar",
            Icons.AutoMirrored.Outlined.ArrowForward,
            onClick = {
                authViewModel.loginIntentado = true
                val errC = Validadores.correo(authViewModel.loginCorreo)
                val errK = Validadores.claveLogin(authViewModel.loginClave)
                if (errC == null && errK == null) {
                    authViewModel.iniciarSesion(onIngresar)
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

    val errorNombre = if (authViewModel.crearIntentado) Validadores.nombre(authViewModel.crearNombre) else null
    val errorCorreo = if (authViewModel.crearIntentado) Validadores.correo(authViewModel.crearCorreo) else null
    val errorClave = if (authViewModel.crearIntentado) Validadores.claveNueva(authViewModel.crearClave) else null
    val errorConfirmar = if (authViewModel.crearIntentado) Validadores.confirmacion(authViewModel.crearClave, authViewModel.crearConfirmar) else null
    val errorTerminos = if (authViewModel.crearIntentado) Validadores.terminos(authViewModel.crearTerminosAceptados) else null

    ScreenScaffold(
        titulo = "Crear cuenta",
        onBack = onBack,
        footer = {
            if (errorGeneral != null) {
                Aviso(AvisoTipo.Alerta, negrita = "Atención: ", texto = errorGeneral!!)
                Spacer(Modifier.height(4.dp))
            }
            BotonPrimario(if (cargando) "Creando cuenta..." else "Crear cuenta", onClick = {
                authViewModel.crearIntentado = true
                val errN = Validadores.nombre(authViewModel.crearNombre)
                val errC = Validadores.correo(authViewModel.crearCorreo)
                val errK = Validadores.claveNueva(authViewModel.crearClave)
                val errCf = Validadores.confirmacion(authViewModel.crearClave, authViewModel.crearConfirmar)
                val errT = Validadores.terminos(authViewModel.crearTerminosAceptados)
                if (errN == null && errC == null && errK == null && errCf == null && errT == null) {
                    authViewModel.registrar(onCrear)
                }
            })
            TextButton(onClick = onYaTengoCuenta, modifier = Modifier.fillMaxWidth()) {
                Text("Ya tengo cuenta", color = RC.Green, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
    ) {
        CampoTexto("Nombre completo", "[Nombre y apellidos]", authViewModel.crearNombre, { authViewModel.crearNombre = it })
        if (errorNombre != null) {
            Text(errorNombre, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Correo electrónico", "nombre@correo.com", authViewModel.crearCorreo, { authViewModel.crearCorreo = it }, teclado = KeyboardType.Email)
        if (errorCorreo != null) {
            Text(errorCorreo, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Contraseña", "Mínimo 8 caracteres", authViewModel.crearClave, { authViewModel.crearClave = it }, teclado = KeyboardType.Password, transformacion = PasswordVisualTransformation())
        if (errorClave != null) {
            Text(errorClave, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        CampoTexto("Confirmar contraseña", "Repite la contraseña", authViewModel.crearConfirmar, { authViewModel.crearConfirmar = it }, teclado = KeyboardType.Password, transformacion = PasswordVisualTransformation())
        if (errorConfirmar != null) {
            Text(errorConfirmar, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        Row(
            Modifier.fillMaxWidth().clickable { authViewModel.crearTerminosAceptados = !authViewModel.crearTerminosAceptados }.padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = authViewModel.crearTerminosAceptados,
                onCheckedChange = { authViewModel.crearTerminosAceptados = it },
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

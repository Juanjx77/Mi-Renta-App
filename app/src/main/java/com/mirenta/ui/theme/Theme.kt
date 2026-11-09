package com.mirenta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Paleta tomada del prototipo HTML. */
object RC {
    val Dark = Color(0xFF112B25)
    val Lima = Color(0xFFA3F843)
    val Green = Color(0xFF1F6B3A)
    val Mid = Color(0xFF4CAF6E)
    val Bg = Color(0xFFF4F6F5)
    val Muted = Color(0xFF5F6F68)
    val Border = Color(0xFFE3EBE6)
    val Divider = Color(0xFFEDF0EE)
    val TabInactive = Color(0xFF9FB5AB)
    val Field = Color(0xFFCFD8D3)
    val Label = Color(0xFF5B6572)
    val OnDarkMuted = Color(0xFFD6E2DC)
}

private val Esquema = lightColorScheme(
    primary = RC.Lima,
    onPrimary = RC.Dark,
    secondary = RC.Green,
    background = RC.Bg,
    onBackground = RC.Dark,
    surface = Color.White,
    onSurface = RC.Dark,
)

@Composable
fun MiRentaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Esquema, content = content)
}

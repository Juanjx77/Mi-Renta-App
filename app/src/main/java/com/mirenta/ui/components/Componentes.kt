package com.mirenta.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mirenta.domain.Campo
import com.mirenta.util.fm
import com.mirenta.util.miles
import androidx.compose.ui.unit.sp
import com.mirenta.ui.theme.RC

/** Muestra un mensaje breve (Snackbar). Sirve para los botones que en el HTML no tenían pantalla ("sin mockup"). */
val LocalShowMessage = staticCompositionLocalOf<(String) -> Unit> { {} }

// ---------------------------------------------------------------- estructura

/** Encabezado oscuro + cuerpo con scroll + pie opcional con botones. */
@Composable
fun ScreenScaffold(
    titulo: String,
    pre: String? = null,
    sub: String? = null,
    onBack: (() -> Unit)? = null,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(RC.Bg)) {
        Encabezado(titulo, pre, sub, onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido,
        )
        if (footer != null) {
            Column(
                Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = footer,
            )
        }
    }
}

@Composable
private fun Encabezado(titulo: String, pre: String?, sub: String?, onBack: (() -> Unit)?) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(RC.Dark, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 22.dp),
    ) {
        if (onBack != null) {
            Row(
                Modifier.heightIn(min = 48.dp).clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.ChevronLeft, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text("Volver", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        } else if (pre != null) {
            Text(pre, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
        }
        Text(titulo, color = Color.White, fontSize = 26.sp, lineHeight = 29.sp, fontWeight = FontWeight.ExtraBold)
        if (sub != null) {
            Text(sub, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

// ---------------------------------------------------------------- tarjetas y filas

@Composable
fun Tarjeta(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(18.dp))
            .border(1.dp, RC.Border, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp),
        content = content,
    )
}

@Composable
fun Etiqueta(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        color = RC.Label,
        fontSize = 12.sp,
        modifier = modifier.padding(top = 10.dp, bottom = 2.dp),
    )
}

enum class PillTipo { Ok, Aviso, Neutro }

@Composable
fun Pill(texto: String, tipo: PillTipo) {
    val (fondo, texto2) = when (tipo) {
        PillTipo.Ok -> RC.Mid to Color(0xFF0B1A16)
        PillTipo.Aviso -> Color(0xFFF5B942) to Color(0xFF3B2A00)
        PillTipo.Neutro -> RC.Divider to RC.Muted
    }
    Box(
        Modifier
            .clip(CircleShape)
            .background(fondo)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(texto, color = texto2, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

data class Fila(
    val titulo: String,
    val valor: String = "",
    val sub: String? = null,
    val icono: ImageVector? = null,
    val negrita: Boolean = false,
    val pill: Pair<String, PillTipo>? = null,
    /** Ruta a la que navega al tocar la fila. */
    val destino: String? = null,
)

@Composable
fun FilasCard(filas: List<Fila>, encabezado: String? = null, onNav: (String) -> Unit = {}) {
    Tarjeta {
        if (encabezado != null) Etiqueta(encabezado)
        filas.forEachIndexed { i, f ->
            if (i > 0) HorizontalDivider(color = RC.Divider)
            FilaItem(f, onNav)
        }
    }
}

@Composable
private fun FilaItem(f: Fila, onNav: (String) -> Unit) {
    val clic = if (f.destino != null) Modifier.clickable { onNav(f.destino) } else Modifier
    Row(
        Modifier.fillMaxWidth().then(clic).padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (f.icono != null) {
                Box(
                    Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFE8F4EC)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(f.icono, null, tint = RC.Green, modifier = Modifier.size(18.dp))
                }
            }
            Column {
                Text(
                    f.titulo,
                    fontSize = 14.sp,
                    fontWeight = if (f.negrita) FontWeight.Bold else FontWeight.Normal,
                )
                if (f.sub != null) {
                    Text(f.sub, fontSize = 12.sp, color = Color(0xFF6B7280))
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        if (f.pill != null) {
            Pill(f.pill.first, f.pill.second)
        } else {
            Text(f.valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, softWrap = false)
        }
    }
}

// ---------------------------------------------------------------- avisos

enum class AvisoTipo { Alerta, Info, Edu }

private data class EstiloAviso(val fondo: Color, val borde: Color, val texto: Color, val icono: ImageVector)

@Composable
fun Aviso(tipo: AvisoTipo, negrita: String? = null, texto: String = "") {
    val e = when (tipo) {
        AvisoTipo.Alerta -> EstiloAviso(Color(0xFFFFF1D6), Color(0xFFF0C35A), Color(0xFF6B4300), Icons.Outlined.Warning)
        AvisoTipo.Info -> EstiloAviso(Color(0xFFE8F4EC), Color(0xFFC5E3CF), RC.Green, Icons.Outlined.Info)
        AvisoTipo.Edu -> EstiloAviso(Color(0xFFF3F9E6), Color(0xFFD6E8A8), Color(0xFF2F4A12), Icons.Outlined.MenuBook)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(e.fondo, RoundedCornerShape(14.dp))
            .border(1.dp, e.borde, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
    ) {
        Icon(e.icono, null, tint = e.texto, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            buildAnnotatedString {
                if (negrita != null) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(negrita) }
                append(texto)
            },
            color = e.texto,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

// ---------------------------------------------------------------- botones

@Composable
private fun RowScope.ContenidoBoton(texto: String, icono: ImageVector?) {
    if (icono != null) {
        Icon(icono, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
    }
    Text(texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun BotonPrimario(
    texto: String,
    icono: ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = RC.Lima, contentColor = RC.Dark),
        contentPadding = PaddingValues(15.dp),
    ) { ContenidoBoton(texto, icono) }
}

@Composable
fun BotonContorno(
    texto: String,
    icono: ImageVector? = null,
    color: Color = RC.Dark,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        contentPadding = PaddingValues(15.dp),
    ) { ContenidoBoton(texto, icono) }
}

// ---------------------------------------------------------------- campos

@Composable
fun CampoTexto(
    etiqueta: String?,
    placeholder: String,
    valor: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    colorEtiqueta: Color = RC.Dark,
    teclado: KeyboardType = KeyboardType.Text,
    transformacion: VisualTransformation = VisualTransformation.None,
) {
    Column(modifier.fillMaxWidth()) {
        if (etiqueta != null) {
            Text(etiqueta, color = colorEtiqueta, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(4.dp))
        }
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            visualTransformation = transformacion,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = RC.Green,
                unfocusedBorderColor = RC.Field,
                focusedTextColor = RC.Dark,
                unfocusedTextColor = RC.Dark,
                cursorColor = RC.Green,
            ),
        )
        Spacer(Modifier.size(10.dp))
    }
}

/** Campo de formulario que maneja su propio estado (para formularios sin lógica, como en el HTML). */
@Composable
fun CampoSimple(
    etiqueta: String,
    placeholder: String,
    teclado: KeyboardType = KeyboardType.Text,
    transformacion: VisualTransformation = VisualTransformation.None,
) {
    var texto by remember { mutableStateOf("") }
    CampoTexto(etiqueta, placeholder, texto, { texto = it }, teclado = teclado, transformacion = transformacion)
}

@Composable
fun Selector(etiqueta: String, opciones: List<String>, valor: String = opciones.first(), onCambio: (String) -> Unit = {}) {
    var abierto by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(etiqueta, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(4.dp))
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, RC.Field, RoundedCornerShape(14.dp))
                    .clickable { abierto = true }
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(valor, fontSize = 14.sp)
                Icon(Icons.Filled.ArrowDropDown, null)
            }
            DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                opciones.forEach { o ->
                    DropdownMenuItem(text = { Text(o) }, onClick = { onCambio(o); abierto = false })
                }
            }
        }
        Spacer(Modifier.size(10.dp))
    }
}

@Composable
fun Casilla(texto: String) {
    var marcada by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().clickable { marcada = !marcada }.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = marcada,
            onCheckedChange = { marcada = it },
            colors = CheckboxDefaults.colors(checkedColor = RC.Green),
        )
        Text(texto, fontSize = 14.sp)
    }
}

/** Muestra 1234567 como 1.234.567 mientras se escribe (el valor guardado son solo dígitos). */
object MilesTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val crudo = text.text
        val n = crudo.length
        val salida = StringBuilder()
        crudo.forEachIndexed { i, c ->
            if (i > 0 && (n - i) % 3 == 0) salida.append('.')
            salida.append(c)
        }
        val formateado = salida.toString()
        val mapa = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var puntos = 0
                for (i in 1 until minOf(offset, n)) if ((n - i) % 3 == 0) puntos++
                return offset + puntos
            }

            override fun transformedToOriginal(offset: Int): Int {
                val o = offset.coerceIn(0, formateado.length)
                return o - formateado.substring(0, o).count { it == '.' }
            }
        }
        return TransformedText(AnnotatedString(formateado), mapa)
    }
}

@Composable
fun CampoEntrada(
    campo: Campo,
    valor: Long,
    onCambio: (String) -> Unit,
) {
    Column {
        if (campo.seccion.isNotEmpty()) Etiqueta(campo.seccion)
        CampoTexto(
            etiqueta = campo.etiqueta,
            placeholder = if (campo.ejemplo > 0) miles(campo.ejemplo) else "0",
            valor = if (valor > 0) valor.toString() else "",
            onCambio = onCambio,
            teclado = KeyboardType.Number,
            transformacion = if (campo.soloCantidad) VisualTransformation.None else MilesTransformation,
        )
        Text(
            buildAnnotatedString {
                append(campo.ayuda)
                if (valor > 0) {
                    append(" · ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(if (campo.soloCantidad) valor.toString() else fm(valor.toDouble()))
                    }
                }
            },
            fontSize = 12.sp,
            color = RC.Label,
        )
    }
}

# Mi Renta (Android · Jetpack Compose)

Port del prototipo HTML `Mi_Renta___Prototipo.html` a un proyecto de Android Studio.

## Cómo abrirlo
1. Android Studio (Ladybug o más reciente) → **Open** → carpeta `MiRenta`.
2. Espera el *Gradle Sync* (descarga Gradle 8.9 y las dependencias; necesita Internet y JDK 17, que ya trae Android Studio).
3. Ejecuta el módulo `app` en un emulador o dispositivo (minSdk 24).

Cada pantalla tiene su `@Preview` al final del archivo (panel *Split/Design*).

## Navegación (igual que el HTML)
- `Login` → `Inicio` (o `Crear cuenta` → `Datos personales` → `Inicio`).
- Barra inferior con 5 pestañas: **Inicio · Exógena · Simular · Formulario · Aprende**.
- Pantallas internas con botón *Volver*: Datos, Patrimonio, Deducciones, Retenciones, Soportes,
  Detalle de renta líquida, Verificar y exportar, Vista previa y PDF.
- Definida en `navigation/MiRentaApp.kt` (Navigation Compose) y `navigation/Rutas.kt`.

## Estructura
```
com.mirenta
├─ MainActivity.kt
├─ navigation/   Rutas.kt, MiRentaApp.kt (NavHost + barra de pestañas)
├─ domain/       Entradas.kt, TaxCalculator.kt (mismo cálculo simplificado del HTML)
├─ ui/
│  ├─ RentaViewModel.kt   estado compartido (exógena + simulador)
│  ├─ theme/Theme.kt      paleta del prototipo
│  ├─ components/         encabezado, tarjetas, filas, botones, campos
│  └─ screens/            una función @Composable por pantalla (+ previews)
```

## Qué es real y qué es simulado (igual que en el prototipo)
- Simulador y documento: **cálculo real** con las fórmulas simplificadas del HTML (UVT 2025 = $ 49.799).
- Exógena: el selector de archivos es real, pero la lectura es **simulada** (precarga cifras de ejemplo).
- Inicio, Formulario 210, Patrimonio, Deducciones, Retenciones y Soportes: cifras de ejemplo fijas.
- Botones que en el HTML decían "sin mockup" y "Guardar como PDF" muestran un mensaje (Snackbar).
  La exportación a PDF queda como `TODO` (ver `DocumentoScreens.kt`).
- Tema solo claro y fuente por defecto del sistema (el HTML usa Plus Jakarta Sans; para usarla,
  agrega los .ttf en `res/font` y define un `FontFamily` en `Theme.kt`).

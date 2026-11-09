package com.mirenta.domain

import java.util.Calendar

/** Validaciones en Kotlin puro (sin imports de Android) para tests unitarios. */
object Validadores {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val documentoRegex = Regex("^\\d{6,12}$")
    private val telefonoRegex = Regex("^3\\d{9}$")
    private val fechaRegex = Regex("^(\\d{4})-(\\d{2})-(\\d{2})$")

    fun nombre(valor: String): String? {
        val partes = valor.trim().split(Regex("\\s+"))
        return if (partes.size < 2 || partes.any { it.length < 2 }) {
            "Ingresa al menos un nombre y un apellido"
        } else {
            null
        }
    }

    fun textoNoVacio(valor: String): String? {
        return if (valor.trim().isEmpty()) {
            "Este campo no puede estar vacío"
        } else {
            null
        }
    }

    fun documento(valor: String): String? {
        val limpio = valor.trim()
        return if (!documentoRegex.matches(limpio)) {
            "El número de documento debe tener entre 6 y 12 dígitos"
        } else {
            null
        }
    }

    fun telefono(valor: String): String? {
        val limpio = valor.trim()
        return if (limpio.isNotEmpty() && !telefonoRegex.matches(limpio)) {
            "El teléfono debe tener 10 dígitos y empezar por 3"
        } else {
            null
        }
    }

    fun fechaNacimiento(valor: String): String? {
        val limpio = valor.trim()
        if (limpio.isEmpty()) return "La fecha de nacimiento es obligatoria"

        val match = fechaRegex.matchEntire(limpio) ?: return "Formato de fecha inválido (AAAA-MM-DD)"
        val (anioStr, mesStr, diaStr) = match.destructured
        val anio = anioStr.toIntOrNull() ?: return "Año inválido"
        val mes = mesStr.toIntOrNull() ?: return "Mes inválido"
        val dia = diaStr.toIntOrNull() ?: return "Día inválido"

        if (mes !in 1..12 || dia !in 1..31) return "Fecha inválida"

        val diasMes = when (mes) {
            2 -> if ((anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
        if (dia > diasMes) return "Día inválido para el mes"

        val calNac = Calendar.getInstance().apply {
            set(anio, mes - 1, dia)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val hoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calNac.after(hoy)) {
            return "La fecha no puede ser futura"
        }

        val cal18 = (hoy.clone() as Calendar).apply {
            add(Calendar.YEAR, -18)
        }
        if (calNac.after(cal18)) {
            return "Debes ser mayor de 18 años"
        }

        return null
    }

    fun correo(valor: String): String? {
        val limpio = valor.trim()
        return if (limpio.isEmpty() || !emailRegex.matches(limpio)) {
            "Ingresa un correo electrónico válido"
        } else {
            null
        }
    }

    fun claveNueva(valor: String): String? {
        val tieneLetra = valor.any { it.isLetter() }
        val tieneNumero = valor.any { it.isDigit() }
        return if (valor.length < 8 || !tieneLetra || !tieneNumero) {
            "Mínimo 8 caracteres, al menos una letra y un número"
        } else {
            null
        }
    }

    fun claveLogin(valor: String): String? {
        return if (valor.isEmpty()) {
            "La contraseña no puede estar vacía"
        } else {
            null
        }
    }

    fun confirmacion(clave: String, confirmacion: String): String? {
        return if (clave != confirmacion) {
            "Las contraseñas no coinciden"
        } else {
            null
        }
    }

    fun terminos(aceptado: Boolean): String? {
        return if (!aceptado) {
            "Debes aceptar los términos y condiciones"
        } else {
            null
        }
    }
}

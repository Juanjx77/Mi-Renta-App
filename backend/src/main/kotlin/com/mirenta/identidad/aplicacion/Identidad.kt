package com.mirenta.identidad.aplicacion

import com.mirenta.identidad.dominio.Perfil

/** Puerto secundario de persistencia del perfil. */
interface PerfilRepository {
    fun buscar(usuarioId: String): Perfil?
    fun guardar(perfil: Perfil): Perfil
}

/** Puerto secundario: verifica un token y devuelve el id del usuario. Aísla al núcleo del proveedor (ADR-007). */
fun interface VerificadorIdentidad {
    /** Devuelve el uid, o null si el token no es válido. */
    fun verificar(token: String): String?
}

class ConsultarPerfil(private val repositorio: PerfilRepository) {
    fun ejecutar(usuarioId: String): Perfil? = repositorio.buscar(usuarioId)
}

class GuardarPerfil(private val repositorio: PerfilRepository) {
    fun ejecutar(perfil: Perfil): Perfil = repositorio.guardar(perfil)
}

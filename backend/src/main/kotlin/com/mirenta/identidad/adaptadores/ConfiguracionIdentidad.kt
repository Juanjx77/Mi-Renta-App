package com.mirenta.identidad.adaptadores

import com.mirenta.compartido.seguridad.UsuarioActual
import com.mirenta.identidad.adaptadores.firebase.DemoVerificadorIdentidad
import com.mirenta.identidad.adaptadores.firebase.FirebaseVerificadorIdentidad
import com.mirenta.identidad.aplicacion.ConsultarPerfil
import com.mirenta.identidad.aplicacion.GuardarPerfil
import com.mirenta.identidad.aplicacion.PerfilRepository
import com.mirenta.identidad.aplicacion.VerificadorIdentidad
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
class ConfiguracionIdentidad {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean fun consultarPerfil(repo: PerfilRepository) = ConsultarPerfil(repo)
    @Bean fun guardarPerfil(repo: PerfilRepository) = GuardarPerfil(repo)

    /** Elige el adaptador del puerto según la configuración: cambiar de proveedor no toca el núcleo. */
    @Bean
    fun verificadorIdentidad(
        @Value("\${mirenta.auth.modo}") modo: String,
        @Value("\${mirenta.auth.firebase-project-id}") projectId: String,
    ): VerificadorIdentidad = when (modo) {
        "firebase" -> {
            require(projectId.isNotBlank()) { "Con AUTH_MODO=firebase hay que definir FIREBASE_PROJECT_ID" }
            FirebaseVerificadorIdentidad(projectId)
        }
        "demo" -> {
            log.warn("AUTH_MODO=demo: el token Bearer se acepta como uid. Solo para desarrollo.")
            DemoVerificadorIdentidad()
        }
        else -> error("mirenta.auth.modo desconocido: $modo")
    }

    @Bean
    fun filtroAutenticacion(verificador: VerificadorIdentidad): FilterRegistrationBean<FiltroAutenticacion> =
        FilterRegistrationBean(FiltroAutenticacion(verificador)).apply { addUrlPatterns("/api/*") }
}

/** Adaptador primario: exige `Authorization: Bearer <token>` en las rutas de la API y deja el uid en la petición. */
class FiltroAutenticacion(private val verificador: VerificadorIdentidad) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")?.trim()
        val uid = token?.takeIf { it.isNotEmpty() }?.let(verificador::verificar)
        if (uid == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            response.writer.write("""{"codigo":"NO_AUTENTICADO","mensaje":"Falta el token o no es válido"}""")
            return
        }
        request.setAttribute(UsuarioActual.ATRIBUTO, uid)
        chain.doFilter(request, response)
    }
}

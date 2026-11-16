package com.mirenta.identidad.adaptadores.firebase

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mirenta.identidad.aplicacion.VerificadorIdentidad
import java.util.Base64
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

/**
 * Adaptador secundario: verifica el ID token de Firebase Authentication (firma RS256 con las llaves públicas
 * de Google, emisor, audiencia y expiración). No necesita credenciales, solo el id del proyecto Firebase.
 */
class FirebaseVerificadorIdentidad(projectId: String) : VerificadorIdentidad {
    private val log = LoggerFactory.getLogger(javaClass)

    private val decoder = NimbusJwtDecoder
        .withJwkSetUri("https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com")
        .build()
        .apply {
            val audiencia = { jwt: org.springframework.security.oauth2.jwt.Jwt ->
                if (jwt.audience.contains(projectId)) OAuth2TokenValidatorResult.success()
                else OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_token", "Audiencia inválida", null))
            }
            setJwtValidator(
                DelegatingOAuth2TokenValidator(
                    JwtValidators.createDefaultWithIssuer("https://securetoken.google.com/$projectId"),
                    audiencia,
                )
            )
        }

    override fun verificar(token: String): String? = try {
        decoder.decode(token).subject?.takeIf { it.isNotBlank() }
    } catch (e: JwtException) {
        log.debug("Token de Firebase rechazado: {}", e.message)
        null
    }
}

/**
 * Solo para desarrollo y pruebas: acepta un uid plano (para Swagger) o un JWT de Firebase SIN verificar la firma
 * (para probar la app en local). Nunca activar en un despliegue público.
 */
class DemoVerificadorIdentidad : VerificadorIdentidad {
    private val mapper = jacksonObjectMapper()
    private val uidValido = Regex("[A-Za-z0-9_-]{3,128}")

    override fun verificar(token: String): String? {
        val partes = token.split(".")
        val uid = if (partes.size == 3) {
            runCatching { mapper.readTree(Base64.getUrlDecoder().decode(partes[1])).path("sub").asText() }.getOrNull()
        } else token
        return uid?.takeIf { it.matches(uidValido) }
    }
}

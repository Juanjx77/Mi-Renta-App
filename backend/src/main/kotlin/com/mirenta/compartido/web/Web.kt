package com.mirenta.compartido.web

import com.mirenta.declaracion.aplicacion.ConflictoVersionException
import com.mirenta.declaracion.dominio.AnioNoSoportadoException
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

data class ErrorDto(val codigo: String, val mensaje: String)

/** Traduce excepciones del núcleo a respuestas HTTP con un formato único. */
@RestControllerAdvice
class ManejadorErrores {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ConflictoVersionException::class)
    fun conflicto(e: ConflictoVersionException) = respuesta(HttpStatus.CONFLICT, "CONFLICTO_VERSION", e.message)

    @ExceptionHandler(AnioNoSoportadoException::class)
    fun anio(e: AnioNoSoportadoException) = respuesta(HttpStatus.NOT_FOUND, "ANIO_NO_SOPORTADO", e.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacion(e: MethodArgumentNotValidException) = respuesta(
        HttpStatus.BAD_REQUEST, "DATOS_INVALIDOS",
        e.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" },
    )

    @ExceptionHandler(HttpMessageNotReadableException::class, MethodArgumentTypeMismatchException::class)
    fun formato(e: Exception) = respuesta(HttpStatus.BAD_REQUEST, "FORMATO_INVALIDO", "El cuerpo o los parámetros no tienen el formato esperado")

    @ExceptionHandler(Exception::class)
    fun inesperado(e: Exception): ResponseEntity<ErrorDto> {
        log.error("Error no controlado", e)
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Ocurrió un error inesperado")
    }

    private fun respuesta(status: HttpStatus, codigo: String, mensaje: String?) =
        ResponseEntity.status(status).body(ErrorDto(codigo, mensaje ?: codigo))
}

@Configuration
class ConfiguracionOpenApi {
    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(Info().title("API Mi Renta").version("v1").description("Backend del monolito modular hexagonal de Mi Renta"))
        .components(
            Components().addSecuritySchemes(
                "firebase",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                    .description("ID token de Firebase. En modo demo, cualquier uid de prueba, por ejemplo usuario-demo"),
            )
        )
}

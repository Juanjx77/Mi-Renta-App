package com.mirenta

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

/** Prueba de punta a punta (HTTP → caso de uso → JPA) con H2 en modo PostgreSQL y la misma migración Flyway. */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:mirenta;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "mirenta.auth.modo=demo",
    ]
)
class ApiIntegracionTest(@Autowired val mvc: MockMvc) {
    private val auth = "Bearer usuario-prueba"

    @Test
    fun `sin token responde 401`() {
        mvc.get("/api/v1/perfil").andExpect { status { isUnauthorized() }; jsonPath("$.codigo") { value("NO_AUTENTICADO") } }
    }

    @Test
    fun `perfil se crea y se consulta`() {
        mvc.get("/api/v1/perfil") { header("Authorization", "Bearer otro-usuario") }.andExpect { status { isNotFound() } }
        mvc.put("/api/v1/perfil") {
            header("Authorization", auth)
            contentType = MediaType.APPLICATION_JSON
            content = """{"nombre":"Ana Pérez","numeroDocumento":"1098765432"}"""
        }.andExpect { status { isOk() } }
        mvc.get("/api/v1/perfil") { header("Authorization", auth) }
            .andExpect { status { isOk() }; jsonPath("$.nombre") { value("Ana Pérez") } }
    }

    @Test
    fun `declaracion se guarda, se calcula y detecta conflictos de version`() {
        val cuerpo = { version: String -> """{"version":$version,"entradas":{"salario":120000000,"aportes":10000000,"retenciones":5000000}}""" }
        val token = "Bearer usuario-declaracion"
        mvc.put("/api/v1/declaraciones/2025") {
            header("Authorization", token); contentType = MediaType.APPLICATION_JSON; content = cuerpo("null")
        }.andExpect { status { isOk() }; jsonPath("$.version") { value(0) }; jsonPath("$.resultado.exenta") { value(27_500_000.0) } }

        mvc.put("/api/v1/declaraciones/2025") {
            header("Authorization", token); contentType = MediaType.APPLICATION_JSON; content = cuerpo("0")
        }.andExpect { status { isOk() }; jsonPath("$.version") { value(1) } }

        mvc.put("/api/v1/declaraciones/2025") {
            header("Authorization", token); contentType = MediaType.APPLICATION_JSON; content = cuerpo("0")
        }.andExpect { status { isConflict() }; jsonPath("$.codigo") { value("CONFLICTO_VERSION") } }

        mvc.get("/api/v1/declaraciones/2025") { header("Authorization", token) }
            .andExpect { status { isOk() }; jsonPath("$.entradas.salario") { value(120000000) } }
    }

    @Test
    fun `simulacion, reglas y validaciones`() {
        mvc.post("/api/v1/declaraciones/2025/simulacion") {
            header("Authorization", auth); contentType = MediaType.APPLICATION_JSON; content = """{"salario":30000000}"""
        }.andExpect { status { isOk() }; jsonPath("$.impuesto") { value(0.0) } }

        mvc.post("/api/v1/declaraciones/2025/simulacion") {
            header("Authorization", auth); contentType = MediaType.APPLICATION_JSON; content = """{"salario":-5}"""
        }.andExpect { status { isBadRequest() }; jsonPath("$.codigo") { value("DATOS_INVALIDOS") } }

        mvc.get("/api/v1/reglas/2025") { header("Authorization", auth) }
            .andExpect { status { isOk() }; jsonPath("$.uvt") { value(49799.0) } }
        mvc.get("/api/v1/reglas/1999") { header("Authorization", auth) }.andExpect { status { isNotFound() } }
    }
}

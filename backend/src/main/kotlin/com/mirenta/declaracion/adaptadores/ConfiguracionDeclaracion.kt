package com.mirenta.declaracion.adaptadores

import com.mirenta.declaracion.aplicacion.ConsultarDeclaracion
import com.mirenta.declaracion.aplicacion.ConsultarReglas
import com.mirenta.declaracion.aplicacion.DeclaracionRepository
import com.mirenta.declaracion.aplicacion.GuardarDeclaracion
import com.mirenta.declaracion.aplicacion.SimularDeclaracion
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Los casos de uso no conocen Spring: se ensamblan aquí con el adaptador concreto (inyección por constructor). */
@Configuration
class ConfiguracionDeclaracion {
    @Bean fun consultarDeclaracion(repo: DeclaracionRepository) = ConsultarDeclaracion(repo)
    @Bean fun guardarDeclaracion(repo: DeclaracionRepository) = GuardarDeclaracion(repo)
    @Bean fun simularDeclaracion() = SimularDeclaracion()
    @Bean fun consultarReglas() = ConsultarReglas()
}

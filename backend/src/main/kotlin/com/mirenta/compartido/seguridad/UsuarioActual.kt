package com.mirenta.compartido.seguridad

import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

/** Da a los controladores el uid que dejó el filtro de autenticación. Los demás contextos solo conocen este id. */
@Component
class UsuarioActual {
    fun id(): String =
        RequestContextHolder.currentRequestAttributes().getAttribute(ATRIBUTO, RequestAttributes.SCOPE_REQUEST) as? String
            ?: throw IllegalStateException("Petición sin usuario autenticado")

    companion object {
        const val ATRIBUTO = "mirenta.usuarioId"
    }
}

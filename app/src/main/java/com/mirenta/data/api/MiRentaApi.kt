package com.mirenta.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MiRentaApi {
    @GET("api/v1/perfil")
    suspend fun obtenerPerfil(): Response<PerfilDto>

    @PUT("api/v1/perfil")
    suspend fun guardarPerfil(@Body perfil: PerfilDto): Response<PerfilDto>

    @GET("api/v1/declaraciones/{anio}")
    suspend fun obtenerDeclaracion(@Path("anio") anio: Int): Response<DeclaracionDto>

    @PUT("api/v1/declaraciones/{anio}")
    suspend fun guardarDeclaracion(
        @Path("anio") anio: Int,
        @Body cuerpo: GuardarDeclaracionRequest,
    ): Response<DeclaracionDto>

    @POST("api/v1/declaraciones/{anio}/simulacion")
    suspend fun simular(@Path("anio") anio: Int, @Body entradas: EntradasDto): Response<ResultadoDto>

    @GET("api/v1/reglas/{anio}")
    suspend fun obtenerReglas(@Path("anio") anio: Int): Response<ReglasDto>
}

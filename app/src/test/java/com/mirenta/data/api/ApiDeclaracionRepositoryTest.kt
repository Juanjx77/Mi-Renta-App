package com.mirenta.data.api

import com.google.gson.GsonBuilder
import com.mirenta.data.ConflictoVersionException
import com.mirenta.data.SesionVencidaException
import com.mirenta.domain.Entradas
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ApiDeclaracionRepositoryTest {

    private val gson = GsonBuilder().serializeNulls().create()

    private fun <T> respuestaError(codigo: Int): Response<T> =
        Response.error(codigo, """{"codigo":"X","mensaje":"m"}""".toResponseBody("application/json".toMediaType()))

    private class ApiFalsa : MiRentaApi {
        var respuestaGet: Response<DeclaracionDto> = Response.success(DeclaracionDto())
        var respuestaPut: (GuardarDeclaracionRequest) -> Response<DeclaracionDto> = { Response.success(DeclaracionDto()) }
        val versionesEnviadas = mutableListOf<Long?>()

        override suspend fun obtenerPerfil(): Response<PerfilDto> = Response.success(PerfilDto())
        override suspend fun guardarPerfil(perfil: PerfilDto): Response<PerfilDto> = Response.success(perfil)
        override suspend fun obtenerDeclaracion(anio: Int): Response<DeclaracionDto> = respuestaGet
        override suspend fun guardarDeclaracion(anio: Int, cuerpo: GuardarDeclaracionRequest): Response<DeclaracionDto> {
            versionesEnviadas += cuerpo.version
            return respuestaPut(cuerpo)
        }
        override suspend fun simular(anio: Int, entradas: EntradasDto): Response<ResultadoDto> = Response.success(ResultadoDto())
        override suspend fun obtenerReglas(anio: Int): Response<ReglasDto> = Response.success(ReglasDto())
    }

    @Test
    fun primeraVezSinDeclaracionEnviaVersionNullYLuegoLaDevuelta() = runBlocking {
        val api = ApiFalsa()
        api.respuestaGet = respuestaError(404)
        api.respuestaPut = { Response.success(DeclaracionDto(version = it.version?.plus(1) ?: 0L, entradas = it.entradas)) }
        val repo = ApiDeclaracionRepository(api, gson)

        assertNull(repo.cargar("uid"))
        repo.guardar("uid", Entradas(salario = 1))
        repo.guardar("uid", Entradas(salario = 2))

        assertEquals(listOf<Long?>(null, 0L), api.versionesEnviadas)
    }

    @Test
    fun usaLaVersionDelUltimoGet() = runBlocking {
        val api = ApiFalsa()
        api.respuestaGet = Response.success(DeclaracionDto(version = 7, entradas = EntradasDto(salario = 5, seccionesGuardadas = listOf("Ingresos"))))
        val repo = ApiDeclaracionRepository(api, gson)

        val cargadas = repo.cargar("uid")
        repo.guardar("uid", Entradas())

        assertEquals(Entradas(salario = 5, seccionesGuardadas = setOf("Ingresos")), cargadas)
        assertEquals(listOf<Long?>(7L), api.versionesEnviadas)
    }

    @Test(expected = ConflictoVersionException::class)
    fun conflictoDeVersionLanzaExcepcion() = runBlocking {
        val api = ApiFalsa()
        api.respuestaPut = { respuestaError(409) }
        ApiDeclaracionRepository(api, gson).guardar("uid", Entradas())
    }

    @Test(expected = SesionVencidaException::class)
    fun tokenInvalidoLanzaSesionVencida() = runBlocking {
        val api = ApiFalsa()
        api.respuestaGet = respuestaError(401)
        ApiDeclaracionRepository(api, gson).cargar("uid")
        Unit
    }

    @Test
    fun elCuerpoDelPutLlevaVersionNullExplicita() {
        val json = gson.toJson(GuardarDeclaracionRequest(version = null, entradas = EntradasDto()))
        assertTrue(json.startsWith("""{"version":null,"entradas":{"""))
    }
}

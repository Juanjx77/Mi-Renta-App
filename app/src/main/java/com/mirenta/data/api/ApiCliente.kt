package com.mirenta.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mirenta.BuildConfig
import com.mirenta.data.ConflictoVersionException
import com.mirenta.data.ErrorServidorException
import com.mirenta.data.SesionVencidaException
import com.mirenta.data.SinConexionException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class TokenInterceptor(private val obtenerToken: suspend () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = runBlocking {
            withTimeoutOrNull(10_000) { runCatching { obtenerToken() }.getOrNull() }
        }
        val peticion = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder().header("Authorization", "Bearer $token").build()
        }
        return chain.proceed(peticion)
    }
}

class ApiCliente(urlBase: String, obtenerToken: suspend () -> String?) {
    val gson: Gson = GsonBuilder().serializeNulls().create()

    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(TokenInterceptor(obtenerToken))
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
        }
        .build()

    val api: MiRentaApi = Retrofit.Builder()
        .baseUrl(if (urlBase.endsWith("/")) urlBase else "$urlBase/")
        .client(http)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(MiRentaApi::class.java)
}

suspend fun <T> ejecutarLlamada(gson: Gson, llamada: suspend () -> Response<T>): T? {
    val respuesta = try {
        llamada()
    } catch (e: IOException) {
        throw SinConexionException()
    }
    if (respuesta.isSuccessful) return respuesta.body()
    when (respuesta.code()) {
        401, 403 -> throw SesionVencidaException()
        404 -> return null
        409 -> throw ConflictoVersionException()
    }
    val mensaje = runCatching {
        gson.fromJson(respuesta.errorBody()?.string(), ErrorDto::class.java)?.mensaje
    }.getOrNull()
    throw ErrorServidorException(
        if (mensaje.isNullOrBlank()) "El servidor respondió con un error (${respuesta.code()})." else mensaje
    )
}

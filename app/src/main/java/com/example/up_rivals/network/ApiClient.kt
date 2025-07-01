package com.example.up_rivals.network // Asegúrate que el paquete sea el correcto

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // 1. La URL base de nuestro servidor en producción
    private const val BASE_URL = "https://up-rivals-ba-production.up.railway.app/"

    // 2. Esto es para poder ver en la consola qué enviamos y qué recibimos.
    // Es una herramienta de depuración increíblemente útil.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 3. Creamos el cliente que usará Retrofit por debajo
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // 4. Construimos la instancia de Retrofit uniendo todas las piezas
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // El convertidor de JSON
        .client(okHttpClient) // El cliente con el interceptor de logs
        .build()

    // 5. Esta es la propiedad que usaremos desde el resto de la app
    // para acceder a las funciones de nuestra ApiService (el "Menú")
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
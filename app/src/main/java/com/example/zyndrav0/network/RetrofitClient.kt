package com.example.zyndrav0.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient // Importante
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit // Importante

object RetrofitClient {

    private const val BASE_URL = "https://bot.dropptelecom.cl/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Aumentamos la paciencia a 60 o 90 segundos para evitar error de timeout
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Tiempo para establecer conexión
        .readTimeout(60, TimeUnit.SECONDS)    // Tiempo esperando que N8N responda
        .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo enviando datos
        .build()

    val instance: N8nApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        retrofit.create(N8nApiService::class.java)
    }
}
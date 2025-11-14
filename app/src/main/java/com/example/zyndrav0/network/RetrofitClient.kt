package com.example.zyndrav0.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://bot.dropptelecom.cl/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    val instance: N8nApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()) // Para respuestas de texto plano STRING
            .addConverterFactory(GsonConverterFactory.create(gson)) // Para JSON si es necesario aunque no usado
            .build()
        retrofit.create(N8nApiService::class.java)
    }
}
